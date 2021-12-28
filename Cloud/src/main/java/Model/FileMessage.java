package Model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileMessage implements AbstractMessage{

    private final String fileName;
    private final byte[] bytes;

    public FileMessage (Path path) throws IOException {
        bytes = Files.readAllBytes(path);
        fileName = path.getFileName().toString();
    }


    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
