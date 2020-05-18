package NettyServer;

import NettyServer.file_controller.ServerFileController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FrameHandler extends ChannelInboundHandlerAdapter {
    public enum State {
        IDLE, COMMAND, NAME_LENGTH, NAME, FILE_LENGTH, FILE,
        FILE_NAME_LENGTH, FILE_NAME
    }

    private final byte COMMAND_RECEIVE_FILE = 49; //1 Получение файла с Клиента
    private final byte COMMAND_DELETE_FILE = 2;
    private final byte COMMAND_SEND_FILE = 51; //3 Запрос на получение файла

    private State currentState = State.IDLE;
    private int receivedFileLength;  //Принято байт файла
    private byte nameLength; //Длина имени файла
    private BufferedOutputStream out;
    private int fileLength; //Длина данных файла

    private Consumer consumer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("* FrameHandler.channelActive: ");
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
        ctx.fireChannelActive();

        //TODO
        Path path = Paths.get("server_storage", "1");
        System.out.println(path);
        this.consumer = new Consumer(ctx, path);
        //TODO передача структуры каталогов клиенту, CallBack из ServerFileController !!!
//        byte[] userFiles = ServerFileController.getFilesNameList(
//                ServerFileController.getDirectory(consumer.getUserDirectory()));
        byte[] userFiles = ServerFileController.getFilesNameList(consumer.getUserDirectory());
        CommandServer.sendDirectoryStruct(ctx, userFiles);

        System.out.print("ServerInBoundHandler.channelActive ");
        System.out.println("*** Client " + ctx.channel().remoteAddress() + " connected");
        ctx.writeAndFlush(Unpooled.copiedBuffer("* Server channel send", CharsetUtil.UTF_8));

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("* FrameHandler.channelRead: ");
        ByteBuf in = ((ByteBuf) msg);

        while (in.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte read = in.readByte();
                if (read == COMMAND_RECEIVE_FILE) {
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0;
                    System.out.print("COMMAND 1/49: " + read);
                }
                else if (read == COMMAND_SEND_FILE) {
                    currentState = State.FILE_NAME_LENGTH;
                    receivedFileLength = 0;
                    System.out.print("COMMAND 3/51: " + read);
                }
                else {
                    System.out.println(" !!! ERROR: Invalid first byte - " + read);
                    //TODO рубим соединение
                }
            }
            if (currentState == State.NAME_LENGTH) {
                if (in.readableBytes() >= 1) {
                    nameLength = in.readByte();
                    currentState = State.NAME;
                    System.out.print(" NAME_LENGTH: " + nameLength);
                }
            }
            if (currentState == State.FILE_NAME_LENGTH) {
                if (in.readableBytes() >= 1) {
                    nameLength = in.readByte();
                    currentState = State.FILE_NAME;
                    System.out.print(" FILE_NAME_LENGTH: " + nameLength);
                }
            }
            if (currentState == State.NAME) {
                if (in.readableBytes() >= nameLength) {
                    byte[] fileName = new byte[nameLength];
                    in.readBytes(fileName);
                    out = new BufferedOutputStream(
                            new FileOutputStream(consumer.getUserDirectory() + "\\" + "_" + new String(fileName)));
//                    System.out.println(consumer.getUserDirectory() + "\\" + "_" + new String(fileName));
                    currentState = State.FILE_LENGTH;
                    System.out.print(" NAME: " + new String(fileName, "UTF-8"));
                }
            }
            if (currentState == State.FILE_LENGTH) {
                if (in.readableBytes() >= 4) {
                    fileLength = in.readInt();
                    System.out.print(" FILE_LENGTH: " + fileLength);
                    currentState = State.FILE;
                }
            }
            if (currentState == State.FILE) {
                while (in.readableBytes() > 0) {
                    out.write(in.readByte()); //Запись данных в файл пачками по 8кБ
                    receivedFileLength++;
                    if (fileLength == receivedFileLength) {
                        currentState = State.IDLE;
                        System.out.print(" File received");
                        out.close();
                        //TODO передача структуры каталогов клиенту, CallBack из ServerFileController !!!
                        byte[] userFiles = ServerFileController.getFilesNameList(consumer.getUserDirectory());
                        CommandServer.sendDirectoryStruct(ctx, userFiles);
                        break;
                    }
                }
            }
            if (currentState == State.FILE_NAME) {
                if (in.readableBytes() >= nameLength) {
                    byte[] fileName = new byte[nameLength];
                    in.readBytes(fileName);
                    String file = new String(fileName);
                    System.out.println(", FILE_NAME: " + file);
                    CommandServer.sendFileToClient (ctx, consumer, file);
                    currentState = State.IDLE;
                }
            }
        }
        if (in.readableBytes() == 0) {
            in.release();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client " + ctx.channel().remoteAddress() + " disconnected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
