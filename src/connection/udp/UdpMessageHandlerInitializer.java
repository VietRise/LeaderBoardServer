package connection.udp;

import common.GlobalVariable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

/**
 * Created by MacBook on 1/23/19.
 */
public class UdpMessageHandlerInitializer  extends ChannelInitializer<DatagramChannel>
{
    @Override
    protected void initChannel(DatagramChannel channel)
    {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(GlobalVariable.MESSAGE_HANDLER_NAME.HANDLER, new UdpMessageHandler());
    }
}
