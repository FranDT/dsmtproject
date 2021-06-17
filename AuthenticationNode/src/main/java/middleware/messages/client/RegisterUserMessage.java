package middleware.messages.client;

import middleware.messages.OpCode;
import middleware.User;
import middleware.messages.UserMessage;

public class RegisterUserMessage extends UserMessage {

    public RegisterUserMessage(User user){
        this.opcode = OpCode.Register_User;
        this.user = user;
    }

}
