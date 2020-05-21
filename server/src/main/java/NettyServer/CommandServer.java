package NettyServer;

import NettyServer.consumer.Consumer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * byte[] arr - список файлов/каталогов папки Клиента
 */
public class CommandServer {
    private static int BUFFER_SIZE = 64 * 1024; //64kB

    private static String SEND_FILE_TO_CLIENT = "6";
    private static String SEND_DIRECTORY_STRUCT = "4";

    public static void sendDirectoryStruct (ChannelHandlerContext ctx, byte[] arr) {
        System.out.println();
        System.out.println("**CommandServer.sendDirectoryStruct: ");

        int capacity =
                1 +         //command byte
                8 +         //length of transmitted data
                arr.length; //data

        ByteBuf buf = Unpooled.buffer(capacity);

        buf.writeBytes(SEND_DIRECTORY_STRUCT.getBytes());
        buf.writeLong(arr.length);
        buf.writeBytes(arr);

        //Test
        System.out.print(" Command: " + SEND_DIRECTORY_STRUCT + "\n" +
                " data length: " + arr.length + "\n" + " data: ");
        for (byte b : arr) {
            System.out.print((char) b);
        }
        System.out.println();

        ctx.writeAndFlush(buf);
    }

    public static void sendFileToClient (ChannelHandlerContext ctx, Consumer consumer, String file) throws IOException {
        System.out.println(" * CommandServer.sendFileToClient");

        Path path = consumer.getUserDirectory().resolve(file);
        FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path));

//        long capacity =
//                1 + //command
//                1 + //file name length (не больше 254)
//                file.trim().getBytes().length + //name of file
//                8 +  //data length
//                Files.size(path); //fileBuffer.length; //data

        ByteBuf byteBuf = Unpooled.buffer(1+1+1+8);

        byteBuf.writeBytes(SEND_FILE_TO_CLIENT.getBytes()); //Команда
        byteBuf.writeByte(file.trim().getBytes().length); //Длина имени файла (не больше 254)
        byteBuf.writeBytes(file.trim().getBytes(StandardCharsets.UTF_8)); //Имя файла
        byteBuf.writeLong(Files.size(path)); // Размер данных

        ctx.writeAndFlush(byteBuf);
        ctx.writeAndFlush(region);// Данные файла

        //Test
        System.out.println("* Server received command: " + SEND_FILE_TO_CLIENT + "\n" +
                " name of file length: " + file.trim().getBytes().length + "\n" +
                " file name: " + file + "\n" + " data length: " + Files.size(path));
        System.out.print(" Data: ");
        System.out.println(region.toString());
        System.out.println();

//        for (int i = 0; i < fileBuffer.length; i++) {
//            System.out.print((char) fileBuffer[i]);
//        }

    }

//    public static void deleteFile () {
//
//    }

}
