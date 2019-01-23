package db;
import java.util.Collection;
import java.util.Map;
import common.JsonUtils;
import common.StringUtils;

public abstract class CBConnection
{
    protected String[] functionKey;

    public CBConnection(String[] functionKey)
    {
    	this.functionKey = functionKey;
        initialize();
    }

    protected abstract void initialize();
    public abstract <T> T getValue(String key) throws Exception;
    public abstract Map<String, Object> getMulti(Collection<String> keys);
    public abstract boolean delete(String key);
    public abstract boolean setData(String key, Object value) throws Exception;
    public abstract boolean setData(String key, Object value, int expire) throws Exception;
    public abstract boolean setDataCas(String key, long cas, Object value);
    public abstract boolean addData(String key, Object value) throws Exception;
    public abstract boolean addData(String key, Object value, int expire) throws Exception;//expiry based on second
    public abstract long increase(String key, int offset, long defaultValue);
    public abstract boolean replaceData(String key, Object value, int expire) throws Exception;
    public abstract long getLong(String key);
    public abstract void shutDown();

    public <T> T getValueJson(String key, Class<T> object)
    {
        try
        {
            if (object == null)
            {
                throw new Exception("Class object is null.");
            }
            String bytes = getValue(key);
            if (bytes != null && !StringUtils.EMPTY_STRING.equals(bytes))
            {
                return JsonUtils.fromJson(bytes, object);
            }
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        return null;
    }

    public <T> T getValueJsonData(String bytes, Class<T> object)
    {
        try
        {
            if (object == null)
            {
                throw new Exception("Class object is null.");
            }
            if (bytes != null && !"".equals(bytes))
            {
                return JsonUtils.fromJson(bytes, object);
            }
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        return null;
    }
}
