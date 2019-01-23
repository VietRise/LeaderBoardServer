package common;

/**
 * Created by MacBook on 1/23/19.
 */
public class StringUtils
{
    public static final String EMPTY_STRING             = "";
    public static final String DELIMITER_COLON          = ":";
    public static final String DELIMITER_UNDERSCORE     = "_";
    public static final String DELIMITER_SLASH          = "/";

    private static final ThreadLocal<StringBuilder> stringBuilder = new ThreadLocal<StringBuilder>()
    {
        @Override protected StringBuilder initialValue()
        {
            return new StringBuilder();
        }

        @Override public StringBuilder get()
        {
            StringBuilder b = super.get();
            b.setLength(0); // clear/reset the buffer
            return b;
        }
    };

    public static String join(Object... objects)
    {
        if (objects != null && objects.length > 0)
        {
            StringBuilder sb = stringBuilder.get();
            for (Object object : objects)
            {
                sb.append(object);
            }
            return sb.toString();
        }
        return EMPTY_STRING;
    }
}
