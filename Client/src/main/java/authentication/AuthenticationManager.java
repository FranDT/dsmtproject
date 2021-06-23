package authentication;

public interface AuthenticationManager {
    /**
     *
     * Searches for a user account with the specified username and, if it exists, checks if the password matches, returning
     * the result of the match.
     *
     * @param username String representing the username of the account the user wants to log into.
     * @param psw String representing the password connected to the username passed by the user.
     * @return The result of the authentication process in form of an AuthResult object.
     */
    AuthResult login(String username, String psw);

    /**
     *
     * Creates a new user account with the specified information passed. Return the result of the registration, that can
     * fail if the username has already been used.
     *
     * @param username String containing the username chose by the user to be identified with on the platform.
     * @param psw String containing the secret key chose by the user to login with.
     * @return The result of the registration process in form of an AuthResult object.
     */
    AuthResult register(String username, String psw);

    String securePassword(String password);
}
