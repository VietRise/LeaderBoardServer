package connection.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by MacBook on 1/23/19.
 */
public class UdpMessageHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
    public UdpMessageHandler()
    {

    }

    // Handler message income
    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
    {
        ByteBuf content = packet.content();
        int length = content.readableBytes();
        if (length > 0)
        {
            // Handle income message
            // ...
            // Response message to client
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        System.out.printf("UdpMessageHandler.exceptionCaught");
    }
}
