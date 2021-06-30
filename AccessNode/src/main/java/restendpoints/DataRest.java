package restendpoints;

import accesscontroller.AccessController;
import erlangconnector.ErlangConnector;
import pojo.Request;
import pojo.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.stream.Collectors;

@Path("data")
public class DataRest extends Application {

    @GET
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("key") String key){
        if(!ErlangConnector.isLaunched()){
            return new Response(null, 1);
        }
        if (AccessController.isPresent(key.split("-")[0])) {
            String usernameAndFile = Arrays.stream(key.split("-")).skip(1).collect(Collectors.joining("-"));
            System.out.println("GET FILE " + usernameAndFile);
            return ErlangConnector.getByKey(usernameAndFile);
        }
        return new Response(null, 1);
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response put(Request request){
        String key = request.getKey();
        String value = request.getValue();
        if(key == null || key.isEmpty() || value == null || value.isEmpty())
            return new Response(null, 2);

        if(!ErlangConnector.isLaunched()){
            return new Response(null, 3);
        }
        if (AccessController.isPresent(key.split("-")[0])) {
            return ErlangConnector.updateFile(key, value);
        }
        return new Response(null, 1);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response post(Request request){
        String key = request.getKey();
        String value = request.getValue();
        if(key == null || key.isEmpty() || value == null || value.isEmpty())
            return new Response(null, 2);

        if(!ErlangConnector.isLaunched()){
            return new Response(null, 3);
        }

        if (AccessController.isPresent(key.split("-")[0])) {
            return ErlangConnector.insert(key, value);
        }
        return new Response(null, 1);

    }

    @DELETE
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("key") String key){
        if(!ErlangConnector.isLaunched()){
            return new Response(null, 1);
        }

        if (AccessController.isPresent(key.split("-")[0])) {
            return ErlangConnector.deleteByKey(key);
        }

        return new Response(null, 1);
    }
}
