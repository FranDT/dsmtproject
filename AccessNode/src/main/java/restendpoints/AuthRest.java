package restendpoints;

import authconnector.AuthConnector;
import pojo.Request;
import pojo.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;


@Path("authentication")
public class AuthRest extends Application {

    @GET
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("key") String key){
        if(!AuthConnector.isLaunched()){
            return new Response(null, 1);
        }

        return AuthConnector.getAuthentication(key);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response post(Request request){
        if(request.getKey() == null || request.getKey().isEmpty() || request.getValue() == null || request.getValue().isEmpty()){
            return new Response(null, 2);
        }

        if(!AuthConnector.isLaunched()){
            return new Response(null, 3);
        }

        return AuthConnector.postNewUser(request);
    }
}
