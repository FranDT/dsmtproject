package resources.api;

import resources.model.ModelApiResponse;
import resources.model.User;

import javax.ws.rs.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.validation.Valid;

/**
 * REST Auth
 *
 * <p>API for Authentication Node
 *
 */
@Path("/authentication")
@Api(value = "/authentication", description = "")
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
    @ApiOperation(value = "Check if user exists", tags={ "user",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful operation", response = ModelApiResponse.class),
        @ApiResponse(code = 400, message = "Invalid username supplied", response = ModelApiResponse.class) })
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
    @ApiOperation(value = "Create new user", tags={ "user",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful operation", response = ModelApiResponse.class),
        @ApiResponse(code = 400, message = "User already present", response = ModelApiResponse.class) })
    public ModelApiResponse insertUser(@Valid User user);

    /**
     * Logs user into the system
     *
     * 
     *
     */
    @POST
    @Path("/user/login")
    @Produces({ "application/json" })
    @ApiOperation(value = "Logs user into the system", tags={ "user" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful operation", response = ModelApiResponse.class),
        @ApiResponse(code = 400, message = "Invalid username/password supplied", response = ModelApiResponse.class) })
    public ModelApiResponse loginUser(@Valid User body);
}

