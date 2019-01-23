package connection.http;

import common.JsonUtils;
import common.TimeUtils;
import data.UserProfile;
import db.DataAccess;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import leaderboard.LeaderBoard;
import leaderboard.LeaderBoardData;
import leaderboard.LeaderBoardManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by MacBook on 1/23/19.
 */
public class HttpTopRank
{
    private final static HashSet<String> ALLOW_GET_TOP_IPS = new HashSet<String>()
    {{
        add("127.0.0.1");
    }};

    private static TopData jsonTopUser = new TopUser();

    public static void getTopUser(DefaultFullHttpResponse response, String clientIp)
    {
        if (ALLOW_GET_TOP_IPS.contains(clientIp))
        {
            response.content().writeBytes(jsonTopUser.getData(TimeUtils.currentTimeSeconds()));
            return;
        }
        String strError = "Not allow stranger IP: " + clientIp;
        response.content().writeBytes(strError.getBytes());
    }

    static abstract class TopData
    {
        final static int RELOAD_INTERVAL = 5 * TimeUtils.SECOND_IN_1_MINUTE; // 5 minute
        int loadAt;
        byte[] data;

        public TopData()
        {
            data = getData(TimeUtils.currentTimeSeconds());
        }

        abstract byte[] getData(int second);
    }

    static class TopUser extends TopData
    {
        @Override byte[] getData(int second)
        {
            if(second - loadAt >= RELOAD_INTERVAL)
            {
                loadAt = second;
                LeaderBoard leaderBoard = LeaderBoardManager.getInstance().getLeaderBoard(LeaderBoardManager.LeaderBoardType.LEADERBOARD_TYPE_SCORE_USER_VALUE);
                List<LeaderBoardData> leaderBoardDataList = leaderBoard.leaders(1, false);
                if (leaderBoardDataList != null)
                {
                    try
                    {
                        ArrayList<UserProfile> topUser = new ArrayList<>(leaderBoardDataList.size());
                        DataAccess dataAccess = DataAccess.getInstance();
                        for (LeaderBoardData leaderBoardData : leaderBoardDataList)
                        {
                            UserProfile userProfile = dataAccess.getUserProfile(leaderBoardData.getMember(), 0);
                            if (userProfile == null) {
                                userProfile = new UserProfile(leaderBoardData.getMember(), leaderBoardData.getMember(), (long) leaderBoardData.getScore());
                                dataAccess.addUserProfile(userProfile, 0);
                            }
                            topUser.add(userProfile);
                        }
                        data = JsonUtils.toJson(topUser).getBytes();
                    }
                    catch (Exception ex)
                    {
                        System.out.printf(ex.getMessage() + " -- TopUser getData");
                    }
                }
            }
            return data;
        }
    }
}
