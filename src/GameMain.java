import common.ServiceInfo;
import connection.http.HttpServer;
import connection.tcp.TcpServer;
import connection.udp.UdpMessageHandlerInitializer;
import connection.udp.UdpServer;
import db.DataAccess;
import leaderboard.LeaderBoardManager;

/**
 * Created by MacBook on 1/23/19.
 */
public class GameMain
{
    public static final GameMain instance = new GameMain();

    public String serverID = "0";
    public TcpServer tcpServer;
    public UdpServer udpServer;
    public HttpServer httpServer;


    private void initServer()
    {
        DataAccess.getInstance();
        LeaderBoardManager.getInstance().start();
    }

    public void start()
    {
        System.out.printf("SERVICE_STATUS_STARTING ...");
        final ServiceInfo.Game     siGame     = ServiceInfo.game;
        serverID = String.valueOf(siGame.sid);

        initServer();
        addShutdownHook();

        tcpServer = new TcpServer(siGame.tcpIP, siGame.tcpPort);
        udpServer = new UdpServer(siGame.udpIP, siGame.udpPort, new UdpMessageHandlerInitializer());
        httpServer = new HttpServer(siGame.httpIP, siGame.httpPort);

        System.out.printf("SERVICE_STATUS_RUNNING ...");
    }

    private void addShutdownHook()
    {
        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                System.out.printf("BCServer shutdown hook!!!");
                LeaderBoardManager.getInstance().stop();
            }
            catch (Exception ex)
            {
                System.out.printf(ex.getMessage());
            }
        }));
    }
}
