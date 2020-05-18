package NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * byte[] arr - список файлов/каталогов папки Клиента
 */
public class CommandServer {
    public static void sendDirectoryStruct (ChannelHandlerContext ctx, byte[] arr) {
        System.out.println();
        System.out.print("* CommandService.sendDirectoryStruct: ");

        int capacity =
                1 +         //command byte
                4 +         //length of transmitted data
                arr.length; //data

        ByteBuf buf = Unpooled.buffer(capacity);

        buf.writeBytes("4".getBytes());
        buf.writeInt(arr.length);
        buf.writeBytes(arr);

        //Отладка
        System.out.print("* Command: " + "4" + ", " + "data length: " + arr.length + ", data: ");
        for (byte b : arr) {
            System.out.print((char) b);
        }
        System.out.println();

        ctx.writeAndFlush(buf);
    }

    public static void sendFileToClient (ChannelHandlerContext ctx, Consumer consumer, String file) throws IOException {
        System.out.println(" * CommandServer.sendFileToClient");

        InputStream in = Files.newInputStream(consumer.getUserDirectory().resolve(file));
        System.out.println(" send file: " + consumer.getUserDirectory().resolve(file));

        int bufferSize = 32000;
        byte[] fileBuffer = new byte[in.available()];

        while (in.available() > 0) {
            if (in.available() < 32000) bufferSize = in.available();
            in.read(fileBuffer, 0, bufferSize);
            //TODO если файл больше 32000
        }

        int capacity =
                1 + //command
                1 + //file name length (больше 254)
                file.trim().getBytes().length + //name of file
                4 +  //data length
                fileBuffer.length; //data

        ByteBuf byteBuf = Unpooled.buffer(capacity);

        byteBuf.writeBytes("6".getBytes()); //Команда
        byteBuf.writeByte(file.trim().getBytes().length); //Длина имени файла не (больше 254)
        byteBuf.writeBytes(file.getBytes()); //Имя файла
        byteBuf.writeInt(fileBuffer.length); // Размер данных
        byteBuf.writeBytes(fileBuffer); // Данные файла

//        ChannelPipeline pipeline = connectController.getChannel().pipeline();
//        pipeline.writeAndFlush(byteBuf);
        ctx.writeAndFlush(byteBuf);

        //Отладка
        System.out.println("* Server received command: " + "6" + ", " +
                "name of file length: " + file.trim().getBytes().length + ", " +
                "file name: " + file +", data length: " + fileBuffer.length);
        System.out.print("Data: ");
        for (int i = 0; i < fileBuffer.length; i++) {
            System.out.print((char) fileBuffer[i]);
        }
        System.out.println();
    }

//    public static void deleteFile () {
//
//    }

}
