-module(controlnode).
-export([start/0, loop/2]).

start() ->
	io:format("Central Node pid is: ~p~n", [self()]),
	register(control_node, self()),
	loop([], []).

loop(Servers, List) ->
	receive
		% Data Node operations
		{DataNode, {join}} ->
			io:format("Central Node: Received join request from ~w~n", [DataNode]),
			{NewServersAfterJoin, Successor, NewNodeId} = join(DataNode, Servers),
			NewServers = manage_join(NewServersAfterJoin, Successor, {NewNodeId, DataNode}),
			loop(NewServers, List);
		{DataNode, {leave}} ->
			io:format("Central Node: Received leave request from ~w~n", [DataNode]),
			{Successor, {_, PredecessorId}} = find_neighbors(Servers, DataNode),
			DataNode ! {granted},
			% wait for leaving confirmation
			NewServers = wait_leave_completed(DataNode, Servers),
			PredecessorId ! {successor, Successor},
			loop(NewServers, List);

		% Requests from Access Node
		{ReqId, AccessNode, {insert, Key, Value}} ->
			io:format("Prima insert~n", []),
			insert_key(ReqId, AccessNode, Key, Value, Servers),
			NewList = insert_list(List, Key),
			io:format("Dopo insert~n", []),
			loop(Servers, NewList);
		{ReqId, AccessNode, {get, Key}} ->
			io:format("Prima get~n", []),
			get_key(ReqId, AccessNode, Key, Servers),
			io:format("Dopo get~n", []),
			loop(Servers, List);
		{ReqId, AccessNode, {update, Key, Value}} ->
			io:format("Prima update~n", []),
			update_key(ReqId, AccessNode, Key, Value, Servers),
			io:format("Dopo update~n", []),
			loop(Servers, List);
		{ReqId, AccessNode, {delete, Key}} ->
			io:format("Prima delete~n", []),
			delete_key(ReqId, AccessNode, Key, Servers),
			NewList = remove_list(List, Key),
			io:format("Dopo delete~n", []),
			loop(Servers, NewList);
		{ReqId, AccessNode, {getFileList}} ->
			io:format("Prima getFileList~n", []),
			AccessNode ! {ReqId, List},
			io:format("Dopo getFileList~n", []),
			loop(Servers, List);
		{terminate} ->
			io:format("Central Node: shutting down the system~n"),
			lists:foreach(fun({_, DataNode}) -> DataNode ! {terminate} end, Servers),
			io:format("Central Node: trusted termination ~n", []),
			{ok, terminated};
		WrongMessage ->
			io:format("Central Node: wrong message ~p~n", [WrongMessage]),
			loop(Servers, List)
	end.

join(DataNode, Servers) ->
	NewNodeId = create_node_id(Servers),
	% add to the list of servers the new NodeId and its PID to let the server send it messages
	NewServersUnsorted = lists:append(Servers, [{NewNodeId, DataNode}]),
	NewServers = lists:sort(fun({X, _}, {Y, _}) -> X < Y end, NewServersUnsorted),
	if
		length(NewServers) == 1 -> % first joining node
			DataNode ! {granted, NewNodeId},
			{NewServers, {DataNode, NewNodeId}, NewNodeId};
		length(NewServers) =/= 1 ->
			{Successor, {PredecessorPid, PredecessorId}} = find_neighbors(Servers, NewNodeId),
			io:format("Successor: ~w~n", [Successor]),
			io:format("PredecessorPid: ~w~n", [PredecessorId]),
			DataNode ! {granted, NewNodeId, Successor}, 
			PredecessorId ! {successor, Successor}, % send to a node his new successor
			{NewServers, Successor, NewNodeId}
	end.

% also check special case: space key wrap
create_node_id(Servers) ->
	% create random list
	RandomList = [rand:uniform(10) || _ <- lists:seq(1, 1000)],
	NodeId = binary:decode_unsigned(crypto:hash(sha256, RandomList), big),
	% until a new unique NodeId is not generated, continue to generate new random IDs
	AlreadyExists = lists:any(fun({X, _}) -> X == NodeId end, Servers),
	if
		AlreadyExists == true ->	
			create_node_id(Servers);
		true ->
			NodeId
	end.

% find_successor is used both for servers and keys, because they share the same key-space
find_neighbors([H|T], NewNodeId) ->
	find_neighbors([H|T], NewNodeId, H, H).

find_neighbors([], _, {NodeId, DataNode}, Preavious) ->
	{{DataNode, NodeId}, Preavious}; % for sure is minimum, nobody has key higher than me
find_neighbors([{NodeId, DataNode} | T], NewNodeId, MinNode, Preavious) ->
	if
		NodeId > NewNodeId -> 
			{{DataNode, NodeId}, Preavious};
		true -> 
			find_neighbors(T, NewNodeId, MinNode, {NodeId, DataNode})
	end.

% NewNodeId and DataNode are passed to this function to avoid keeping it into Servers if it doesn't send joinCompleted
manage_join(Servers, {SuccessorPid, _}, {NewNodeId, DataNode}) ->
	receive
		% wait for this specific message. All the other messages are kept in the saved messages
		% queue, until a match occurs or timeout
		{DataNode, {joinCompleted}} ->
			if
				SuccessorPid =/= NewNodeId ->
					SuccessorPid ! {deleteOldKeys},
					Servers;
				true -> Servers
			end
	after 10000 ->
		io:format("Central Node: not receiving join completed from joining node~n"),
		NewServers = lists:delete({NewNodeId, DataNode}, Servers),
		NewServers % avoid a joining node to block forever the server if it doesn't send joinCompleted
	end.


wait_leave_completed(DataNode, Servers) ->
	receive
		{DataNode, {leaveCompleted}} ->
			[ToDelete] = lists:filter(fun({_, Y}) -> Y == DataNode end, Servers),
			NewServers = lists:delete(ToDelete, Servers),
			NewServers
	after 10000 ->
		io:format("Central Node: not receiving leave completed from leaving node ~n"),
		[ToDelete] = lists:filter(fun({_, Y}) -> Y == DataNode end, Servers),
		NewServers = lists:delete(ToDelete, Servers),
		NewServers
	end.

% ------------------------------------ CLIENT OPS --------------------------------------------------
insert_key(ReqId, AccessNode, Key, Value, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {insert, HashedKey, Value}}.

get_key(ReqId, AccessNode, Key, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {get, HashedKey}}.

update_key(ReqId, AccessNode, Key, Value, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {update, HashedKey, Value}}.

delete_key(ReqId, AccessNode, Key, Servers) ->
	HashedKey = binary:decode_unsigned(crypto:hash(sha256, Key), big),
	{{SuccessorPid, _}, _} = find_neighbors(Servers, HashedKey),
	SuccessorPid ! {ReqId, AccessNode, {delete, HashedKey}}.

insert_list(List, Value) ->
	NewList = [Value|List],
	NewList.

remove_list(List, Value) ->
	NewList = [Y || Y <- List, Y =/= Value],
	NewList.
% --------------------------------------------------------------------------------------------------