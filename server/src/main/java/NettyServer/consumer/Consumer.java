package NettyServer.consumer;

import io.netty.channel.ChannelHandlerContext;
import java.nio.file.Path;

/**
 * TODO
 */
public class Consumer {
    private ChannelHandlerContext channel;
    private Path userDirectory;

    public Consumer(ChannelHandlerContext ctx, Path dir) {
        this.channel = ctx;
        this.userDirectory = dir;
    }

    public ChannelHandlerContext getChannel() {
        return channel;
    }

    public Path getUserDirectory() {
        return userDirectory;
    }
}
