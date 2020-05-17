package NettyServer;

import NettyServer.file_controller.ServerFileController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerInBoundHandler extends ChannelInboundHandlerAdapter {
    private Consumer consumer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        ctx.fireChannelActive();

        //TODO
        Path path = Paths.get("server_storage", "1");
        System.out.println(path);
        this.consumer = new Consumer(ctx, path);
        //TODO передача структуры каталогов клиенту, CallBack из ServerFileController !!!
        byte[] userFiles = ServerFileController.getFilesNameList(ServerFileController.getDirectory("1"));
        CommandService.sendDirectoryStruct(ctx, userFiles);

        System.out.print("ServerInBoundHandler.channelActive ");
        System.out.println("*** Client " + ctx.channel().remoteAddress() + " connected");
        ctx.writeAndFlush(Unpooled.copiedBuffer("* Server channel send", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("** ServerInBoundHandler.channelRead");

        ByteBuf in = (ByteBuf) msg;
        //*** Принцип работы Read
//        try {
//            while (in.isReadable()) {
//                System.out.print(in.readByte() + " ");
//            }
//        } finally {
//            in.release(); //Сообщаем, что буфер не нужен.
//        }

//        //Отладка
//        while (in.isReadable()) {
//            System.out.print(in.readByte() + " ");
//        }
//        System.out.println();

        int a = in.readByte(); //Команда
        int b = in.readByte(); //Длина имени файла
        byte[] name = new byte[b]; //Имя файла
        in.readBytes(name);
//        for (int i = 0; i < b; i++) {
//            name[i] = in.readByte(); //in.getByte(0); а так можно опять читать сначала
//        }
        int dataLength = in.readInt();
        byte[] data = new byte[dataLength]; //Длина данных файла
        for (int i = 0; i < dataLength; i++) {
            data[i] = in.readByte(); //Данные файла
        }

        //Отладка
        System.out.print("* Command: " + a + ", " + "name of file length: "
                + b + ", file name: ");
        for (int i = 0; i < name.length; i++) {
            System.out.print((char) name[i]);
        }
        System.out.println(" data length: " + dataLength);
        System.out.print("data: ");
        for (int i = 0; i < data.length; i++) {
            System.out.print((char) data[i]);
        }
        System.out.println();

//        ctx.fireChannelRead(msg);

        //Отправим, что приняли
        ctx.writeAndFlush(Unpooled.copiedBuffer("* Server received msg", CharsetUtil.UTF_8));
        //ctx.flush();
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