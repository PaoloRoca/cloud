package netty_client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 * SimpleChannelInboundHandler сам заботится об освобождении памяти ByteBuf
 */
@Sharable
public class ClientReadFromServer extends ChannelInboundHandlerAdapter {
    public enum State {
        IDLE, COMMAND, NAME_LENGTH, NAME, FILE_LENGTH, FILE
    }

    private final byte COM_DIRECTORY_STRUCT = 4;
    private final byte COM_RECEIPT_FILE = 1;
//    private final byte COMMAND_DELETE_FILE = 2;
//    private final byte COMMAND_SEND_FILE = 3;

    private State currentState = State.IDLE;
    private int receivedFileLength;  //Принято байт файла
    private byte nameLength; //Длина имени файла
    private BufferedOutputStream out;
    private int fileLength; //Длина данных файла

    //Вызывается при установлении соединения
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("*** ClientReadFromServer.channelActive");
    }

    //Чтение данных от сервера
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = ((ByteBuf) msg);
        System.out.println("*** ClientConnectHandler.channelRead0: " + in.toString(CharsetUtil.UTF_8) + " ");

        while (in.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte read = in.readByte();
                if (read == COM_RECEIPT_FILE) {
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0;
                    System.out.println("COMMAND 1: " + read);
                }
                else if (read == COM_DIRECTORY_STRUCT) {
                    currentState = State.FILE_LENGTH;
                    receivedFileLength = 0;
                    System.out.println("COMMAND 4: " + read);
                }
                else {
                    System.out.println("ERROR: Invalid first byte - " + read);
                    //TODO рубим соединение
                }
            }
            if (currentState == State.NAME_LENGTH) {
                if (in.readableBytes() >= 4) {
                    nameLength = in.readByte();
                    currentState = State.NAME;
                    System.out.println("NAME_LENGTH: " + nameLength);
                }
            }
            if (currentState == State.NAME) {
                if (in.readableBytes() >= nameLength) {
                    byte[] fileName = new byte[nameLength];
                    in.readBytes(fileName);
                    out = new BufferedOutputStream(new FileOutputStream("_" + new String(fileName)));
                    currentState = State.FILE_LENGTH;
                    System.out.println("NAME: " + new String(fileName, "UTF-8"));
                }
            }
            if (currentState == State.FILE_LENGTH) {
                if (in.readableBytes() >= 8) {
                    fileLength = in.readInt();
                    System.out.println("FILE_LENGTH: " + fileLength);
                    currentState = State.FILE;
                }
            }
            if (currentState == State.FILE) {
                while (in.readableBytes() > 0) {
                    out.write(in.readByte()); //Запись данных в файл пачками по 8кБ
                    receivedFileLength++;
                    if (fileLength == receivedFileLength) {
                        currentState = State.IDLE;
                        System.out.println("File received");
                        out.close();
                        break;
                    }
                }
            }
        }
        if (in.readableBytes() == 0) {
            in.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
