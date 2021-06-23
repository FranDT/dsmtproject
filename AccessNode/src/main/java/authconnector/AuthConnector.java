package authconnector;

import accesscontroller.AccessController;
import org.glassfish.jersey.client.ClientConfig;
import pojo.Request;
import pojo.Response;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

public class AuthConnector {
    private static String urlBase;
    private static Client client;
    private static WebTarget webTarget;
    private static boolean launched = false;

    public static void launch(String url){
        urlBase = url;
        client = ClientBuilder.newClient(new ClientConfig().register(ClientResponseFilter.class));
    }

    public static boolean isLaunched() {return launched;}

    public static void close(){
        if(!launched)
            return;
        launched = false;
        client.close();
    }

    public static Response getAuthentication(String key){
        webTarget = client.target(urlBase).path("authentication").path(key);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        javax.ws.rs.core.Response response = invocationBuilder.get();
        Response myResponse = response.readEntity(Response.class);

        return myResponse;
    }

    public static Response postNewUser(Request request){
        webTarget = client.target(urlBase).path("authentication");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        javax.ws.rs.core.Response response = invocationBuilder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
        Response myResponse = response.readEntity(Response.class);

        if(myResponse.getStatus() == 0){
            AccessController.addActiveUser(request.getKey());
        }

        return myResponse;
    }
}
