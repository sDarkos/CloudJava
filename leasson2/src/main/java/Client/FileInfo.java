package Client;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private final String name;
    private final long size;
    private final boolean isFolder;

    public FileInfo(Path path) {
        isFolder = Files.isDirectory(path);
        name = path.getFileName().toString();
        if (!isFolder) {
            size = path.toFile().length();
        } else {
            size = 0;
        }
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
