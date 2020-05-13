package garbage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 *
 */
public class NioServer {
    private final String host;
    private final int port;

    ChannelFuture future;
    ServerBootstrap b;
    NioEventLoopGroup bossGroup;
    NioEventLoopGroup workerGroup;

    public NioServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n",Charset.forName("UTF-8")));
        //ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor(1, 400000000, 2000000000, 60, TimeUnit.SECONDS);
        //ExecutorService ioExec = new OrderedMemoryAwareThreadPoolExecutor(4 /* число рабочих потоков */, 400000000, 2000000000, 60, TimeUnit.SECONDS);

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                        }
                                    });
                        }
                                  }
                    );
            future = b.bind().sync();

        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    /**
     *
     */
    public void stop() {

        if (this.future.isDone()) {
            try {
                future.channel().closeFuture().sync();
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final NioServer nioServer = new NioServer("localhost", 5151);

        try {
            nioServer.start();
            System.out.println("Server Nio Start!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
