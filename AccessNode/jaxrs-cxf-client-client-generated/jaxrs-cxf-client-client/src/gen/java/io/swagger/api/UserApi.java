package io.swagger.api;

import io.swagger.model.ModelApiResponse;
import io.swagger.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;

/**
 * REST Auth
 *
 * <p>API for Authentication Node
 *
 */
@Path("/")
@Api(value = "/", description = "")
public interface UserApi  {

    /**
     * Check if user exists
     *
     * It can be used to check whether a user exists or not
     *
     */
    @GET
    @Path("/user/{username}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Check if user exists", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful operation", response = ModelApiResponse.class),
        @ApiResponse(code = 400, message = "Invalid username/password supplied", response = ModelApiResponse.class) })
    public ModelApiResponse checkIfUserIsRegistered(@PathParam("username") String username);

    /**
     * Create new user
     *
     * A new user is inserted into the database, if it doesn&#39;t exist
     *
     */
    @POST
    @Path("/user/register")
    @Produces({ "application/json" })
    @ApiOperation(value = "Create new user", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful operation", response = ModelApiResponse.class),
        @ApiResponse(code = 400, message = "User already present", response = ModelApiResponse.class) })
    public ModelApiResponse insertUser(User user);

    /**
     * Logs user into the system
     *
     * 
     *
     */
    @POST
    @Path("/user/login")
    @Produces({ "application/json" })
    @ApiOperation(value = "Logs user into the system", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful operation", response = ModelApiResponse.class),
        @ApiResponse(code = 400, message = "Invalid username/password supplied", response = ModelApiResponse.class) })
    public ModelApiResponse loginUser(User body);
}

