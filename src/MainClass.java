import common.ConfigPath;
import common.ServiceInfo;

import java.io.File;

/**
 * Created by MacBook on 1/23/19.
 */
public class MainClass
{
    private static final int ARGS_POSITION_ENVIRONMENT_NAME  = 0;
    private static final int ARGS_POSITION_RUNNING_MODE_NAME = 1;

    public static void main(String[] args)
    {
        final ServiceInfo.Environment environment = ServiceInfo.Environment.valueOf(args[ARGS_POSITION_ENVIRONMENT_NAME]);
        try
        {
            ConfigPath.load(environment);
            ServiceInfo.load(ConfigPath.services);
            ServiceInfo.environment = environment;
            if (ServiceInfo.environment.isLocal())
            {
                redisOnLocal();
            }
            GameMain.instance.start();

        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
    }

    private static void redisOnLocal() throws Exception
    {
        // Run redis service on windows.
        if (ServiceInfo.environment.isLocal())
        {
            File dir = new File(".."+"/run");
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "START", "/B", "run_redis.bat");
            pb.directory(dir);
            pb.start();
        }
    }
}
