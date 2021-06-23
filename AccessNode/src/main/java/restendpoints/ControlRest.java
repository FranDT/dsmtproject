package restendpoints;

import accesscontroller.AccessController;
import erlangconnector.ErlangConnector;
import pojo.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@ApplicationPath("rest")
@Path("control")
public class ControlRest extends Application {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(){
        if(!ErlangConnector.isLaunched())
            return new Response(null, 1);

        return ErlangConnector.getFileList();
    }

    @DELETE
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("key") String key){
        return AccessController.delete(key);
    }
}
