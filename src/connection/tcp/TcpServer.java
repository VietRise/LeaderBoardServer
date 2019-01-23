package connection.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by MacBook on 1/23/19.
 */
public class TcpServer
{
    private String ip;
    private int    port;

    public TcpServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try
        {
            System.out.printf("TcpServer.runWindows NIO standard");
            EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
            ServerBootstrap bootstrap   = new ServerBootstrap();
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
            bootstrap.option(ChannelOption.SO_TIMEOUT, 1000);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.group(workerGroup).channel(NioServerSocketChannel.class).childHandler(new BCMessageHandlerInitializer());
            bootstrap.bind(ip, port);
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
    }
}
