package Model;

import lombok.Data;

@Data
public class FileRequestMessage implements AbstractMessage{

    private final String fileName;

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}
