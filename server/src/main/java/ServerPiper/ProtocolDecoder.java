package ServerPiper;

import io.netty.handler.codec.TooLongFrameException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

import java.util.List;

//enum MyDecoderState {
//    READ_LENGTH,
//    READ_CONTENT
//}

public class ProtocolDecoder extends ReplayingDecoder<VoidEnum> {
//    private int length;
//
//    public ProtocolDecoder() {
//        super(MyDecoderState.READ_LENGTH);
//    }
//
//    @Override
//    protected Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, ChannelBuffer buffer, MyDecoderState state) {
//        switch (state) {
//            case READ_LENGTH:
//                length = buffer.readInt();
//                checkpoint(MyDecoderState.READ_CONTENT);
//
//            case READ_CONTENT:
//                ChannelBuffer frame = buffer.readBytes(length);
//                checkpoint(MyDecoderState.READ_LENGTH);
//                System.out.println("ProtocolDecoder.decode: " + frame);
//                return frame;
//
//            default:
//                throw new Error("Shouldn't reach here.");
//        }
//    }

    @Override
    public Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum e) throws Exception {
        List<Byte> out = null;

        System.out.println("ProtocolDecoder.decode");
        //System.out.println(buffer.readBytes(buffer.readInt()));

        int readable = buffer.readableBytes();

        for (int i = 0; i < readable; i++) {
            out.add(buffer.readByte());
            System.out.println(out.get(i));
        }

        return Packet.read(buffer);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //super.channelClosed(ctx, e);
        ctx.sendUpstream(e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //super.channelDisconnected(ctx, e);
        ctx.sendUpstream(e);
    }
}
