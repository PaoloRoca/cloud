package netty_client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * SimpleChannelInboundHandler сам заботится об освобождении памяти ByteBuf
 */
@Sharable
public class ClientReadFromServer extends SimpleChannelInboundHandler<ByteBuf> {

    //Вызывается при установлении соединения
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("*** ClientReadFromServer.channelActive");
    }

    //Чтение данных от сервера
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println("*** ClientConnectHandler.channelRead0: " + in.toString(CharsetUtil.UTF_8) + " ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("ClientReadFromServer.channelRegistered");
//        super.channelRegistered(ctx);
    }
}
