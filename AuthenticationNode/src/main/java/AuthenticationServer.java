import interfaces.DBFactory;
import interfaces.AuthenticationManagerInterface;
import leveldb.LevelDBFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthenticationServer {

    private final ServerSocket socket;
    private final DBFactory factory = new LevelDBFactory();
    private static final int DEFAULT_PORT = 33210;
    private final AuthenticationManagerInterface database;

    public AuthenticationServer() throws IOException{
        this(AuthenticationServer.DEFAULT_PORT);
    }

    public AuthenticationServer(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.database = factory.createUserManager();
        System.out.println("Server reachable on port " + port);
    }

    @Override
    protected void finalize() throws Throwable {
        this.socket.close();
    }

    public void start() {
        System.out.println("Server will now accept clients");
        while (true) {
            try (Socket socketClient = this.socket.accept()) {
                new ThreadServerDatabase(socketClient, this.database).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new AuthenticationServer().start();
    }
}
