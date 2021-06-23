package resources.api.impl;

import resources.api.UserApi;
import resources.model.ModelApiResponse;
import resources.model.User;

import leveldb.LevelDBAuthenticationManager;

/**
 * REST Auth
 *
 * <p>API for Authentication Node
 *
 */
public class UserApiServiceImpl implements UserApi {
    /**
     * Check if user exists
     *
     * It can be used to check whether a user exists or not
     *
     */
    public ModelApiResponse checkIfUserIsRegistered(String username) {
        // TODO: Implement...
        LevelDBAuthenticationManager db = LevelDBAuthenticationManager.getDB();
        ModelApiResponse response = new ModelApiResponse();
        if (db.isUserPresent(username)) {
            response.setCode(200);
            response.setMessage("Successful operation");
        }
        else {
            response.setCode(400);
            response.setMessage("Invalid username supplied");
        }
        response.setType("Check");
        return response;
    }
    
    /**
     * Create new user
     *
     * A new user is inserted into the database, if it doesn&#39;t exist
     *
     */
    public ModelApiResponse insertUser(User user) {
        // TODO: Implement...
        LevelDBAuthenticationManager db = LevelDBAuthenticationManager.getDB();
        ModelApiResponse response = new ModelApiResponse();
        if (db.insertUser(user.getUsername(), user.getPassword())) {
            response.setCode(200);
            response.setMessage("Successful operation");
        }
        else {
            response.setCode(400);
            response.setMessage("User already present");
        }
        response.setType("Registration");
        return response;
    }
    
    /**
     * Logs user into the system
     *
     * 
     *
     */
    public ModelApiResponse loginUser(User user) {
        // TODO: Implement...
        LevelDBAuthenticationManager db = LevelDBAuthenticationManager.getDB();
        ModelApiResponse response = new ModelApiResponse();
        if (db.isPasswordCorrect(user.getUsername(), user.getPassword())) {
            response.setCode(200);
            response.setMessage("Successful operation");
        }
        else {
            response.setCode(400);
            response.setMessage("Invalid username/password supplied");
        }
        response.setType("Login");
        return response;
    }
    
}

