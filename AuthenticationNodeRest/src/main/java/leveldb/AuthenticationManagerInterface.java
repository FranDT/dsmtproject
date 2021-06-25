package leveldb;

public interface AuthenticationManagerInterface {
    boolean insertUser(String user, String value);
    boolean isUserPresent(String user);
    boolean isPasswordCorrect(String user, String value);
    void close();
}
