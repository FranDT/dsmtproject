package authentication;

import org.apache.commons.codec.digest.DigestUtils;
import restclient.RestClient;

public class AuthenticationManagerImplementation implements AuthenticationManager {

    private final String WRONG_CREDENTIALS = "Wrong username or password";
    private final String USERNAME_TAKEN = "This username is already taken";
    private final String CREDENTIALS_INVALID_CHARS = "The username and password must contain only letters, numbers, or \"_\"";
    private final String CONNECTION_REFUSED = "Cannot connect to the server";

    AuthenticationManagerImplementation(){ }

    @Override
    public AuthResult login(String username, String psw) {
        int checkResult = RestClient.getAuthentication(username, securePassword(psw));
        if(checkResult == 0){
            return new AuthResult(username, true, null);
        }
        else if(checkResult == 1){
            return new AuthResult(null, false, CONNECTION_REFUSED);
        }
        else {
            return new AuthResult(null, false, WRONG_CREDENTIALS);
        }
    }

    @Override
    public AuthResult register(String username, String psw) {
        String regex = "^[a-zA-Z0-9.]{3,}$";
        if(!username.matches(regex) || !psw.matches(regex)){
            return new AuthResult(null, false, CREDENTIALS_INVALID_CHARS);
        }
        int result = RestClient.postNewUser(username, securePassword(psw));
        if(result == 1){
            return new AuthResult(null, false, USERNAME_TAKEN);
        }
        else if(result == 2){
            return new AuthResult(null, false, WRONG_CREDENTIALS);
        }
        else if(result == 3){
            return new AuthResult(null, false, CONNECTION_REFUSED);
        }
        else{
            return new AuthResult(username, true, null);
        }
    }

    @Override
    public String securePassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
