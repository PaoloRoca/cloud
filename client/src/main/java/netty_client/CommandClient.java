package netty_client;

import file_manager.FilesController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * byte[] arr - список файлов/каталогов папки Клиента
 */
public class CommandClient {
    /**
     * Запрос файла с Сервера.
     */
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
     * @param channel
     * @throws IOException
     */
    public static void sendFileToServer (Channel channel) throws IOException {
        System.out.println();
        System.out.print("* CommandService.sendFileToServer: ");

        //Отладка
//        ChannelPipeline pipeline = connectController.getChannel().pipeline();
//        pipeline.writeAndFlush(Unpooled.copiedBuffer("123 456",CharsetUtil.UTF_8));

        Path filePath = FilesController.filesController.getSelectFileToSend();
        String fileName = filePath.getFileName().toString();
        System.out.println("Файл для передачи: " + filePath);

        //Данные файла
        //TODO Путь - отобразить реальную папку!!!
//        Path path = Paths.get("client_storage", file);
//        Path file = path.getFileName();
        System.out.println("path.getParent(): " + filePath.getParent());
        InputStream inputStream = Files.newInputStream(filePath);

        int bufferSize = 32000;
        byte[] fileBuffer = new byte[inputStream.available()];

        while (inputStream.available() > 0) {
            if (inputStream.available() < 32000) bufferSize = inputStream.available();
            inputStream.read(fileBuffer, 0, bufferSize);
            //TODO если файл больше 32000
        }

        int capacity =
                1 + //command
                        1 + //file name length (больше 254)
                        fileName.trim().getBytes().length + //name of file
                        4 +  //data length
                        fileBuffer.length; //data

        ByteBuf byteBuf = Unpooled.buffer(capacity);

        byteBuf.writeBytes("1".getBytes()); //Команда
        byteBuf.writeByte(fileName.trim().getBytes().length); //Длина имени файла не (больше 254)
        byteBuf.writeBytes(fileName.getBytes()); //Имя файла
        byteBuf.writeInt(fileBuffer.length); // Размер данных
        byteBuf.writeBytes(fileBuffer); // Данные файла

//        ChannelPipeline pipeline = connectController.getChannel().pipeline();
//        pipeline.writeAndFlush(byteBuf);
        channel.writeAndFlush(byteBuf);

        //Отладка
        System.out.println("* Server received command: " + "1" + ", " +
                "name of file length: " + fileName.trim().getBytes().length + ", " +
                "file name: " + fileName +", data length: " + fileBuffer.length);
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
