package NettyServer.file_controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerFileController {
    Path baseDir;

    public ServerFileController(Path baseDir) {
        this.baseDir = baseDir;
    }

    public Path getDirectory(String name) throws IOException {
        Path dirIsExits = baseDir.resolve(name);

        if (Files.exists(dirIsExits))
            return dirIsExits;

        Path directory = Files.createDirectory(dirIsExits);
            return directory;
    }


}
