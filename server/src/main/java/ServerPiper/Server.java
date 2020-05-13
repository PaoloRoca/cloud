package ServerPiper;

import io.netty.channel.nio.NioEventLoopGroup;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Server {
    private final String host;
    private final int port;

    Channel channel;
    ServerBootstrap bootstrap;
    NioEventLoopGroup bossGroup;
    NioEventLoopGroup workerGroup;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        ExecutorService bossGroup = Executors.newFixedThreadPool(1);
        ExecutorService workerGroup = Executors.newFixedThreadPool(2);

        try {
            ChannelFactory factory = new NioServerSocketChannelFactory(bossGroup, workerGroup, 2);
            bootstrap = new ServerBootstrap(factory);
            bootstrap.setOption("child.tcpNoDelay", true);
            bootstrap.setOption("child.keepAlive", true);
            bootstrap.setPipelineFactory(new ServerPipelineFactory());
            channel = bootstrap.bind(new InetSocketAddress(this.host, this.port));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Как остановить сервер на Netty
     */
    public void stop() {

        if (this.channel.isOpen()) {
            try {
                channel.close();
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final Server server = new Server("localhost", 8080);

        try {
            server.start();
            System.out.println("Server Nio Start!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
