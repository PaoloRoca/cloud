package ServerPiper;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * отправка пакетов клиента в поток
 */
public class ProtocolEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext channelHandlerContext, Channel channel, Object o) throws Exception {
        //return null;
        Packet p = (Packet) o;
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        Packet.write(p, buffer); //Пишем пакет в буфер

        System.out.println("ProtocolEncoder.encode");

        return buffer;
    }
}
