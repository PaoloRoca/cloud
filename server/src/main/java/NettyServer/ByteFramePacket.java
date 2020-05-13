package NettyServer;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import org.jboss.netty.util.CharsetUtil;

public class ByteFramePacket extends Packet{
//    private Logger logger = LoggerFactory.getLogger(ByteFramePacket.class );

    @Override
    public void get() {
        System.out.print("ByteFramePacket.get()");
        ChannelFuture f;

        ByteBuf in = (ByteBuf) msg;
        int length = in.readableBytes();
        int a = in.readByte();
        int b = in.readShort();
        int c = in.readInt();
        int d = in.readInt();

        System.out.println(" * Server received: " + a + " " + b + " " + c + " " + d);

        //Отправим, что приняли (пишем в объект)
        consumer.getChannel().write(Unpooled.copiedBuffer("* ByteFramePacket ", CharsetUtil.UTF_8));
    }

    @Override
    public void send() {

    }
}
