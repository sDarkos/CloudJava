package FileModel;

import com.sun.prism.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private final String name;
    private final long size;
    private final boolean isFolder;
    private final Path path;

    public FileInfo(Path path) {
        this.path = path;
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

    public Path getPath() {
        return path;
    }
}
