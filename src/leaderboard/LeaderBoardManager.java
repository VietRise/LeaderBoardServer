package leaderboard;

import common.GlobalVariable;
import db.RedisController;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    private List<LeaderBoardData> cachedListUser        = null;

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

    public List<LeaderBoardData> getCachedListUser()
    {
        return cachedListUser;
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

    public void rankMember(LeaderBoardType leaderBoardType, String member, double score)
    {
        addRankMemberTransaction(leaderBoardType, member, score);
    }

    private static void addRankMemberTransaction(LeaderBoardType leaderboardType, String member, double score)
    {
        rankMemberQueue.add(RankMemberTransaction.borrowObject(leaderboardType, member, score));
        GlobalVariable.exeThreadPool.execute(rankMemberTask);
    }

    private void rankMemberTo(LeaderBoardType leaderboardType, String member, double score)
    {
        LeaderBoard leaderBoard = getLeaderBoard(leaderboardType);
        if (leaderBoard != null)
        {
            List<LeaderBoardData> oldLeaders = new ArrayList<>(leaderBoard.leaders(1, false));
            Long oldRank = leaderBoard.rankFor(member);
            leaderBoard.rankMemberIn(member, score, LeaderBoardData.getMemberData(oldRank));
            Long newRankObj = leaderBoard.rankFor(member);
            if (newRankObj != null)
            {
                long newRank = newRankObj;
                if (oldRank == null || oldRank != newRank)
                {
                    for (LeaderBoardData oldLeader: oldLeaders)
                    {
                        if (oldLeader.getRank() > 0)
                        {
                            leaderBoard.updateMemberData(member, LeaderBoardData.getMemberData(oldLeader.getRank()));
                        }
                    }
                }
                if (leaderBoard.getPageSize() > newRank)
                {
                    this.setDataChanged(leaderboardType, true);
                }
                int maxRank = getMaxRank(leaderboardType);
                leaderBoard.removeMembersOutsideRank(maxRank);
            }
        }

    }

    private void doCacheScoreUser() throws Exception
    {
        LeaderBoardType leaderBoardType = LeaderBoardType.LEADERBOARD_TYPE_SCORE_USER_VALUE;
        LeaderBoard leaderBoard = this.getLeaderBoard(leaderBoardType);
        if (leaderBoard != null && isDataChanged(leaderBoardType))
        {
            List<LeaderBoardData> leaderBoardDataList = leaderBoard.leaders(1, true);
            if (leaderBoardDataList != null)
            {
                cachedListUser = leaderBoardDataList;
                this.setDataChanged(leaderBoardType, false);
            }
        }
    }


    public void setDataChanged(LeaderBoardType leaderBoardType, boolean isChange)
    {
        this.dataChanged.put(leaderBoardType.ordinal(), isChange);
    }

    public boolean isDataChanged(LeaderBoardType leaderboardType)
    {
        Boolean b = this.dataChanged.get(leaderboardType.ordinal());
        return (b != null && b);
    }

    private int getMaxRank(LeaderBoardType leaderboardType)
    {
        switch (leaderboardType)
        {
            case LEADERBOARD_TYPE_SCORE_USER_VALUE:
                return MAX_RANK_OF_TOP_USER;
        }
        return MAX_RANK;
    }

    private static final LeaderBoardManager instance = new LeaderBoardManager();
    public static LeaderBoardManager getInstance()
    {
        return instance;
    }

    // ------------- RankMemberTransaction ----------------
    private static Queue<RankMemberTransaction> rankMemberQueue = new ConcurrentLinkedQueue<>();
    private static Runnable rankMemberTask = () ->
    {
        try
        {
            RankMemberTransaction transaction = rankMemberQueue.poll();
            if (transaction != null)
            {
                LeaderBoardManager.getInstance().rankMemberTo(transaction.leaderboardType, transaction.member, transaction.score);
                RankMemberTransaction.returnObject(transaction);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    };
    private static class RankMemberTransaction
    {
        LeaderBoardType leaderboardType;
        String member;
        double score;

        RankMemberTransaction(LeaderBoardType leaderboardType, String member, double score)
        {
            this.leaderboardType = leaderboardType;
            this.member = member;
            this.score = score;
        }

        private static final int OBJECT_POOL_MAX = 256;
        private static Queue<RankMemberTransaction> objectPool = new ConcurrentLinkedQueue<>();
        public static RankMemberTransaction borrowObject(LeaderBoardType leaderboardType, String member, double score)
        {
            RankMemberTransaction request = objectPool.poll();
            if (request != null)
            {
                request.leaderboardType = leaderboardType;
                request.member = member;
                request.score = score;
                return request;
            }
            return new RankMemberTransaction(leaderboardType, member, score);
        }

        public static void returnObject(RankMemberTransaction request)
        {
            if (request != null && objectPool.size() < OBJECT_POOL_MAX)
            {
                objectPool.add(request);
            }
        }
    }
}
