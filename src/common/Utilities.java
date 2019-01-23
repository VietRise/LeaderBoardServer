package common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by MacBook on 1/23/19.
 */
public class Utilities
{
    public static final String STRING_UTF8 = "utf8";

    public static boolean createFolder(String path)
    {
        try
        {
            File file = new File(path);
            if (!file.exists())
            {
                return file.mkdir();
            }
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        return false;
    }

    public static boolean deleteFileOrFolder(String strFolder)
    {
        try
        {
            File file = new File(strFolder);
            return file.delete();
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
        return false;
    }

    public static HashMap<String, String> getQueryMap(String query, boolean isGet) throws UnsupportedEncodingException
    {
        if(isGet)
            query = query.replace('/', '&').replace('?', '&');
        String[] params = query.split("&");
        HashMap<String, String> map = new LinkedHashMap<>();
        for (String param : params)
        {
            String[] pars = param.split("=");
            String name = pars[0];
            String value = pars.length > 1 ? java.net.URLDecoder.decode(pars[1], STRING_UTF8) : StringUtils.EMPTY_STRING;
            if (!name.equals(StringUtils.EMPTY_STRING)) {
                map.put(name, value);
            }
        }
        return map;
    }
}
