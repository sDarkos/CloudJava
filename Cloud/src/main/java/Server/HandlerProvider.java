package Server;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HandlerProvider {

    public ChannelHandler[] getStringPipeLine () {
        return new ChannelHandler[] {
                new StringEncoder(),
                new StringDecoder(),
                new StringMessageHandler()
        };
    }

    public ChannelHandler[] getSerializePipeLine () {
        return new ChannelHandler[] {
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ObjectEncoder(),
                new AbstractMessageHandler()
        };
    }
}
