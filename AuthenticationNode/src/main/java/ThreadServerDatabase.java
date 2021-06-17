import interfaces.AuthenticationManagerInterface;
import middleware.*;
import middleware.messages.Message;
import middleware.messages.StatusCode;
import middleware.messages.client.*;
import middleware.messages.server.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ThreadServerDatabase extends Thread{
    private final Socket socket;
    private final AuthenticationManagerInterface database;
    private final ObjectOutputStream userOutputStream;
    private final ObjectInputStream userInputStream;

    public ThreadServerDatabase(Socket socketClient, AuthenticationManagerInterface database) throws IOException {
        this.socket = socketClient;
        this.database = database;
        this.userInputStream = new ObjectInputStream(this.socket.getInputStream());
        this.userOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
    }

    public void waitForMessages() throws Exception {
        while(true) {
            Message msg = receive();
            System.out.println("Received " + msg);
            switch (msg.getOpcode()){
                case Login_User:
                    LoginUserMessage msgl = (LoginUserMessage)msg;
                    User user_log = msgl.getUser();
                    if (this.database.isPasswordCorrect(user_log.getUsername(), user_log.getPassword())) {
                        send(new AnswerUserMessage(user_log, StatusCode.OK));
                    }
                    else{
                        send(new AnswerUserMessage(user_log, StatusCode.Error));
                    }
                    break;

                case Register_User:
                    RegisterUserMessage msgr = (RegisterUserMessage)msg;
                    User user_reg = msgr.getUser();
                    if (this.database.isUserPresent(user_reg.getUsername())) {
                        send(new AnswerUserMessage(user_reg, StatusCode.Error));
                    }
                    else {
                        this.database.insertUser(user_reg.getUsername(), user_reg.getPassword());
                        send(new AnswerUserMessage(user_reg, StatusCode.OK));
                    }
                    break;

                default:
                    send(new AnswerUserMessage(null, StatusCode.Error));
            }
        }
    }

    public void run() {
        try {
            waitForMessages();
        } catch (SocketException | EOFException exc) {
            System.out.println("Socket closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        return (Message) userInputStream.readObject();
    }

    public void send(Message message) throws IOException {
        userOutputStream.writeObject(message);
    }
}
