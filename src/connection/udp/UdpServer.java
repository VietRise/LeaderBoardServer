package connection.udp;

import common.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by MacBook on 1/23/19.
 */
public class UdpServer
{
    private final static int SO_RCVBUF_SIZE = 32 * 1024;

    public String udpIp;
    public int udpPort;
    public String hostAddress;
    public ChannelInitializer<DatagramChannel> initializer;

    public UdpServer(String ip, int port, ChannelInitializer<DatagramChannel> initializer)
    {
        udpIp = ip;
        udpPort = port;
        hostAddress = StringUtils.join(udpIp, StringUtils.DELIMITER_COLON, udpPort);
        this.initializer = initializer;

        EventLoopGroup group = new NioEventLoopGroup(1);
        try
        {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_RCVBUF, SO_RCVBUF_SIZE)
                    .handler(initializer).bind(udpIp, udpPort);
        }
        catch(Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
    }
}
