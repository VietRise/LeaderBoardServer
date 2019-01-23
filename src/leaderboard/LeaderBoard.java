package leaderboard;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by MacBook on 1/23/19.
 */
public class LeaderBoard
{
    public enum Order
    {
        ASC,
        DESC
    }

    private static final int                    DEFAULT_PAGE_SIZE             = 25;
    private static final String                 DEFAULT_REDIS_HOST            = "127.0.0.1";
    private static final int                    DEFAULT_REDIS_PORT            = 6379;
    private static final String                 DEFAULT_MEMBER_DATA_NAMESPACE = "MemberData";
    private static final boolean                DEFAULT_GLOBAL_MEMBER_DATA    = false;
    private static final List<LeaderBoardData>  EMPTY_LEADER_DATA             = Collections.emptyList();
    private static final String[]               EMPTY_RAW_DATA                = {};

    private JedisPool _jedisPool;
    private String    _leaderboardName;
    private int       _pageSize;
    private Order     _order;
    private String    _memberDataNamespace;
    private boolean   _globalMemberData;

    public LeaderBoard(String leaderboardName, JedisPool jedisPool, int pageSize, Order order, String memberDataNamespace, boolean globalMemberData)
    {
        _leaderboardName = leaderboardName;
        _pageSize = this.getOrDefaultPageSize(pageSize, DEFAULT_PAGE_SIZE);
        _order = (order != null) ? order : Order.DESC;
        _memberDataNamespace = (memberDataNamespace != null && memberDataNamespace.length() > 0) ? memberDataNamespace : DEFAULT_MEMBER_DATA_NAMESPACE;
        _globalMemberData = globalMemberData;
        _jedisPool = jedisPool;
    }

    public String getLeaderboardName()
    {
        return _leaderboardName;
    }

    public int getPageSize()
    {
        return _pageSize;
    }

    public void deleteLeaderboardNamed(String leaderboardName)
    {
        Jedis jedis = null;
        try
        {
            jedis = _jedisPool.getResource();
            Transaction transaction = jedis.multi();
            transaction.del(leaderboardName);
            transaction.del(this.memberDataKey(leaderboardName));
            transaction.exec();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            returnResource(jedis);
        }
    }

    // Update user's score in leaderboard
    public void rankMemberIn(String member, double score, String memberData)
    {
        Jedis jedis = null;
        try
        {
            jedis = _jedisPool.getResource();
            Transaction transaction = jedis.multi();
            transaction.zadd(_leaderboardName, score, member);
            if (memberData != null)
            {
                transaction.hset(this.memberDataKey(_leaderboardName), member, memberData);
            }
            transaction.exec();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            returnResource(jedis);
        }
    }

    // Get rank user
    public Long rankFor(String member)
    {
        return this.rankForIn(_leaderboardName, member);
    }

    // Get leaderboard top user
    public List<LeaderBoardData> leaders(int currentPage, boolean withMemberData)
    {
        return this.leadersIn(currentPage, _pageSize, withMemberData);
    }

    public List<LeaderBoardData> leadersIn(int currentPage, int pageSize, boolean withMemberData)
    {
        if (currentPage < 1)
        {
            currentPage = 1;
        }

        pageSize = this.getOrDefaultPageSize(pageSize, _pageSize);
        int indexForRedis = currentPage - 1;
        int startingOffset = indexForRedis * pageSize < 0 ? 0 : indexForRedis * pageSize;
        int endingOffset = (startingOffset + pageSize) - 1;
        String[] rawLeaderData = this.rangeMethod(_leaderboardName, startingOffset, endingOffset);
        return this.rankedInListIn(_leaderboardName, rawLeaderData, withMemberData);
    }

    private String[] rangeMethod(String leaderboardName, int startingOffset, int endingOffset)
    {
        Jedis jedis = null;
        try
        {
            jedis = _jedisPool.getResource();
            Set<String> rawData;
            if (_order == Order.DESC)
            {
                rawData = jedis.zrevrange(leaderboardName, startingOffset, endingOffset);
            }
            else
            {
                rawData = jedis.zrange(leaderboardName, startingOffset, endingOffset);
            }
            if (rawData == null || rawData.isEmpty())
            {
                return EMPTY_RAW_DATA;
            }
            String[] strings = new String[rawData.size()];
            return rawData.toArray(strings);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            returnResource(jedis);
        }
        return EMPTY_RAW_DATA;
    }

    private List<LeaderBoardData> rankedInListIn(String leaderboardName, String[] members, boolean withMemberData)
    {
        if (members == null || members.length == 0)
        {
            return EMPTY_LEADER_DATA;
        }
        List<LeaderBoardData> ranksForMembers = new ArrayList<>();
        for (String member : members)
        {
            LeaderBoardData leaderData = this.scoreAndRankForIn(leaderboardName, member);
            ranksForMembers.add(leaderData);
        }
        if (withMemberData)
        {
            int index = 0;
            List<String> membersData = this.membersDataForIn(leaderboardName, members);
            for (String memberData : membersData)
            {
                ranksForMembers.get(index).setOldRank(memberData);
                index++;
            }
        }
        return ranksForMembers;
    }

    private LeaderBoardData scoreAndRankForIn(String leaderboardName, String member)
    {
        Double score = this.scoreForIn(leaderboardName, member);
        Long   rank  = this.rankForIn(leaderboardName, member);
        return new LeaderBoardData(member, score, rank);
    }

    private Double scoreForIn(String leaderboardName, String member)
    {
        Jedis jedis = null;
        try
        {
            jedis = _jedisPool.getResource();
            return jedis.zscore(leaderboardName, member);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            returnResource(jedis);
        }
        return null;
    }

    private Long rankForIn(String leaderboardName, String member)
    {
        Jedis jedis = null;
        try
        {
            jedis = _jedisPool.getResource();
            Long rank;
            if (_order == Order.DESC)
            {
                rank = jedis.zrevrank(leaderboardName, member);
            }
            else
            {
                rank = jedis.zrank(leaderboardName, member);
            }
            if (rank != null)
            {
                return rank + 1;
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            returnResource(jedis);
        }
        return null;
    }

    private List<String> membersDataForIn(String leaderboardName, String[] members)
    {
        Jedis jedis = null;
        try
        {
            jedis = _jedisPool.getResource();
            return  jedis.hmget(this.memberDataKey(leaderboardName), members);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            returnResource(jedis);
        }
        return null;
    }

    private void returnResource(Jedis jedis)
    {
        if (jedis != null)
        {
            jedis.close();
        }
    }

    private String memberDataKey(String leaderboardName)
    {
        if (!_globalMemberData)
        {
            return leaderboardName + ":" + _memberDataNamespace;
        }
        return _memberDataNamespace;
    }

    private int getOrDefaultPageSize(int pageSize, int defaultPageSize)
    {
        return (pageSize > 0) ? pageSize : defaultPageSize;
    }
}
