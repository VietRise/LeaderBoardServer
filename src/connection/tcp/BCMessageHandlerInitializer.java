package connection.tcp;

import common.GlobalVariable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by MacBook on 1/23/19.
 */
public class BCMessageHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel)
    {
        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addLast(new IdleStateHandler(GlobalVariable.ONLINE_INFO.SESSION_IDLE_CHECK_INTERVAL, 0, 0));
        channelPipeline.addLast(GlobalVariable.MESSAGE_HANDLER_NAME.HANDLER, new BCMessageHandler());
    }
}
