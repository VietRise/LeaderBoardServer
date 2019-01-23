package common;

import db.DataAccess;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by MacBook on 1/23/19.
 */
public class GlobalVariable
{
    private static final String USERID_GENERATOR_KEY                = "UserID";
    private static final int    NUM_CORE                            = Runtime.getRuntime().availableProcessors();
    private static final int    NUM_THREAD_PER_POOL                 = Runtime.getRuntime().availableProcessors() / 2;

    public static ScheduledThreadPoolExecutor schThreadPool    = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(NUM_THREAD_PER_POOL);

    public static String redis;

    public static class DB_FUNCTION_KEY
    {
        public static final String DB_CHECK_KEY = "Game";
        public static final int DB_CHECK_INTERVAL = 29 * 60 * 1000; // 29' in ms

        // TODO: will be loaded from Json config file
        public static String[] GAME_STATIC;
        public static String[][] USER_GAME_DATA;

        public static final HashMap<String[], String> DEBUG_BUCKET_NAMES = new HashMap<>();
    }

    public interface ONLINE_INFO
    {
        int SESSION_IDLE_CHECK_INTERVAL = 130; // 2'10s
    }

    public static class MESSAGE_HANDLER_NAME
    {
        public static final String ENCODER           = "encoder";
        public static final String DECODER           = "decoder";
        public static final String HANDLER           = "handler";
    }

    public static String getNextUserID()
    {
        long nextUserId = DataAccess.getInstance().getNextUserID(USERID_GENERATOR_KEY);
        return Long.toString(nextUserId);
    }
}
