package ServerPiper;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.io.ByteArrayInputStream;

@ChannelHandler.Sharable
public class DataServerHandler extends SimpleChannelInboundHandler<ByteArrayInputStream> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteArrayInputStream byteArrayInputStream) throws Exception {

    }
}
