package NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;

public class CommandService {
    public static void sendDirectoryStruct (ChannelHandlerContext ctx, byte[] arr) {
        System.out.println("* CommandService.sendDirectoryStruct: ");

        int capacity =
                1 +         //command byte
                4 +         //length of transmitted data
                arr.length; //data

        ByteBuf buf = Unpooled.buffer(capacity);

        buf.writeBytes("4".getBytes());
        buf.writeInt(arr.length);
        buf.writeBytes(arr);

        //Отладка
        System.out.print("* Command: " + "4" + ", " + "data length: " + arr.length + ", data: ");
        for (byte b : arr) {
            System.out.print((char) b);
        }
        System.out.println();

        ctx.writeAndFlush(buf);
    }

//    public static void deleteFile () {
//
//    }

}
