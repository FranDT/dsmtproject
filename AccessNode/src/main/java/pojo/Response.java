package pojo;

public class Response {

    private String data;
    private int status;

    public Response() {}

    public Response(String data, int status){
        this.data = data;
        this.status = status;
    }

    public String getData() {return data;}
    public int getStatus() {return status;}
    public void setData(String data) {this.data = data;}
    public void setStatus(int status) {this.status = status;}
}
