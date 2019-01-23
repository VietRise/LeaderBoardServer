package data;

import common.JsonUtils;

/**
 * Created by MacBook on 1/23/19.
 */

public class UserProfile
{
    private String id;
    private String name;
    private long highscore;

    public UserProfile(String id, String name, long highscore)
    {
        this.id = id;
        this.name = name;
        this.highscore = highscore;
    }

    public String getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setHighscore(long highscore)
    {
        this.highscore = highscore;
    }

    public long getHighscore()
    {
        return highscore;
    }

    public String toJson()
    {
        return JsonUtils.toJson(this, this.getClass());
    }
}
