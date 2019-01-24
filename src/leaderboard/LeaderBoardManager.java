package leaderboard;

import common.GlobalVariable;
import db.RedisController;
import redis.clients.jedis.JedisPool;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by MacBook on 1/23/19.
 */
public class LeaderBoardManager
{
    public enum LeaderBoardType
    {
        LEADERBOARD_TYPE_SCORE_USER_VALUE
    }

    public static final  int PAGE_SIZE = 100;
    private static final int MAX_RANK = 100;
    private static final int MAX_RANK_OF_TOP_USER  = 100;
    public static final String KEY_LEADERBOARD_TROPHY_USER  = "LeaderboardScoreUser";

    private LeaderBoard lbScoreUser;
    private HashMap<Integer, Boolean> dataChanged       = new HashMap<>();

    private static Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            try
            {
                instance.doCacheScoreUser();
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            GlobalVariable.schThreadPool.schedule(this, 5, TimeUnit.SECONDS);
        }
    };

    public void start()
    {
        System.out.println("------ LEADERBOARD STATUS STARTING --------");
        JedisPool jedisPool = RedisController.getInstance().getJedisPool();

        lbScoreUser = new LeaderBoard(KEY_LEADERBOARD_TROPHY_USER, jedisPool, PAGE_SIZE, LeaderBoard.Order.DESC, null, false);

        this.setDataChanged(LeaderBoardType.LEADERBOARD_TYPE_SCORE_USER_VALUE, true);

        System.out.printf(lbScoreUser.getLeaderboardName(), "STARTED", lbScoreUser.getPageSize());

        GlobalVariable.schThreadPool.schedule(updateTask, 5, TimeUnit.SECONDS);

        System.out.println("------ LEADERBOARD STATUS RUNNING --------");
    }

    public void stop()
    {
        System.out.println("------ LEADERBOARD STATUS STOPPING --------");
        RedisController.getInstance().getJedisPool().close();
        System.out.println("------ LEADERBOARD STATUS STOPPED --------");
    }

    public LeaderBoard getLeaderBoard(LeaderBoardType leaderboardType)
    {
        switch (leaderboardType)
        {
            case LEADERBOARD_TYPE_SCORE_USER_VALUE:
                return lbScoreUser;
        }
        return null;
    }

    public void rankMemberIn(LeaderBoardType leaderBoardType, String member, double score)
    {
        LeaderBoard leaderBoard = getLeaderBoard(leaderBoardType);
        if (leaderBoard != null)
        {
            long oldRank = leaderBoard.rankFor(member);
            String memberData = LeaderBoardData.getMemberData(oldRank);

            leaderBoard.rankMemberIn(member, score, memberData);
            this.setDataChanged(leaderBoardType, true);
        }
    }

    private void doCacheScoreUser() throws Exception
    {
        LeaderBoardType leaderBoardType = LeaderBoardType.LEADERBOARD_TYPE_SCORE_USER_VALUE;
        LeaderBoard leaderBoard = this.getLeaderBoard(leaderBoardType);
        if (leaderBoard != null)
        {
            List<LeaderBoardData> leaderBoardDataList = leaderBoard.leaders(1, true);
            if (leaderBoardDataList != null)
            {
                this.setDataChanged(leaderBoardType, false);
            }
        }
    }


    public void setDataChanged(LeaderBoardType leaderBoardType, boolean isChange)
    {
        this.dataChanged.put(leaderBoardType.ordinal(), isChange);
    }

    private static final LeaderBoardManager instance = new LeaderBoardManager();
    public static LeaderBoardManager getInstance()
    {
        return instance;
    }
}
