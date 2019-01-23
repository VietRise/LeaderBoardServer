package common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by MacBook on 1/23/19.
 */
public class ConfigPath
{
    private static final String CONFIG_FOLDER = "./data/config/";
    private static final String CONFIG_PATHS = StringUtils.join(CONFIG_FOLDER, "configPaths.json");

    private static final String KEY_SERVICES   = "SERVICES";
    private static final String KEY_DB         = "DB";
    private static final String KEY_REDIS      = "REDIS";

    public static String services;
    public static String db;
    public static String redis;

    public static void load(ServiceInfo.Environment env)
    {
        JsonObject jsonObject;
        try
        {
            jsonObject = JsonUtils.parseFile(CONFIG_PATHS).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
            {
                JsonObject jsonData = entry.getValue().getAsJsonObject();
                String     path     = StringUtils.join(CONFIG_FOLDER, jsonData.get(env.name()).getAsString());
                String     key      = entry.getKey();
                switch (key)
                {
                    case KEY_SERVICES:
                        services = path;
                        break;
                    case KEY_DB:
                        db = path;
                        break;
                    case KEY_REDIS:
                        redis = path;
                        break;
                    default:
                        throw new Exception("This key [" + key + "] doesn't implement.");
                }
            }
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
    }
}
