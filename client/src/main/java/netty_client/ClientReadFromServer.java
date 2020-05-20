package netty_client;

import file_manager.FilesController;
import file_manager.RedrawWindow;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SimpleChannelInboundHandler сам заботится об освобождении памяти ByteBuf
 */
@Sharable
public class ClientReadFromServer extends ChannelInboundHandlerAdapter {
    Client client;
    private static RedrawWindow redrawWindow;

    public ClientReadFromServer(Client client) {
        this.client = client;
    }

    public enum State {
        IDLE, COMMAND, NAME_LENGTH, NAME, FILE_LENGTH, FILE,
        DATA_LENGTH, DATA //получение структуры каталогов
    }

    private final byte COM_DIRECTORY_STRUCT = 52;  //4 Передача каталога от Сервера
    private final byte COMMAND_RECEIVE_FILE = 54; //6 Получение файла с сервера

    private State currentState = State.IDLE;
    private int receivedFileLength;  //Принято байт файла
    private byte nameLength; //Длина имени файла
    private OutputStream out;
    private long fileLength; //Длина данных файла

    long startTime;
    long finishTime;

    //Вызывается при установлении соединения
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("* ClientReadFromServer.channelActive");
    }

    //Чтение данных от сервера
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = ((ByteBuf) msg);
        System.out.println("*** ClientReadFromServer.channelRead: " + in.toString(CharsetUtil.UTF_8) + " ");

        while (in.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte read = in.readByte();
                if (read == COMMAND_RECEIVE_FILE) {
                    startTime = System.currentTimeMillis(); //Измерение вермени приема файла
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0;
                    System.out.print("COMMAND 6/54: " + read);
                }
                else if (read == COM_DIRECTORY_STRUCT) {
                    currentState = State.DATA_LENGTH;
                    receivedFileLength = 0;
                    System.out.print("COMMAND 4/52: " + read);
                }
                else {
                    System.out.println("ERROR: Invalid first byte - " + read);
                    //TODO рубим соединение
                    currentState = State.IDLE;
                    in.release();
                }
            }
            if (currentState == State.NAME_LENGTH) {
                if (in.readableBytes() >= 1) {
                    nameLength = in.readByte();
                    currentState = State.NAME;
                    System.out.print(" NAME_LENGTH: " + nameLength);
                }
            }
            if (currentState == State.NAME) {
                if (in.readableBytes() >= nameLength) {
                    byte[] fileName = new byte[nameLength];
                    in.readBytes(fileName);
//                    String dir = client.getUserDir() + "\\" + new String(fileName);
                    Path path = client.getUserDir().resolve(new String(fileName));
                    out = Files.newOutputStream(path);
                    currentState = State.FILE_LENGTH;
                    System.out.print(" NAME: " + path.toString());
                }
            }
            if (currentState == State.FILE_LENGTH) {
                if (in.readableBytes() >= 8) {
                    fileLength = in.readLong();
                    System.out.print(" FILE_LENGTH: " + fileLength);
                    currentState = State.FILE;
                }
            }
            if (currentState == State.DATA_LENGTH) {
                if (in.readableBytes() >= 8) {
                    fileLength = in.readLong();
                    System.out.print(" DATA_LENGTH: " + fileLength);
                    currentState = State.DATA;
                }
            }
            if (currentState == State.FILE) {
                while (in.readableBytes() > 0) {
                    int size = in.readableBytes();
                    in.readBytes(out, size);
                    receivedFileLength += size;
                    System.out.println("Принято байтов: " + size + " осталось: " + (fileLength - receivedFileLength));
                    if (fileLength == receivedFileLength) {
                        currentState = State.IDLE;
                        out.close();
                        FilesController.filesController.refresh();
                        System.out.print(" ++ File received");

                        finishTime = System.currentTimeMillis();
                        System.out.println("\n время работы=" + (finishTime-startTime) + "ms.");
                        break;
                    }
                }
            }
            if (currentState == State.DATA) {
                if (in.readableBytes() >= fileLength) {
                    byte[] arr = new byte[(int) fileLength];
                    in.readBytes(arr);
                    currentState = State.IDLE;
                    String str = new String(arr);
                    //TODO Callback!
                    FilesController.filesController.showServerFiles(arr);
                    System.out.print(". ++ The directory structure received");
                    break;
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

    public static void sendMsgRedraw (String str) {
        redrawWindow.redraw(str);
    }
}
