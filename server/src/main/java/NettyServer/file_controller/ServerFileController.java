package NettyServer.file_controller;

import NettyServer.Consumer;
import io.netty.channel.Channel;
import org.apache.commons.lang3.ArrayUtils;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerFileController {
    private static Path baseDir;

    public ServerFileController(Path baseDir) {
        ServerFileController.baseDir = baseDir;
    }

    public static Path getDirectory(String name) throws IOException {
        Path dirIsExits = baseDir.resolve(name);

        if (Files.exists(dirIsExits))
            return dirIsExits;

        Path directory = Files.createDirectory(dirIsExits);
            return directory;
    }

    //TODO убрать соломку!
    public static byte[] getFilesNameList(Path path) throws IOException {
        List<String> files = new ArrayList<>();

        try (Stream<Path> stream = Files.list(path)) {
            files = stream.filter(Files::isRegularFile)
                    .map(Path::getFileName).map(Path::toString)
                    .collect(Collectors.toList());
        }

//        Stream<Path> list = Files.list(path);
        byte[] arr = null;
        byte[] line = "-".getBytes(CharsetUtil.UTF_8);

        for (String file : files) {
            byte[] arr1 = file.getBytes(CharsetUtil.UTF_8);
            arr = ArrayUtils.addAll(arr, line);
            arr = ArrayUtils.addAll(arr, arr1);
        }
        //ByteBuf buf = Unpooled.wrappedBuffer(arr);

        System.out.println("* ServerFileController. Список файлов клиента: " + files);

        return arr;
    }

}
