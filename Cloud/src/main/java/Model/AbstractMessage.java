package Model;

import java.io.Serializable;

public interface AbstractMessage extends Serializable {

    MessageType getMessageType();

}
