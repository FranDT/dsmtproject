package accesscontroller;

import pojo.Response;

import java.util.ArrayList;
import java.util.List;

public class AccessController {

    private static List<String> loggedUsersList = new ArrayList<>();

    public static void addActiveUser(String key){
        String[] div = key.split("-");
        loggedUsersList.add(div[0]);
    }

    public static Response delete(String key){
        if(loggedUsersList.size() == 0){
            return new Response(null, 1);
        }

        for(String s : loggedUsersList){
            if(s == key){
                loggedUsersList.remove(key);
                return new Response(null, 0);
            }
        }
        return new Response(null, 2);
    }
}
