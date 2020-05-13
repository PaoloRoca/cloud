package NettyServer;

/**
 *
 */

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

public class ServerOutBoundHandler extends ChannelOutboundHandlerAdapter {
    /**
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise); //Если убрать, то запись в канал не отрабатывается

        System.out.println("ServerOutBoundHandler.write");
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture cf) throws Exception {
                if (!cf.isSuccess()) {
                    cf.cause().printStackTrace();
                    cf.channel().close();
                }
                //TODO
                ReferenceCountUtil.release(msg);
                promise.setSuccess();
            }
        });
    }
}
