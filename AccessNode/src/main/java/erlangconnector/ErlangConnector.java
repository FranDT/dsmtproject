package erlangconnector;

import com.ericsson.otp.erlang.*;
import common.Configuration;
import pojo.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ErlangConnector {

    private static final String serverNodeName;
    private static final String serverRegisteredName;
    private static final String clientNodeName;
    private static final AtomicInteger counter = new AtomicInteger(0); //shared counter
    private static OtpNode clientNode;  //initialized in constructor

    static {
        String cookie = Configuration.getCookie();
        serverNodeName = Configuration.getControlNodeServerName();
        serverRegisteredName = Configuration.getControlNodeServerRegisteredName();
        clientNodeName = Configuration.getAccessNodeName();
        try {
            if (!cookie.equals("")) {
                clientNode = new OtpNode(clientNodeName, cookie);
            }
            else {
                clientNode = new OtpNode(clientNodeName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static OtpMbox createMbox(){
        return clientNode.createMbox("mbox_access_"+ counter.getAndIncrement());
    }

    public static Response deleteByKey(String key) {
        // { RequestId, PID, {delete, key} }
        final OtpMbox mbox = createMbox(); //one mailbox per task
        OtpErlangInt requestId = new OtpErlangInt(counter.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("delete");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        int status = 1;
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);
        try {
            OtpErlangObject msg = mbox.receive();
            OtpErlangTuple msgTuple = (OtpErlangTuple) msg;
            OtpErlangInt requestIdResponse = (OtpErlangInt) msgTuple.elementAt(0);
            //TODO: This check should return always true. Remove if necessary
            if (requestIdResponse.equals(requestId)) {
                OtpErlangInt statusResponse = (OtpErlangInt) msgTuple.elementAt(1);
                status = statusResponse.intValue();
            }
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, status);
    }

    public static boolean isLaunched() {
        final OtpMbox mbox = createMbox(); //one mailbox per task
        OtpErlangString num = new OtpErlangString("Sostituire");
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{mbox.self(), num});

        //sending out the request
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);

        //blocking receive operation
        OtpErlangObject msg = null;
        try {
            msg = mbox.receive();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        //getting the message content (a number)
        OtpErlangDouble curr_avg_erlang = (OtpErlangDouble) msg;  //it is supposed to be a tuple...
        return true;
    }

    public static Response insert(String key, String value) {
        final OtpMbox mbox = createMbox(); //one mailbox per task
        OtpErlangString num = new OtpErlangString("Sostituire");
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{mbox.self(), num});

        //sending out the request
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);

        //blocking receive operation
        OtpErlangObject msg = null;
        try {
            msg = mbox.receive();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        //getting the message content (a number)
        OtpErlangDouble curr_avg_erlang = (OtpErlangDouble) msg;  //it is supposed to be a tuple...
        return new Response(null, 0);
    }

    public static Response updateFile(String key, String value) {
        final OtpMbox mbox = createMbox(); //one mailbox per task
        OtpErlangString num = new OtpErlangString("Sostituire");
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{mbox.self(), num});

        //sending out the request
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);

        //blocking receive operation
        OtpErlangObject msg = null;
        try {
            msg = mbox.receive();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        //getting the message content (a number)
        OtpErlangDouble curr_avg_erlang = (OtpErlangDouble) msg;  //it is supposed to be a tuple...
        return new Response(null, 0);
    }

    public static Response getByKey(String key) {
        final OtpMbox mbox = createMbox(); //one mailbox per task
        OtpErlangString num = new OtpErlangString("Sostituire");
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{mbox.self(), num});

        //sending out the request
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);

        //blocking receive operation
        OtpErlangObject msg = null;
        try {
            msg = mbox.receive();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        //getting the message content (a number)
        OtpErlangDouble curr_avg_erlang = (OtpErlangDouble) msg;  //it is supposed to be a tuple...
        return new Response(null, 0);
    }

    public static Response getFileList() {
        final OtpMbox mbox = createMbox(); //one mailbox per task
        OtpErlangString num = new OtpErlangString("Sostituire");
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{mbox.self(), num});

        //sending out the request
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);

        //blocking receive operation
        OtpErlangObject msg = null;
        try {
            msg = mbox.receive();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        //getting the message content (a number)
        OtpErlangDouble curr_avg_erlang = (OtpErlangDouble) msg;  //it is supposed to be a tuple...
        return new Response(null, 0);
    }
}
