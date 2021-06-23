package authentication;

public class AuthenticationFactory {
    private AuthenticationFactory(){ }
    private static AuthenticationManager authenticationManagerInstance = null;

    public static AuthenticationManager CreateAuthenticationManager(){
        if(authenticationManagerInstance == null){
            authenticationManagerInstance = new AuthenticationManagerImplementation();
        }
        return authenticationManagerInstance;
    }
}