package NettyServer.consumer;

import NettyServer.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TODO
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authOK = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);

        String input = (String) msg;
        if (authOK) {
            ctx.fireChannelRead(input);
            return;
        }
        //Обработка фрейма авторизации
        if (input.split(" ")[0].equals("/auth")) {
            String userName = input.split(" ")[1];
            authOK = true;
            //Создаем главный обработчик после того, как пользователь прошел аутинификацию
            //ctx.pipeline().addLast(new MainHandler(userName));
            //Удаляем тукущий обработчик
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
