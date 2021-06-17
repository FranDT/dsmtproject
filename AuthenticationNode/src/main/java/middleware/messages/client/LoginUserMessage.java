package middleware.messages.client;

import middleware.messages.OpCode;
import middleware.User;
import middleware.messages.UserMessage;

public class LoginUserMessage extends UserMessage {

    public LoginUserMessage(User user){
        this.opcode = OpCode.Login_User;
        this.user = user;
    }

}
