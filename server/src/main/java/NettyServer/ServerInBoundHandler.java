package NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class ServerInBoundHandler extends ChannelInboundHandlerAdapter {
    Consumer consumer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        ctx.fireChannelActive();

        this.consumer = new Consumer(ctx, new ByteFramePacket());
        //TODO Создание директории пользователя (если новый пользователь)

        System.out.print("ServerInBoundHandler.channelActive ");
        System.out.println("*** Client " + ctx.channel().remoteAddress() + " connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("** ServerInBoundHandler.channelRead");

        ByteBuf in = (ByteBuf) msg;
        //Принцип работы Read
        try {
            while (in.isReadable()) {
                System.out.print(in.readByte() + " ");
            }
        } finally {
            in.release();
        }

//        //Отладка
//        while (in.isReadable()) {
//            System.out.print(in.readByte() + " ");
//        }
//        System.out.println();

        int a = in.readByte(); //Команда
        int b = in.readByte(); //Длина имени файла
        byte[] name = new byte[b]; //Имя файла
        for (int i = 0; i < b; i++) {
            name[i] = in.readByte(); //in.getByte(0); а так можно опять читать сначала
        }
        int dataLength = in.readInt();
        byte[] data = new byte[dataLength]; //Длина данных файла
        for (int i = 0; i < dataLength; i++) {
            data[i] = in.readByte(); //Данные файла
        }

        //Отладка
        System.out.print("* Server received command: " + a + ", file length: " + b + ", name of file: ");
        for (int i = 0; i < name.length; i++) {
            System.out.print((char) name[i]);
        }
        System.out.println();
        System.out.print("* dataLength: " + dataLength + ", data: ");
        for (int i = 0; i < data.length; i++) {
            System.out.print((char) data[i]);
        }
        System.out.println();

//        ctx.fireChannelRead(msg);

        //Отправим, что приняли (пишем в объект)
        ctx.write(Unpooled.copiedBuffer("* Server received msg", CharsetUtil.UTF_8));
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();

//        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ServerInBoundHandler.exceptionCaught");

        cause.printStackTrace();
        ctx.close(); //TODO
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("Client " + ctx.channel().remoteAddress() + " disconnected");
    }
}