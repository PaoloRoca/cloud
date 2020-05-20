package netty_client;

import file_manager.FilesController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * byte[] arr - список файлов/каталогов папки Клиента
 */
public class CommandClient {
    private static int BUFFER_SIZE = 64 * 1024; //64kB

    private static String SEND_FILE_TO_SERVER = "1";
    private static String REQUEST_FILE_SERVER = "3";

    //Запрос файла с Сервера.
    public static void requestFileFromServer (Channel channel, String name) {
        System.out.println();
        System.out.print("* CommandService.requestFileFromServer: ");

        int capacity =
                1 +             //command byte
                1 +             //length of the file name
                name.length(); //file name

        ByteBuf buf = Unpooled.buffer(capacity);

        buf.writeBytes("3".getBytes());
        buf.writeByte(name.length());
        buf.writeBytes(name.getBytes(CharsetUtil.UTF_8));

        //Отладка
        System.out.print("* Command: " + "3/51" + ", " + "data length: " + name.length()
                + ", data: " + name);
        System.out.println();

        channel.writeAndFlush(buf);
    }

    /**
     * Отправка файла на сервер
     */
    public static void sendFileToServer (Channel channel, ChannelFutureListener listener) throws IOException {
        System.out.println();
        System.out.print("* CommandService.sendFileToServer: ");

        Path filePath = FilesController.filesController.getSelectFileToSend();
        byte[] fileNameBytes = filePath.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        FileRegion region = new DefaultFileRegion(filePath.toFile(), 0, Files.size(filePath));

        long capacity =
                1 + //command
                1 + //file name length (не больше 254)
                fileNameBytes.length + //name of file
                8 +  //data length
                Files.size(filePath); //data

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer(1+1+1+8);
//        byteBuf.writeByte((byte)1);
        byteBuf.writeBytes(SEND_FILE_TO_SERVER.getBytes()); //Команда
        byteBuf.writeByte(fileNameBytes.length); //Длина имени файла
        byteBuf.writeBytes(fileNameBytes); //Имя файла
        byteBuf.writeLong(Files.size(filePath)); // Размер данных
        channel.writeAndFlush(byteBuf);
//        channel.writeAndFlush(region);

        ChannelFuture transferOperationFuture = channel.writeAndFlush(region);
        if (listener != null) {
            transferOperationFuture.addListener(listener);
        }

        //Отладка
        System.out.println("* Server received command: " + "1" + ", " +
                "name of file length: " + fileNameBytes.length + ", " +
                "file name: " + filePath.getFileName().toString() +
                ", data length: " + Files.size(filePath));
        System.out.print("Data: ");

        System.out.println(region.toString());
        System.out.println();
//        for (byte b : region) {
//            System.out.print((char) b);
//        }

    }

//    public static void deleteFile () {
//
//    }

}
