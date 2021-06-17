package middleware.messages;

public enum OpCode {
    Login_User      (0),
    Register_User   (1),
    Answer_User     (2);

    private final byte opcode;

    OpCode(int opcode){
        this.opcode = (byte)opcode;
    }

    public byte getOpCode() {
        return this.opcode;
    }
}
