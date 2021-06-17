package middleware;

import java.io.Serializable;

public class User implements Serializable {
    String username;
    String password;

    public User(String username) {
        this(username, null);
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
