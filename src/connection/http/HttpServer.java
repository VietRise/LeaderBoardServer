package connection.http;

import common.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by MacBook on 1/23/19.
 */
public class HttpServer implements Runnable
{
    private String ip;
    private int port;

    public HttpServer(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        new Thread(this).start();
    }

    public String getAddress()
    {
        return StringUtils.join(ip, StringUtils.DELIMITER_UNDERSCORE, port);
    }

    @Override
    public void run()
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpMessageHandlerInitializer());
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            b.option(ChannelOption.SO_TIMEOUT, 5000);
            b.childOption(ChannelOption.SO_KEEPALIVE, false);
            b.bind(ip, port).sync().channel().closeFuture().sync();
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
