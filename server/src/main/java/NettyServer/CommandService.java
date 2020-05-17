package NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class CommandService {
    public static void sendDirectoryStruct (ChannelHandlerContext ctx, byte[] arr) {
        int capacity =
                1 +         //command byte
                4 +         //length of transmitted data
                arr.length; //data

        ByteBuf buf = Unpooled.buffer(capacity);

        buf.writeBytes("4".getBytes());
        buf.writeInt(arr.length);
        buf.writeBytes(arr);

        ctx.writeAndFlush(buf);
    }

    public static void deleteFile () {

    }

}
