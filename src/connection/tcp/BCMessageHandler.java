package connection.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by MacBook on 1/23/19.
 */
public class BCMessageHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
    private ChannelHandlerContext myCtx;
    private boolean isClosed;

    public BCMessageHandler()
    {

    }

    @Override protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
    {
        if (isClosed)
        {
            System.out.printf("This session was closed");
            return;
        }
        // Handle logic for request message from client
        myCtx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        safeClose(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        System.out.printf(cause.getMessage());
        safeClose(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
        if ((evt instanceof IdleStateEvent) && (((IdleStateEvent) evt).state() == IdleState.READER_IDLE))
        {
            safeClose(ctx);
        }
    }

    private void safeClose(ChannelHandlerContext ctx)
    {
        if (ctx != null)
        {
            isClosed = true;
            ctx.close();
        }
    }
}
