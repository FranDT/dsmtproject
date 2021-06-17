package middleware.messages;

import java.io.Serializable;

public abstract class Message implements Serializable{
    protected OpCode opcode;

    public OpCode getOpcode() {
        return opcode;
    }
}
