package middleware.messages;

public enum StatusCode {
    OK      (0),
    Error   (1);

    private final byte opcode;

    StatusCode(int opcode){
        this.opcode = (byte)opcode;
    }

    public byte getOpCode() {
        return this.opcode;
    }
}
