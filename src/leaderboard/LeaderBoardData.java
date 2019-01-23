package leaderboard;

import com.google.gson.JsonObject;
import common.JsonUtils;

/**
 * Created by MacBook on 1/23/19.
 */
public class LeaderBoardData
{
    private static final String PROPERTY_OLD_RANK = "OldRank";

    private String member;
    private double score;
    private long rank;
    private long oldRank;

    public LeaderBoardData(String member, double score, long rank)
    {
        this.member = member;
        this.score = score;
        this.rank = rank;
        this.oldRank = 0;
    }

    public void setMember(String member)
    {
        this.member = member;
    }

    public String getMember()
    {
        return this.member;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public double getScore()
    {
        return this.score;
    }

    public void setRank(long rank)
    {
        this.rank = rank;
    }

    public long getRank()
    {
        return this.rank;
    }

    public long getOldRank()
    {
        return this.oldRank;
    }

    public void setOldRank(String memberData)
    {
        this.oldRank = 0;
        if (memberData != null)
        {
            JsonObject jsonObject = JsonUtils.parse(memberData).getAsJsonObject();
            if (jsonObject.has(PROPERTY_OLD_RANK))
            {
                this.oldRank = jsonObject.get(PROPERTY_OLD_RANK).getAsLong();
            }
        }
    }

    public static String getMemberData(Long oldRank)
    {
        if (oldRank != null)
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(PROPERTY_OLD_RANK, oldRank);
            return jsonObject.toString();
        }
        return null;
    }
}
