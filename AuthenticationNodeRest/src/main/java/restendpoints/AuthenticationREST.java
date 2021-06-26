package restendpoints;

import authcontroller.AuthController;
import pojo.Request;
import pojo.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@ApplicationPath("rest")
@Path("/authentication")
public class AuthenticationREST extends Application {

    @GET
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("key") String key){
        if(!AuthController.isLaunched())
            return new Response(null, 1);
        return AuthController.checkCredentials(key);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response post(Request request){
        if(!AuthController.isLaunched())
            return new Response(null, 1);
        return AuthController.postNewUser(request.getKey(), request.getValue());
    }
}
