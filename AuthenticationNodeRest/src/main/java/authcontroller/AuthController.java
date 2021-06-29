package authcontroller;

import leveldb.AuthenticationManagerInterface;
import leveldb.LevelDBAuthenticationManager;
import pojo.Response;

public class AuthController {

    private static AuthenticationManagerInterface manager = LevelDBAuthenticationManager.getDB();
    private static boolean launched = true;

    public static void launch(){
        manager = LevelDBAuthenticationManager.getDB();
        launched = true;
    }

    public static boolean isLaunched(){return launched;}

    public static void close(){
        manager.close();
    }

    public static Response checkCredentials(String key){
        String[] parts = key.split("-");
        if (parts.length != 2) {
            return new Response("Key received is not username-password", 2);
        }
        String username = parts[0];
        String password = parts[1];
        if(!manager.isPasswordCorrect(username, password))
            return new Response("Authentication failed", 2);
        return new Response("Authentication correct", 0);
    }

    public static Response postNewUser(String username, String password){
        if(!manager.insertUser(username, password))
            return new Response("User already present", 1);
        return new Response("User inserted", 0);
    }

}
