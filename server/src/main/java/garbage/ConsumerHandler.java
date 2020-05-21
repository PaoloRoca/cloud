package garbage;

import NettyServer.consumer.Consumer;
import org.jboss.netty.channel.*;

public class ConsumerHandler extends SimpleChannelUpstreamHandler {
    private Consumer consumer;

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);

        //consumer = new Consumer(ctx, new ByteFramePacket());
        System.out.println("ClientHandler.Подключение клиента");
        System.out.println("Client " + ctx.getChannel().getRemoteAddress() + " connected");
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        //TODO
        System.out.println("ClientHandler.Отключение клиента");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);

        System.out.println("ClientHandler.messageReceived");

//        //Отправим, что приняли (пишем в объект)
//        ctx.sendUpstream(Unpooled.copiedBuffer("message received by the server", CharsetUtil.UTF_8));
//
    }

    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        super.writeComplete(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
        ctx.getChannel().close();
    }
}
