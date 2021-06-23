package restendpoints;

import erlangconnector.ErlangConnector;
import pojo.Request;
import pojo.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@ApplicationPath("rest")
@Path("data")
public class DataRest extends Application {

    @GET
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("key") String key){
        if(!ErlangConnector.isLaunched()){
            return new Response(null, 1);
        }

        return ErlangConnector.getByKey(key);
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

        return ErlangConnector.updateFile(key, value);
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

        return ErlangConnector.insert(key, value);
    }

    @DELETE
    @Path("/{key}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("key") String key){
        if(!ErlangConnector.isLaunched()){
            return new Response(null, 1);
        }

        return ErlangConnector.deleteByKey(key);
    }
}
