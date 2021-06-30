package restendpoints;

import accesscontroller.AccessController;
import erlangconnector.ErlangConnector;
import pojo.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@Path("control")
public class ControlRest extends Application {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(){
        if(!ErlangConnector.isLaunched()) {
            return new Response(null, 1);
        }
        System.out.println("Erlang connector lanciato");
        // TODO: Conviene aggiungere anche lo username da controllare qui?

        return ErlangConnector.getFileList();
    }

    @DELETE
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("key") String key){
        if (AccessController.isPresent(key)) {
            return AccessController.delete(key);
        }
        return new Response(null, 2);
    }
}
