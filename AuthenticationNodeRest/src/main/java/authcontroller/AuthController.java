package authcontroller;

import leveldb.AuthenticationManagerInterface;
import leveldb.LevelDBAuthenticationManager;
import pojo.Response;

public class AuthController {

    private static AuthenticationManagerInterface manager;
    private static boolean launched = false;

    public static void launch(){
        manager = LevelDBAuthenticationManager.getDB();
        launched = true;
    }

    public static boolean isLaunched(){return launched;}

    public static void close(){
        manager.close();
    }

    public static Response checkCredentials(String key){
        String[] parts = key.split(":");
        String username = parts[0];
        String password = parts[1];
        if(!manager.isPasswordCorrect(username, password))
            return new Response(null, 2);
        return new Response(null, 0);
    }

    public static Response postNewUser(String username, String password){
        if(!manager.insertUser(username, password))
            return new Response(null, 1);
        return new Response(null, 0);
    }

}
