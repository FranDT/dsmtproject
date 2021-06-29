package restclient;

import context.LayoutManager;
import context.LayoutManagerFactory;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient extends Application {

    private static String urlBase;
    private static Client client;
    private static WebTarget webTarget;
    private static boolean launched = false;
    private static LayoutManager manager = LayoutManagerFactory.getManager();

    public static void launch(String url){
        urlBase = url;
        client = ClientBuilder.newClient(new ClientConfig().register(ClientResponseFilter.class));
    }

    public static boolean isLaunched() {return launched;}


    //TODO: impostare corretta chiusura dell'applicazione
    public static void close(){
        if(!launched)
            return;

        webTarget = client.target(urlBase).path("rest").path("control").path(manager.context.getAuthenticatedUser());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.delete();
        MyResponse myResponse = response.readEntity(MyResponse.class);
        if(myResponse.getStatus() != 0){
            return;
        }

        client.close();
        launched = false;
    }

    public static int getAuthentication(String username, String password){
        webTarget = client.target(urlBase).path("rest").path("authentication").path(username + "-" + password);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();
        MyResponse myResponse = response.readEntity(MyResponse.class);

        return myResponse.getStatus();
    }

    public static int postNewUser(String username, String password){
        webTarget = client.target(urlBase).path("rest").path("authentication");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        MyRequest myRequest = new MyRequest(username, password);

        Response response = invocationBuilder.post(Entity.entity(myRequest, MediaType.APPLICATION_JSON));
        MyResponse myResponse = response.readEntity(MyResponse.class);

        return myResponse.getStatus();
    }

    public static String getByKey(String key){
        webTarget = client.target(urlBase).path("rest").path("data").path(key);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();
        MyResponse myResponse = response.readEntity(MyResponse.class);

        if(myResponse.getData() == null){
            return "Cannot connect to the server";
        }

        return myResponse.getData();
    }

    public static int put(String key, String value){
        webTarget = client.target(urlBase).path("rest").path("data");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        MyRequest myRequest = new MyRequest(key, value);

        Response response = invocationBuilder.put(Entity.entity(myRequest, MediaType.APPLICATION_JSON));
        MyResponse myResponse = response.readEntity(MyResponse.class);

        return myResponse.getStatus();
    }

    public static int post(String key, String value){
        webTarget = client.target(urlBase).path("rest").path("data");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        MyRequest myRequest = new MyRequest(key, value);

        Response response = invocationBuilder.post(Entity.entity(myRequest, MediaType.APPLICATION_JSON));
        MyResponse myResponse = response.readEntity(MyResponse.class);

        return myResponse.getStatus();
    }

    public static int delete(String key){
        webTarget = client.target(urlBase).path("rest").path("data").path(key);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.delete();
        MyResponse myResponse = response.readEntity(MyResponse.class);

        return myResponse.getStatus();
    }

    public static String getList(){
        webTarget = client.target(urlBase).path("rest").path("control");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();
        MyResponse myResponse = response.readEntity(MyResponse.class);

        if(myResponse.getData() == null){
            return "Cannot connect to the server";
        }

        return myResponse.getData();
    }

    public static int deleteConnection(String key){
        webTarget = client.target(urlBase).path("rest").path("control").path(key);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.delete();
        MyResponse myResponse = response.readEntity(MyResponse.class);

        return myResponse.getStatus();
    }
}














