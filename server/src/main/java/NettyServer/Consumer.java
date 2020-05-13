package NettyServer;

import io.netty.channel.ChannelHandlerContext;
import java.nio.file.Path;

public class Consumer {
    private ChannelHandlerContext channel;
    private Packet packet;
    private Path pathToDir;

    public Consumer(ChannelHandlerContext ctx, Packet packet) {
        this.channel = ctx;
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public ChannelHandlerContext getChannel() {
        return channel;
    }
}
