package db;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import common.StringUtils;
import common.Utilities;
import common.GlobalVariable;

public class CBConnectionFile extends CBConnection
{
	private String m_strFolderName = null;
    public CBConnectionFile(String[] functionKey)
    {
    	super(functionKey);
    	if (m_strFolderName == null)
    	{
            initialize();
    	}
    	
    }

    private static String s_strSavedFolder = "./_database";
    public static void updateSaveFolder(String strFolder)
    {
    	s_strSavedFolder = strFolder;
    }
    
    protected void initialize()
    {
        try
        {
        	if (GlobalVariable.DB_FUNCTION_KEY.DEBUG_BUCKET_NAMES.containsKey(functionKey))
        	{
        		m_strFolderName = StringUtils.join(s_strSavedFolder, StringUtils.DELIMITER_SLASH, GlobalVariable.DB_FUNCTION_KEY.DEBUG_BUCKET_NAMES.get(functionKey));
        	}
        	if (m_strFolderName == null)
        		m_strFolderName = StringUtils.join(s_strSavedFolder, StringUtils.DELIMITER_SLASH, functionKey[0]);

            // Save folder
        	Utilities.createFolder(s_strSavedFolder);
      		Utilities.createFolder(m_strFolderName);
        }
        catch (Exception ex)
        {
        	System.out.printf(ex.getMessage());
        }
    }

    private Object get(String key)
    {
    	try
        {
            String pathData = StringUtils.join(m_strFolderName, StringUtils.DELIMITER_SLASH, getDBKey(key));
            Path path = Paths.get(pathData);
            if (Files.isRegularFile(path))
            {
                return Files.readAllBytes(path);
            }
        }
        catch(Exception ex)
        {
        	System.out.printf(ex.getMessage());
        }
        return null;
    }

    public String getDBKey(String key)
    {
    	return key.replaceAll(":", "___");
    }

    public <T> T getValue(String key) throws Exception
    {
    	byte[] data = (byte[]) get(key);
    	if (data != null)
    	{
    		return (T)new String(data, "UTF-8");
    	}
    	return null;
    }

    public Map<String, Object> getMulti(Collection<String> keys)
    {
    	Map<String, Object> values = null;
        try
        {
            values = new HashMap<>();
            for (String key : keys)
            {
                values.put(key, getValue(key));
            }
        }
        catch(Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        return values;
    }

    public boolean delete(String key)
    {
    	try
    	{
    		return Utilities.deleteFileOrFolder(StringUtils.join(m_strFolderName, StringUtils.DELIMITER_SLASH, getDBKey(key)));
    	}
    	catch(Exception ex)
    	{
            System.out.printf(ex.getMessage());
    	}
    	return false;
    }

    public boolean setData(String key, Object value)
    {
    	return setData(key, value, -1);
    }

    public boolean setData(String key, Object value, int expired)
    {
    	if (value instanceof byte[])
    	{
    		return set(key, (byte[])value, expired);
    	}
    	if (value instanceof String)
    	{
    		try
    		{
    			return set(key, ((String)value).getBytes("UTF-8"), expired);
    		}
    		catch(Exception ex)
    		{
                System.out.printf(ex.getMessage());
    		}
    	}
    	return false;
    }

    private boolean set(String key, byte[] data, int expired)
    {
    	try
        {
            if (data == null)
            {
                return false;
            }

            String strOutput = StringUtils.join(m_strFolderName, StringUtils.DELIMITER_SLASH, getDBKey(key));
            // Remove old file
            //delete(strOutput);

            // Write new file
            FileOutputStream fos = new FileOutputStream(strOutput);
            fos.write(data);
            fos.close();
            fos = null;
        }
        catch(Exception ex)
        {
            System.out.printf(ex.getMessage());
            return false;
        }
        return true;
    }

    public boolean setDataCas(String key, long cas, Object value)
    {
    	try
    	{
    		return setData(key, value);
    	}
    	catch(Exception ex)
    	{
            System.out.printf(ex.getMessage());
    	}
    	
    	return false;
    }

    public boolean addData(String key, Object value)
    {
  		return addData(key, value, 0);
    }

    public boolean addData(String key, Object value, int expire)
    {
    	File file = new File(StringUtils.join(m_strFolderName, StringUtils.DELIMITER_SLASH, getDBKey(key)));
    	return (!file.exists()) && setData(key, value, expire);
    }

    public long increase(String key, int offset, long defaultValue)
    {
    	try
    	{
    		long lVal = getLong(key);
    		if (lVal > 0L)
    		{
    			lVal += offset;
    		}
    		else
    		{
    			lVal = defaultValue;
    		}
    		String value = String.valueOf(lVal);
			set(key, value.getBytes(), -1);
			return lVal;
    	}
    	catch(Exception ex)
    	{
            System.out.printf(ex.getMessage());
    	}
    	
    	return defaultValue;
    }

    public boolean replaceData(String key, Object value, int expire)
    {
    	return setData(key, value);
    }

    public long getLong(String key)
    {
    	try
    	{
    	    byte[] raw = (byte[]) get(key);
    	    if (raw != null)
            {
                String value = new String(raw, "UTF-8");
                return Long.parseLong(value);
            }
    	}
    	catch(Exception ex)
    	{
            System.out.printf(ex.getMessage());
    	}
        return 0L;
    }
    
    public void shutDown()
    {
        
    }
}
