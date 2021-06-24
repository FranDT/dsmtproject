package erlangconnector;

import com.ericsson.otp.erlang.*;
import common.Configuration;
import pojo.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ErlangConnector {

    private static final String serverNodeName;
    private static final String serverRegisteredName;
    private static final String clientNodeName;
    private static final AtomicInteger counter = new AtomicInteger(0); //shared counter
    private static OtpNode clientNode;  //initialized in constructor

    @Inject
    private static Configuration config;

    static {
        String cookie = config.getCookie();
        serverNodeName = config.getControlNodeServerName();
        serverRegisteredName = config.getControlNodeServerRegisteredName();
        clientNodeName = config.getAccessNodeName();
        try {
            if (cookie != "") {
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
        return new Response(null, 0);    }

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
