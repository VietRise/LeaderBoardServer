package connection.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by MacBook on 1/23/19.
 */
public class BCMessageHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
    public BCMessageHandler()
    {

    }

    @Override protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
    {

    }
}
