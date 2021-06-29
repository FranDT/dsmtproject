package erlangconnector;

import com.ericsson.otp.erlang.*;
import common.Configuration;
import pojo.Response;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ErlangConnector {

    private static final String serverNodeName;
    private static final String serverRegisteredName;
    private static final String clientNodeName;
    private static final AtomicInteger nextRequestId = new AtomicInteger(0); //shared counter
    private static boolean launched = false;
    private static OtpNode clientNode;  //initialized in constructor
    private static OtpMbox mbox;

    static {
        long randomNum = ThreadLocalRandom.current().nextLong();
        String cookie = Configuration.getCookie();
        serverNodeName = Configuration.getControlNodeServerName();
        serverRegisteredName = Configuration.getControlNodeServerRegisteredName();
        clientNodeName = Configuration.getAccessNodeName() + Long.toString(randomNum) + "@localhost";
        mbox = clientNode.createMbox("mbox_access" + Long.toString(randomNum));
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
        launched = true;
    }

    public static Response deleteByKey(String key) {
        // { RequestId, PID, {delete, key} }
        OtpErlangInt requestId = new OtpErlangInt(nextRequestId.getAndIncrement());
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
        return launched;
    }

    public static Response insert(String key, String value) {
        OtpErlangInt requestId = new OtpErlangInt(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("insert");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangString valueOperation = new OtpErlangString(value);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation, valueOperation});
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

    public static Response updateFile(String key, String value) {
        OtpErlangInt requestId = new OtpErlangInt(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("update");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangString valueOperation = new OtpErlangString(value);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation, valueOperation});
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

    public static Response getByKey(String key) {
        OtpErlangInt requestId = new OtpErlangInt(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("insert");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        String data = null;
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);
        try {
            OtpErlangObject msg = mbox.receive();
            OtpErlangTuple msgTuple = (OtpErlangTuple) msg;
            OtpErlangInt requestIdResponse = (OtpErlangInt) msgTuple.elementAt(0);
            //TODO: This check should return always true. Remove if necessary
            if (requestIdResponse.equals(requestId)) {
                OtpErlangString dataResponse = (OtpErlangString) msgTuple.elementAt(1);
                data = dataResponse.stringValue();
                return new Response(data, 0);
            }
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }

    public static Response getFileList() {
        OtpErlangInt requestId = new OtpErlangInt(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("getFileList");
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        String data = null;
        mbox.send(serverRegisteredName, serverNodeName, reqMsg);
        try {
            OtpErlangObject msg = mbox.receive();
            OtpErlangTuple msgTuple = (OtpErlangTuple) msg;
            OtpErlangInt requestIdResponse = (OtpErlangInt) msgTuple.elementAt(0);
            //TODO: This check should return always true. Remove if necessary
            if (requestIdResponse.equals(requestId)) {
                OtpErlangString dataResponse = (OtpErlangString) msgTuple.elementAt(1);
                data = dataResponse.stringValue();
                return new Response(data, 0);
            }
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }
}
