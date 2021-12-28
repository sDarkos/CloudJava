package Model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FileListMessage implements AbstractMessage{

    private final List<String> files;

    public FileListMessage(Path path) throws IOException {
            files = Files.list(path)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
    }


    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_LIST;
    }
}
