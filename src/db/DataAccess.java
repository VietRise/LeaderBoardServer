package db;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import common.ConfigPath;
import common.GlobalVariable;
import common.KeyGenerator;
import data.UserProfile;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by MacBook on 1/23/19.
 */
public class DataAccess
{
    private static DataAccess instance = new DataAccess();
    private ConnectionManager connectionManager;

    public static DataAccess getInstance()
    {
        return instance;
    }

    private DataAccess()
    {
        connectionManager = new ConnectionManager();
        try
        {
            loadConfig();
            connectionManager.getConnection(GlobalVariable.DB_FUNCTION_KEY.GAME_STATIC);
            for (String[] params : GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA)
                connectionManager.getConnection(params);
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
    }

    private void loadConfig() throws Exception
    {
        String      strJson = new String(Files.readAllBytes(Paths.get(ConfigPath.db)), StandardCharsets.UTF_8);
        JSONObject objJson = new JSONObject(strJson.trim());
        Iterator<?> buckets = objJson.keys();
        while (buckets.hasNext())
        {
            String bucketName = (String) buckets.next();
            Field field      = GlobalVariable.DB_FUNCTION_KEY.class.getDeclaredField(bucketName);
            if (field != null)
            {
                JSONObject objBucket = (JSONObject) objJson.get(bucketName);
                if (!objBucket.has("buckets")) // single node bucket
                {
                    String[] bucket = new String[]{
                            objBucket.getString("name"), objBucket.getString("pass"), objBucket.getString("url")
                    };
                    field.set(GlobalVariable.DB_FUNCTION_KEY.class, bucket);
                }
                else // multi node bucket
                {
                    JSONArray arrBuckets = objBucket.getJSONArray("buckets");
                    int        numBucket  = arrBuckets.length();
                    String[][] strBuckets = new String[numBucket][];
                    for (int i = 0; i < numBucket; i++)
                    {
                        JSONObject nodeBucket = (JSONObject) arrBuckets.get(i);
                        strBuckets[i] = new String[]{
                                nodeBucket.getString("name"), nodeBucket.getString("pass"), nodeBucket.getString("url")
                        };
                    }
                    field.set(GlobalVariable.DB_FUNCTION_KEY.class, strBuckets);
                }
            }
            else
            {
                throw new Exception("Unknown couchbase bucket name: " + bucketName);
            }
        }
        GlobalVariable.DB_FUNCTION_KEY.DEBUG_BUCKET_NAMES.put(GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA[0], GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA[0][0]);
    }

    public long getNextUserID(String key)
    {
        CBConnection connection = connectionManager.getConnection(GlobalVariable.DB_FUNCTION_KEY.GAME_STATIC);
        return connection.increase(key, 1, 100001L);
    }

    // Get user profile
    public boolean addUserProfile(UserProfile userProfile, int pool) throws Exception
    {
        if (userProfile != null)
        {
            CBConnection connection = connectionManager.getConnection(GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA[pool]);
            if (connection.addData(KeyGenerator.getKeyProfile(userProfile.getId()), userProfile.toJson())) {
                return true;
            }
        }
        return false;
    }

    public UserProfile getUserProfile(String userID, int pool)
    {
        CBConnection connection = connectionManager.getConnection(GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA[pool]);
        return connection.getValueJson(KeyGenerator.getKeyProfile(userID), UserProfile.class);
    }

    public boolean updateUserProfile(String userID, UserProfile userProfile, int pool) throws Exception
    {
        CBConnection connection = connectionManager.getConnection(GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA[pool]);
        return connection.setData(KeyGenerator.getKeyProfile(userID), userProfile.toJson());
    }

    public boolean deleteUserProfile(String userID, int pool)
    {
        CBConnection connection = connectionManager.getConnection(GlobalVariable.DB_FUNCTION_KEY.USER_GAME_DATA[pool]);
        return connection.delete(userID);
    }
}
