package authentication;

public class AuthResult {

    public String authenticated = null;
    public boolean success;
    public String errorMsg = null;

    public AuthResult(String authenticated, boolean success,  String errorMsg){
        this.success = success;
        if(success){
            this.authenticated = authenticated;
        }
        else{
            this.errorMsg = errorMsg;
        }
    }
}
