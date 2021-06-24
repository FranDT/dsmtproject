package authconnector;

import accesscontroller.AccessController;
import common.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import pojo.Request;
import pojo.Response;

import javax.inject.Inject;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

public class AuthConnector {
    //TODO: Quale e' il senso di launched in questo contesto?
    private static boolean launched = true;
    @Inject
    private static Configuration config;

    public static boolean isLaunched() {return launched;}

    public static Response getAuthentication(String key){
        Client client = ClientBuilder.newClient(new ClientConfig().register(ClientResponseFilter.class));
        WebTarget webTarget = client.target(config.getUrlAuthNode()).path("authentication").path(key);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        javax.ws.rs.core.Response response = invocationBuilder.get();
        Response myResponse = response.readEntity(Response.class);
        client.close();
        return myResponse;
    }

    public static Response postNewUser(Request request){
        Client client = ClientBuilder.newClient(new ClientConfig().register(ClientResponseFilter.class));
        WebTarget webTarget = client.target(config.getUrlAuthNode()).path("authentication");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        javax.ws.rs.core.Response response = invocationBuilder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
        Response myResponse = response.readEntity(Response.class);

        if(myResponse.getStatus() == 0){
            AccessController.addActiveUser(request.getKey());
        }
        client.close();
        return myResponse;
    }
}
