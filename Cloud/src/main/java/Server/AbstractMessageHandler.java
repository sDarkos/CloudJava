package Server;

import Model.AbstractMessage;
import Model.FileListMessage;
import Model.FileMessage;
import Model.FileRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class AbstractMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path serverFiles;

    public AbstractMessageHandler() {
        serverFiles = Paths.get("src/main/java/Server/UsersFiles");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new FileListMessage(serverFiles));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {

        log.debug("Received: {}", msg);
            switch (msg.getMessageType()){
                case FILE:
                    FileMessage fileMessage = (FileMessage) msg;
                    Files.write(
                            serverFiles.resolve(fileMessage.getFileName()),
                            fileMessage.getBytes());
                    ctx.writeAndFlush(new FileListMessage(serverFiles));
                    break;
                case FILE_REQUEST:
                    FileRequestMessage req = (FileRequestMessage) msg;
                    ctx.writeAndFlush(new FileMessage(
                            serverFiles.resolve(req.getFileName())));
                    break;
            }
    }
}
