package erlangconnector;

import com.ericsson.otp.erlang.*;
import common.Configuration;
import pojo.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ErlangConnector {

    private static final String serverNodeName;
    private static final String serverRegisteredName;
    private static final String clientNodeName;
    private static final AtomicLong nextRequestId = new AtomicLong(0); //shared counter
    private static boolean launched = false;
    private static ConcurrentHashMap<Long, Exchanger<OtpErlangObject>> requests = new ConcurrentHashMap<>();
    private static OtpNode clientNode;  //initialized in constructor
    private static OtpMbox mbox;
    private static Thread dispatchingThread;

    static {
        long randomNum = ThreadLocalRandom.current().nextLong();
        String cookie = Configuration.getCookie();
        serverNodeName = Configuration.getControlNodeServerName();
        serverRegisteredName = Configuration.getControlNodeServerRegisteredName();
        clientNodeName = Configuration.getAccessNodeName() + Long.toString(randomNum) + "@localhost";
        try {
            if (!cookie.equals("")) {
                clientNode = new OtpNode(clientNodeName, cookie);
            }
            else {
                clientNode = new OtpNode(clientNodeName);
            }
            mbox = clientNode.createMbox("mbox_access" + Long.toString(randomNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
        launchDispatchingThread();
        launched = true;
    }

    private static void launchDispatchingThread(){
        dispatchingThread = new Thread( () -> {
           while(!Thread.interrupted()){
               try{
                   OtpErlangObject msg = mbox.receive();
                   OtpErlangTuple msgTuple = (OtpErlangTuple) msg;
                   OtpErlangLong requestIdResponse = (OtpErlangLong) msgTuple.elementAt(0);
                   Exchanger<OtpErlangObject> exchanger = requests.get(requestIdResponse.longValue());
                   if(exchanger != null){
                       exchanger.exchange(msgTuple.elementAt(1), 0, TimeUnit.MILLISECONDS);
                   }
               }
               catch (OtpErlangExit otpErlangExit) {
                   otpErlangExit.printStackTrace();
               } catch (OtpErlangDecodeException e) {
                   e.printStackTrace();
               } catch (Exception e) {
                   e.printStackTrace();
            }
           }
        });
        dispatchingThread.start();
    }

    public static Response deleteByKey(String key) {
        // { RequestId, PID, {delete, key} }
        OtpErlangLong requestId = new OtpErlangLong(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("delete");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        OtpErlangObject reply = null;

        try {
            Exchanger<OtpErlangObject> exchanger = new Exchanger<>();
            requests.put(requestId.longValue(), exchanger);
            mbox.send(serverRegisteredName, serverNodeName, reqMsg);

            reply = exchanger.exchange(reply, 5000, TimeUnit.MILLISECONDS);
            requests.remove(requestId);
            System.out.println(reply.getClass() + "\n" + reply.toString());
            if(reply instanceof OtpErlangLong){
                return new Response(null, (int) ((OtpErlangLong) reply).longValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }

    public static boolean isLaunched() {
        return launched;
    }

    public static Response insert(String key, String value) {
        OtpErlangLong requestId = new OtpErlangLong(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("insert");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangString valueOperation = new OtpErlangString(value);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation, valueOperation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        OtpErlangObject reply = null;

        try {
            Exchanger<OtpErlangObject> exchanger = new Exchanger<>();
            requests.put(requestId.longValue(), exchanger);
            mbox.send(serverRegisteredName, serverNodeName, reqMsg);

            reply = exchanger.exchange(reply, 12000, TimeUnit.MILLISECONDS);
            requests.remove(requestId);
            System.out.println(reply.getClass() + "\n" + reply.toString());
            if(reply instanceof OtpErlangLong){
                return new Response(null, (int) ((OtpErlangLong) reply).longValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }

    public static Response updateFile(String key, String value) {
        OtpErlangLong requestId = new OtpErlangLong(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("update");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangString valueOperation = new OtpErlangString(value);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation, valueOperation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        OtpErlangObject reply = null;

        try {
            Exchanger<OtpErlangObject> exchanger = new Exchanger<>();
            requests.put(requestId.longValue(), exchanger);
            mbox.send(serverRegisteredName, serverNodeName, reqMsg);

            reply = exchanger.exchange(reply, 12000, TimeUnit.MILLISECONDS);
            requests.remove(requestId);
            System.out.println(reply.getClass() + "\n" + reply.toString());
            if(reply instanceof OtpErlangLong){
                return new Response(null, (int) ((OtpErlangLong) reply).longValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }

    public static Response getByKey(String key) {
        OtpErlangLong requestId = new OtpErlangLong(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("get");
        OtpErlangString keyOperation = new OtpErlangString(key);
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation, keyOperation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        OtpErlangObject reply = null;

        try {
            Exchanger<OtpErlangObject> exchanger = new Exchanger<>();
            requests.put(requestId.longValue(), exchanger);
            mbox.send(serverRegisteredName, serverNodeName, reqMsg);

            reply = exchanger.exchange(reply, 10000, TimeUnit.MILLISECONDS);
            requests.remove(requestId);
            System.out.println(reply.getClass() + "\n" + reply.toString());
            if(reply instanceof OtpErlangString){
                return new Response(((OtpErlangString)reply).stringValue(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }

    public static Response getFileList() {
        OtpErlangLong requestId = new OtpErlangLong(nextRequestId.getAndIncrement());
        OtpErlangPid pid = mbox.self();
        OtpErlangAtom operation = new OtpErlangAtom("getFileList");
        OtpErlangTuple body = new OtpErlangTuple(new OtpErlangObject[]{operation});
        OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{requestId, pid, body});

        //sending out the request
        OtpErlangObject reply = null;

        try {
            Exchanger<OtpErlangObject> exchanger = new Exchanger<>();
            requests.put(requestId.longValue(), exchanger);
            mbox.send(serverRegisteredName, serverNodeName, reqMsg);

            reply = exchanger.exchange(reply, 10000, TimeUnit.MILLISECONDS);
            requests.remove(requestId);
            System.out.println(reply.getClass() + "\n" + reply.toString());
            if(reply instanceof OtpErlangList){
                String res = "";
                Iterator iter = ((OtpErlangList) reply).iterator();
                while(iter.hasNext()){
                    OtpErlangString elem = (OtpErlangString) iter.next();
                    res += elem.stringValue() + ";";
                }
                return new Response(res, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(null, 1);
    }
}
