package NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class FrameHandler extends ChannelInboundHandlerAdapter {
    public enum State {
        IDLE, COMMAND, NAME_LENGTH, NAME, FILE_LENGTH, FILE
    }

    private final byte COMMAND_RECEIPT_FILE = 1;
    private final byte COMMAND_DELETE_FILE = 2;
    private final byte COMMAND_SEND_FILE = 3;

    private State currentState = State.IDLE;
    private int receivedFileLength;  //Принято байт файла
    private byte nameLength; //Длина имени файла
    private BufferedOutputStream out;
    private int fileLength; //Длина данных файла

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = ((ByteBuf) msg);

        while (in.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte read = in.readByte();
                if (read == COMMAND_RECEIPT_FILE) {
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0;
                    System.out.println("COMMAND: " + read);
                } else {
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
