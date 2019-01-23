package common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MacBook on 1/23/19.
 */
public class ServiceInfo
{
    public enum Environment
    {
        LOCAL;

        public boolean isLocal()
        {
            return this == LOCAL;
        }
    }

    public static transient Environment environment;

    @SerializedName("GAME_SERVICE")
    public static Game game;

    public class Game
    {
        @SerializedName("SID")
        public int sid;
        @SerializedName("TCP_IP")
        public String tcpIP;
        @SerializedName("TCP_PORT")
        public int tcpPort;
        @SerializedName("UDP_IP")
        public String udpIP;
        @SerializedName("UDP_PORT")
        public int udpPort;
        @SerializedName("HTTP_IP")
        public String httpIP;
        @SerializedName("HTTP_PORT")
        public int httpPort;
    }

    public static void load(String path) throws Exception
    {
        ServiceInfo info = JsonUtils.fromFile(path, ServiceInfo.class, true);
    }

    public static boolean has(String ip, int port)
    {
        return ip != null && ip.length() > 0 && port > 0;
    }
}
