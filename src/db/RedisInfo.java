package db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MacBook on 1/23/19.
 */
public class RedisInfo
{
    @SerializedName("REDIS_HOST") public static String HOST;
    @SerializedName("REDIS_PORT") public static int    PORT;
    @SerializedName("DB_INDEX")   public static int    DB_INDEX;
}
