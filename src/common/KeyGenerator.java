package common;

/**
 * Created by MacBook on 1/23/19.
 */
public class KeyGenerator
{
    private static final String KEY_PROFILE        = "_profile";

    public static String getKeyProfile(String userID)
    {
        return StringUtils.join(userID, KEY_PROFILE);
    }
}
