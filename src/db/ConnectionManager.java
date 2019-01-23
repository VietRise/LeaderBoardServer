package db;

import common.GlobalVariable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by MacBook on 1/23/19.
 */
public class ConnectionManager implements Runnable
{
    private final ConcurrentHashMap<String[], CBConnection> connectionPools = new ConcurrentHashMap<>();

    public ConnectionManager()
    {
        run();
    }

    public CBConnection getConnection(String[] functionKey)
    {
        CBConnection connection = connectionPools.get(functionKey);
        if(connection != null)
            return connection;
        connection = new CBConnectionFile(functionKey);
        connectionPools.put(functionKey, connection);
        return connection;
    }

    @Override
    public void run()
    {
        try
        {
            for(CBConnection connection : connectionPools.values())
                connection.increase(GlobalVariable.DB_FUNCTION_KEY.DB_CHECK_KEY, 1, 0L);
        }
        catch(Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        finally
        {
            GlobalVariable.schThreadPool.schedule(this, GlobalVariable.DB_FUNCTION_KEY.DB_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }
}
