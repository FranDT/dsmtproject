package accesscontroller;

import pojo.Response;

import java.util.concurrent.ConcurrentHashMap;

public class AccessController {

    private static ConcurrentHashMap.KeySetView<String, Boolean> loggedUsersSet =
            new ConcurrentHashMap<String, Boolean>().keySet(true);

    public static void addActiveUser(String key){
        String[] div = key.split("-");
        loggedUsersSet.add(div[0]);
    }

    public static Response delete(String key){
        //TODO: Forse il controllo se empty si può rimuovere
        if(loggedUsersSet.isEmpty()){
            return new Response(null, 1);
        }
        if (loggedUsersSet.contains(key)) {
            loggedUsersSet.remove(key);
            return new Response(null, 0);
        }
        return new Response(null, 2);
    }

    public static boolean isPresent(String key) {
        return loggedUsersSet
                .parallelStream()
                .anyMatch(k -> k.equals(key));
    }
}
