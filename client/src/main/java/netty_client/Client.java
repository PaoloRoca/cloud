package netty_client;

import io.netty.channel.Channel;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    private String host;
    private int port;
    private String userName;
    private String password;

    private Channel channel;
    private Path userDir;

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

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setClientParam (String host, int port, String userName, String password) {
        this.userDir = Paths.get("client_storage");
        this.host = host;
        this.port = port;
        this.userName = userName;
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

    public Path getUserDir() {
        return userDir;
    }

    public Client() {
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }
}

