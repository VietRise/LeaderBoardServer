package common;

/**
 * Created by MacBook on 1/23/19.
 */
public class TimeUtils
{
    public static final int SECOND_IN_1_MINUTE = 60;
    public static final int SECOND_IN_1_HOUR   = 60 * SECOND_IN_1_MINUTE;
    public static final int SECOND_IN_1_DAY    = 24 * SECOND_IN_1_HOUR;
    public static final int SECOND_IN_7_DAY    = 7 * SECOND_IN_1_DAY;
    public static final int SECOND_IN_14_DAY   = 14 * SECOND_IN_1_DAY;
    public static final int SECOND_IN_30_DAY   = 30 * SECOND_IN_1_DAY;
    public static final int SECOND_IN_31_DAY   = 31 * SECOND_IN_1_DAY;

    public static long currentTimeMillis = System.currentTimeMillis();

    public static int currentTimeSeconds()
    {
        return getTimeSecondOf(currentTimeMillis);
    }

    public static int getTimeSecondOf(long millis)
    {
        return (int) (millis / 1_000);
    }
}
