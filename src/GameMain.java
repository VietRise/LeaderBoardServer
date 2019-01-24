import common.GlobalVariable;
import common.ServiceInfo;
import connection.http.HttpServer;
import connection.tcp.TcpServer;
import connection.udp.UdpMessageHandlerInitializer;
import connection.udp.UdpServer;
import data.UserProfile;
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

        // Cheat create 10 member
        /*
        try {
            for (int i = 0; i < 10; i++) {
                String userID = GlobalVariable.getNextUserID();
                UserProfile userProfile = new UserProfile(userID, userID, 0);
                DataAccess.getInstance().addUserProfile(userProfile, 0);
            }
        }
        catch (Exception ex)
        {
            System.out.println("DataAccess.getInstance().addUserProfile error");
        }
        */

        // tcpServer = new TcpServer(siGame.tcpIP, siGame.tcpPort);
        // udpServer = new UdpServer(siGame.udpIP, siGame.udpPort, new UdpMessageHandlerInitializer());
        // httpServer = new HttpServer(siGame.httpIP, siGame.httpPort);

        System.out.printf("SERVICE_STATUS_RUNNING ...");
    }

    private void addShutdownHook()
    {
        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                System.out.println("BCServer shutdown hook!!!");
                LeaderBoardManager.getInstance().stop();
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }));
    }
}
