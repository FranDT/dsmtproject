package context;

public class ApplicationContext {
    private String authenticatedUser = null;

    ApplicationContext(){}

    public String getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(String user){
        this.authenticatedUser = user;
    }


}
