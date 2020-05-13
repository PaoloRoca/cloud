package netty_client;

public class Client {
    private String host;
    private int port;
    private String userName;
    private String password;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public Client() {
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }
}

