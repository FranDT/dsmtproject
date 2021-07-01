-module(controlnode).
-export([start/0, loop/2]).

start() ->
	io:format("Control Node pid is: ~p~n", [self()]),
	register(control_node, self()),
	loop([], []).

loop(Servers, List) ->
	receive
		% Data Node operations
		{DataNode, {join}, HBreceiver} ->
			io:format("Control Node: Received join request from ~w~n", [DataNode]),
			{NewServersAfterJoin, Successor, NewNodeId} = join(DataNode, Servers, HBreceiver),
			NewServers = manage_join(NewServersAfterJoin, Successor, {NewNodeId, DataNode, HBreceiver}),
			loop(NewServers, List);
		{DataNode, {leave}} ->
			io:format("Control Node: Received leave request from ~w~n", [DataNode]),
			{Successor, {_, PredecessorId, _}} = find_neighbors(Servers, DataNode),
			DataNode ! {granted},
			% wait for leaving confirmation
			NewServers = wait_leave_completed(DataNode, Servers),
			PredecessorId ! {successor, Successor},
			loop(NewServers, List);

		% Requests from Access Node
		{ReqId, AccessNode, {insert, Key, Value}} ->
			insert_key(ReqId, AccessNode, Key, Value, Servers),
			NewList = insert_list(List, Key),
			loop(Servers, NewList);
		{ReqId, AccessNode, {get, Key}} ->
			get_key(ReqId, AccessNode, Key, Servers),
			loop(Servers, List);
		{ReqId, AccessNode, {update, Key, Value}} ->
			update_key(ReqId, AccessNode, Key, Value, Servers),
			loop(Servers, List);
		{ReqId, AccessNode, {delete, Key}} ->
			delete_key(ReqId, AccessNode, Key, Servers),
			NewList = remove_list(List, Key),
			loop(Servers, NewList);
		{ReqId, AccessNode, {getFileList}} ->
			AccessNode ! {ReqId, List},
			loop(Servers, List);
		{terminate} ->
			io:format("Control Node: shutting down the system~n"),
			lists:foreach(fun({_, DataNode, _}) -> DataNode ! {terminate} end, Servers),
			io:format("Control Node: trusted termination ~n", []),
			{ok, terminated};

		{crashed, CrashedId} ->
			io:format("DataNode ~w crashed~n", [CrashedId]),
			[ToDelete] = lists:filter(
				fun({X, _, _}) ->
					X == CrashedId
				end,
				Servers),
			NewServers = lists:delete(ToDelete, Servers),
			{{SuccessorPid, SuccessorId, SuccessorHB}, {PredecessorId, PredecessorPid, _}} = find_neighbors(NewServers, CrashedId),

			Filtered = lists:filter(
				fun(X) ->
					NewX = binary:decode_unsigned(crypto:hash(sha256, X), big),
					(NewX =< CrashedId) and (NewX > PredecessorId)
				end,
				List),
			NewList = remove_recursive(Filtered, List),
			PredecessorPid ! {successor, {SuccessorPid, SuccessorId, SuccessorHB}},
			loop(NewServers, NewList);
		WrongMessage ->
			io:format("Control Node: wrong message ~p~n", [WrongMessage]),
			loop(Servers, List)
	end.

remove_recursive([], List) ->
	List;

remove_recursive([Elem | T], List) ->
	remove_recursive(T, lists:delete(Elem, List)).


join(DataNode, Servers, HBreceiver) ->
	NewNodeId = create_node_id(Servers),
	% add to the list of servers the new NodeId and its PID to let the server send it messages
	NewServersUnsorted = lists:append(Servers, [{NewNodeId, DataNode, HBreceiver}]),
	NewServers = lists:sort(fun({X, _, _}, {Y, _, _}) -> X < Y end, NewServersUnsorted),
	if
		length(NewServers) == 1 -> % first joining node
			DataNode ! {granted, NewNodeId},
			{NewServers, {DataNode, NewNodeId, HBreceiver}, NewNodeId};
		length(NewServers) =/= 1 ->
			{Successor, {_, PredecessorId, _}} = find_neighbors(Servers, NewNodeId),
			DataNode ! {granted, NewNodeId, Successor},
			PredecessorId ! {successor, {DataNode, NewNodeId, HBreceiver}}, % send to a node his new successor

			{NewServers, Successor, NewNodeId}
	end.

% also check special case: space key wrap
create_node_id(Servers) ->
	% create random list
	RandomList = [rand:uniform(10) || _ <- lists:seq(1, 1000)],
	NodeId = binary:decode_unsigned(crypto:hash(sha256, RandomList), big),
	% until a new unique NodeId is not generated, continue to generate new random IDs
	AlreadyExists = lists:any(fun({X, _, _}) -> X == NodeId end, Servers),
	if
		AlreadyExists == true ->	
			create_node_id(Servers);
		true ->
			NodeId
	end.

% find_successor is used both for servers and keys, because they share the same key-space
find_neighbors([H|T], NewNodeId) ->
	find_neighbors([H|T], NewNodeId, H, H).

find_neighbors([], _, {NodeId, DataNode, HBreceiver}, Preavious) ->
	{{DataNode, NodeId, HBreceiver}, Preavious}; % for sure is minimum, nobody has key higher than me

find_neighbors([{NodeId, DataNode, HBreceiver} | T], NewNodeId, MinNode, Preavious) ->
	if
		NodeId > NewNodeId -> 
			{{DataNode, NodeId, HBreceiver}, Preavious};
		true -> 
			find_neighbors(T, NewNodeId, MinNode, {NodeId, DataNode, HBreceiver})
	end.

% NewNodeId and DataNode are passed to this function to avoid keeping it into Servers if it doesn't send joinCompleted
manage_join(Servers, {SuccessorPid, _, _}, {NewNodeId, DataNode, HBreceiver}) ->
	receive
		% wait for this specific message. All the other messages are kept in the saved messages
		% queue, until a match occurs or timeout
		{DataNode, {joinCompleted}} ->
			if
				SuccessorPid =/= DataNode ->
					SuccessorPid ! {deleteOldKeys},
					Servers;
				true -> Servers
			end
	after 10000 ->
		io:format("Control Node: not receiving join completed from joining node~n"),
		NewServers = lists:delete({NewNodeId, DataNode, HBreceiver}, Servers),
		NewServers % avoid a joining node to block forever the server if it doesn't send joinCompleted
	end.


wait_leave_completed(DataNode, Servers) ->
	receive
		{DataNode, {leaveCompleted}} ->
			[ToDelete] = lists:filter(fun({_, Y, _}) -> Y == DataNode end, Servers),
			NewServers = lists:delete(ToDelete, Servers),
			NewServers
	after 10000 ->
		io:format("Control Node: not receiving leave completed from leaving node ~n"),
		[ToDelete] = lists:filter(fun({_, Y, _}) -> Y == DataNode end, Servers),
		NewServers = lists:delete(ToDelete, Servers),
		NewServers
	end.

% ------------------------------------ CLIENT OPS --------------------------------------------------
insert_key(ReqId, AccessNode, Key, Value, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {insert, HashedKey, Value}}.

get_key(ReqId, AccessNode, Key, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {get, HashedKey}}.

update_key(ReqId, AccessNode, Key, Value, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {update, HashedKey, Value}}.

delete_key(ReqId, AccessNode, Key, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {delete, HashedKey}}.

insert_list(List, Value) ->
	NewList = [Value|List],
	NewList.

remove_list(List, Value) ->
	NewList = [Y || Y <- List, Y =/= Value],
	NewList.
% --------------------------------------------------------------------------------------------------