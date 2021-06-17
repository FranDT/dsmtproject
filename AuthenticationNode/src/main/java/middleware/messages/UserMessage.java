package middleware.messages;

import middleware.User;

public abstract class UserMessage extends Message{
    protected User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
