package file_manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
    public static final String UP_TOKEN = "[..]";

    public enum FileType {
        FILE("F"), DIRECTORY("D");
        private String name;
        public String getName() {
            return name;
        }
        FileType(String name) {
            this.name = name;
        }
    }

    private String filename;
    private long size;
    private FileType type;
    private LocalDateTime lastModified;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public FileInfo(Path path) {
        try {
            this.filename = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.type == FileType.DIRECTORY) {
                this.size = -1L;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }

    public FileInfo(String filename, long size) {
        this.filename = filename;
        this.size = size;
    }

    public boolean isDirectory() {
        return size == -1L;
    }

    public boolean isUpElement() {
        return size == -2L;
    }
}