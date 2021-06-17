package middleware.messages.server;

import middleware.messages.OpCode;
import middleware.messages.StatusCode;
import middleware.User;
import middleware.messages.UserMessage;

public class AnswerUserMessage extends UserMessage {
    protected StatusCode answer;

    public AnswerUserMessage(User user, StatusCode answer){
        this.opcode = OpCode.Answer_User;
        this.answer = answer;
        this.user = user;
    }

    public StatusCode getAnswer() {
        return answer;
    }

    public void setAnswer(StatusCode answer) {
        this.answer = answer;
    }
}
