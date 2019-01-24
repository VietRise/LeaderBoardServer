package db;

import common.ConfigPath;
import common.GlobalVariable;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import common.JsonUtils;

/**
 * Created by MacBook on 1/23/19.
 */

public class RedisController
{
    private JedisPool pool;

    private static final RedisController instance = new RedisController();
    public static RedisController getInstance()
    {
        return instance;
    }

    public RedisController()
    {
        try
        {
            JsonUtils .fromFile(ConfigPath.redis, RedisInfo.class, true);
            pool = new JedisPool(new GenericObjectPoolConfig(), RedisInfo.HOST, RedisInfo.PORT, 10_000, null, RedisInfo.DB_INDEX);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public JedisPool getJedisPool()
    {
        return pool;
    }
}
