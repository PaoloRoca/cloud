package NettyServer;

import NettyServer.file_controller.ServerFileController;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * - Добавить возможность работы с разными протоколами
 * - Если Клиент шлет одновременно 2 файла...
 */
public class NettyServer {
    private final int port;

    private ChannelFuture channelFuture;
    private ServerBootstrap b;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    private ServerFileController fileController;
    private String usersDirectory = "server_storage";

    public NettyServer(int port) {
        this.port = port;
        this.fileController = new ServerFileController(Paths.get(usersDirectory));
    }

    public void start() {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n",Charset.forName("UTF-8")));

        bossGroup = new NioEventLoopGroup(); //обработка событий: новых соединений, чтение/запись
        workerGroup = new NioEventLoopGroup(2); //

        try {
            b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) //Тип канала
             .localAddress(new InetSocketAddress(port))
             .childHandler(new ChannelInitializer<SocketChannel>() { //Добавляем обработчик на канал
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new ServerInBoundHandler());
                        ch.pipeline().addLast(new FrameHandler());
                        ch.pipeline().addLast(new ServerOutBoundHandler());
                    }
            });
            channelFuture = b.bind().sync();

        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        if (this.channelFuture.isDone()) {
            try {
                channelFuture.channel().closeFuture().sync(); //sync() ждет завершения операции
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final NettyServer nettyServer = new NettyServer(8080);

        try {
            nettyServer.start();
            System.out.println("Server Nio Start!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
