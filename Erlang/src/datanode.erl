-module(datanode).
-export([start/0, join/2, receiveHB/1, sendHB/2, empty/0]).
-import(timer, [sleep/1]).

% spawning of the process that executes a data node
start() ->
	ControlNodeLocation = control_node@localhost,
	net_kernel:connect_node(ControlNodeLocation),

	HBreceiver = spawn(?MODULE, receiveHB, [ControlNodeLocation]),
	io:format("DataNode ~w: spawned receiver with pid ~w~n", [self(), HBreceiver]),

	DataPid = spawn(?MODULE, join, [ControlNodeLocation, HBreceiver]),
	io:format("DataNode pid is: ~p~n", [DataPid]),
	DataPid.

% just spawned node: send the join request to the control node
join(ControlNodeLocation, HBreceiver) ->
	{control_node, ControlNodeLocation} ! {self(), {join}, HBreceiver},
	receive
		% not the first node, so it needs a part of the keys from the successor
		{granted, NodeId, {SuccessorPid, SuccessorId, SuccessorHB}} ->
			io:format("DataNode ~w: received granted message, with successor~n", [self()]),
			Dict = ask_for_keys({SuccessorPid, SuccessorId, SuccessorHB}, NodeId, ControlNodeLocation),
			% {NodeId, Dict},
			NewHBsender = spawn(?MODULE, sendHB, [ControlNodeLocation, {SuccessorPid, SuccessorId, SuccessorHB}]),
			io:format("DataNode ~w: spawned sender with pid ~w~n", [self(), NewHBsender]),
			loop(Dict, NodeId, ControlNodeLocation, {SuccessorPid, SuccessorId, SuccessorHB}, NewHBsender);
		% first node in the key space
		{granted, NodeId} ->
			io:format("DataNode ~w: received granted message, with successor~n", [self()]),
			% ensure to send join completed in this case, otherwise is sent at the ask_for_keys
			{control_node, ControlNodeLocation} ! {self(), {joinCompleted}},
			% {NodeId, dict.new(), SuccessorPid}
			Placeholder = spawn(?MODULE, empty, []),
			loop(dict:new(), NodeId, ControlNodeLocation, NodeId, Placeholder);
		_ ->
			io:format("DataNode ~w: joining unexpected termination~n", [self()])
	end.

loop(Dict, NodeId, ControlNodeLocation, Successor, HBsender) ->
	io:format("Data Node ~w: loop -> Dict length: ~w~n",[self(), length(dict:to_list(Dict))]),
	receive
		{successor, NewSuccessor} ->
			exit(HBsender, kill),
			NewHBsender = spawn(?MODULE, sendHB, [ControlNodeLocation, NewSuccessor]),
			io:format("DataNode ~w: spawned sender with pid ~w~n", [self(), NewHBsender]),
			loop(Dict, NodeId, ControlNodeLocation, NewSuccessor, NewHBsender);
			% successor that has to send keys to a new joining node
		{NewPredecessorPid, {ask, NewPredecessorId}} ->
			io:format("Data Node ~w: predecessor is asking for keys~n", [self()]),
			OldKeysList = send_keys(NewPredecessorPid, NewPredecessorId, NodeId, Dict),
			NewDict = delete_old_keys(OldKeysList, dict:to_list(Dict)),
			loop(NewDict, NodeId, ControlNodeLocation, Successor, HBsender);
		
		% Client ops
		{ReqId, AccessNode, {insert, Key, Value}}  ->
			io:format("Data Node ~w: insert request arrived~n", [self()]),
			NewDict = insert_key_value(ReqId, AccessNode, Dict, Key, Value),
			loop(NewDict, NodeId, ControlNodeLocation, Successor, HBsender);
		{ReqId, AccessNode, {get, Key}} ->
			io:format("Data Node ~w: get request arrived~n", [self()]),
			get_value_from_key(ReqId, AccessNode, Dict, Key),
			loop(Dict, NodeId, ControlNodeLocation, Successor, HBsender);
		{ReqId, AccessNode, {update, Key, Value}} ->
			io:format("Data Node ~w: update request arrived~n", [self()]),
			NewDict = update_key_value(ReqId, AccessNode, Dict, Key, Value),
			loop(NewDict, NodeId, ControlNodeLocation, Successor, HBsender);
		{ReqId, AccessNode, {delete, Key}} ->
			io:format("Data Node ~w: delete request arrived~n", [self()]),
			NewDict = delete_value_from_key(ReqId, AccessNode, Dict, Key),
			loop(NewDict, NodeId, ControlNodeLocation, Successor, HBsender);

		{leave} ->
			io:format("Data Node ~w: leave request arrived~n", [self()]),
			Granted = leave(Dict, ControlNodeLocation, Successor),
			if
				Granted == true ->
					io:format("Data Node ~w: leaving...~n", [self()]);
				% if leaving was not granted, data node is in the loop again
				Granted == false ->
					loop(Dict, NodeId, ControlNodeLocation, Successor, HBsender)
			end;
		{insertFromLeaving, RecvList} ->
			io:format("Data Node ~w: received list from leaving node, length is: ~p~n", [self(), length(RecvList)]),
			RecvDict = dict:from_list(RecvList),
			NewDict = dict:merge(fun({_, Y, _}) -> Y end, Dict, RecvDict),
			loop(NewDict, NodeId, ControlNodeLocation, Successor, HBsender);
		{terminate} ->
			io:format("Data Node ~w: trusted termination~n", [self()]);
		WrongMessage ->
			io:format("Data Node ~w: Wrong message ~p~n", [self(), WrongMessage]),
			loop(Dict, NodeId, ControlNodeLocation, Successor, HBsender)
	end.

ask_for_keys({SuccessorPid, _, _}, NewNodeId, ControlNodeLocation) ->
	SuccessorPid ! {self(), {ask, NewNodeId}},
	receive
		% receive dictionary and say it to the control node
		{send, NewDict} ->
			{control_node, ControlNodeLocation} ! {self(), {joinCompleted}},
			NewDict
	end.

% function to send a part of the key-value pairs to a joining predecessor
send_keys(NewPredecessorPid, NewPredecessorId, NodeId, Dict) ->
	% work with lists, easier!
	List = dict:to_list(Dict),

	Filtered = lists:filter(fun({X,_}) -> X =< NewPredecessorId end, List),
	ToSend = lists:append(lists:filter(fun({X,_}) -> X > NodeId end, List), Filtered), % also check keys greater than me for the minimum server nodeid

	% convert the list into a dictionary and send to the new data node
	DictToSend = dict:from_list(ToSend),
	NewPredecessorPid ! {send, DictToSend},
	ToSend.

% function to delete old keys, just sent, from a node
delete_old_keys(OldKeysList, DictList) ->
	% receive the ack from the control node
	receive
		{deleteOldKeys} ->
			% work with lists, easier!
			NewList = lists:subtract(DictList, OldKeysList),
			% convert the list into a dictionary and return it
			dict:from_list(NewList)
		after 1000000 ->
			io:format("Data Node ~w: I'm forced to delete sent keys, control node not responding~n", [self()]),
			NewList = lists:subtract(DictList, OldKeysList),
			dict:from_list(NewList)
	end.

leave(Dict, ControlNodeLocation, {SuccessorPid, _, _}) ->
	{control_node, ControlNodeLocation} ! {self(), {leave}},
	receive
		{granted} ->
			if
				SuccessorPid =/= self() ->
					SuccessorPid ! {insertFromLeaving, dict:to_list(Dict)}
			end,
			{control_node, ControlNodeLocation} ! {self(), {leaveCompleted}},
			true;
		{notgranted, Reason} ->
			io:format("Data Node ~w: Impossible to leave. Reason: ~s~n", [self(), Reason]),
			false
		after 10000000 ->
			io:format("Data Node ~w: Impossible to leave. Reason: control node not responding~n", [self()]),
			false
	end.

% ------------------------------------ CLIENT OPS --------------------------------------------------
insert_key_value(ReqId, AccessNode, Dict, Key, Value) ->
	Founded = dict:find(Key, Dict),
	if
		% I found an already existing value for that key
		Founded =/= error ->
			AccessNode ! {ReqId, 4},
			Dict;
		% Not found, I can insert now the key-value pair
		true ->
			NewDict = dict:store(Key, Value, Dict),
			AccessNode ! {ReqId, 0},
			NewDict
	end.

get_value_from_key(ReqId, AccessNode, Dict, Key) ->
	Founded = dict:find(Key, Dict),
	if
		Founded == error ->
			AccessNode ! {ReqId, "Not found"};
		true ->
			{_, Value} = Founded,
			AccessNode ! {ReqId, Value}
	end.

update_key_value(ReqId, AccessNode, Dict, Key, Value) ->
	Founded = dict:find(Key, Dict),
	if
		Founded == error ->
			AccessNode ! {ReqId, 4};
		true ->
			NewDict = dict:update(Key, fun (_) -> Value end, Dict),
			AccessNode ! {ReqId, 0},
			NewDict
	end.

delete_value_from_key(ReqId, AccessNode, Dict, Key) ->
	Founded = dict:find(Key, Dict),
	if
		Founded == error ->
			AccessNode ! {ReqId, 2},
			Dict;
		true ->
			NewDict = dict:erase(Key, Dict),
			AccessNode ! {ReqId, 0},
			NewDict
	end.
% --------------------------------------------------------------------------------------------------

receiveHB(CNL) ->
	receive
		{NodeBack, heartbeat} ->
			io:format("DataNode ~w: received HB from ~w~n", [self(), NodeBack]),
			NodeBack ! {node_ok, self()},
			receiveHB(CNL)
	end.

sendHB(CNL, {SuccessorPid, SuccessorId, SuccessorHB}) ->
	SuccessorHB ! {self(), heartbeat},
	receive
		{node_ok, SenderHB} ->
			io:format("DataNode ~w: received node_ok with senderHB = ~w~n", [self(), SenderHB]),
			timer:sleep(10000),
			sendHB(CNL, {SuccessorPid, SuccessorId, SuccessorHB})
		after 20000 ->
			io:format("DataNode ~w: timer expired, alerting controlnode~n", [self()]),
			{control_node, CNL} ! {crashed, SuccessorId}
	end.

empty() ->
	empty().