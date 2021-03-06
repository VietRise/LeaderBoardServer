package connection.http;

import common.TimeUtils;
import common.Utilities;
import data.UserProfile;
import db.DataAccess;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import leaderboard.LeaderBoard;
import leaderboard.LeaderBoardData;
import leaderboard.LeaderBoardManager;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by MacBook on 1/23/19.
 */
public class HttpMessageHandler extends SimpleChannelInboundHandler<Object>
{
    private final static String GET_TOP_USER = "topuser";
    private final static String POST_SCORE_USER = "postscore";
    private final static String POST_UPDATE_USER = "updateuser";

    private final static String SUCCESS = "Success";
    private final static String FAIL = "Fail";
    private final static HashSet<String> ALLOWED_ORIGINS = new HashSet<String>()
    {{
        add("http://localhost");
        add("http://127.0.0.1");
    }};

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
        if (evt instanceof IdleStateEvent)
        {
            ctx.close();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        TimeUtils.currentTimeMillis = System.currentTimeMillis();
        if (msg instanceof HttpRequest)
        {
            try
            {
                HttpRequest request = (HttpRequest) msg;
                HttpUtil.setKeepAlive(request, false);
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                String clientIp = ((InetSocketAddress)ctx.channel().remoteAddress()).getHostString();
                HashMap<String, String> getParams = Utilities.getQueryMap(request.uri(), true);
                String result = "";
                if (getParams.containsKey(GET_TOP_USER))
                {
                    // Response leaderboard
                    HttpTopRank.getTopUser(response, clientIp);
                    System.out.println("Get top user successful");
                }
                else if (getParams.containsKey(POST_SCORE_USER))
                {
                    // Save score to leaderboard
                    String userID = getParams.get("userid");
                    long score = Long.valueOf(getParams.get("score"));
                    DataAccess dataAccess = DataAccess.getInstance();
                    UserProfile userProfile = dataAccess.getUserProfile(userID, 0);
                    if (userProfile != null)
                    {
                        long oldScore = userProfile.getHighscore();
                        if (score > oldScore)
                        {
                            // Update db
                            userProfile.setHighscore(score);
                            dataAccess.updateUserProfile(userID, userProfile, 0);
                            // Update leaderboard
                            LeaderBoardManager.getInstance().rankMember(LeaderBoardManager.LeaderBoardType.LEADERBOARD_TYPE_SCORE_USER_VALUE, userID, score);
                            System.out.println("Post score user successful");
                            // Response data
                            result = SUCCESS;
                            response.content().writeBytes(result.getBytes());
                        }
                    }
                    else
                    {
                        // Response data
                        result = FAIL;
                        response.content().writeBytes(result.getBytes());
                        System.out.println("This user isn't exist");
                    }
                }
                else if (getParams.containsKey(POST_UPDATE_USER))
                {
                    String userID = getParams.get("userid");
                    String name = getParams.get("name");
                    DataAccess dataAccess = DataAccess.getInstance();
                    UserProfile userProfile = dataAccess.getUserProfile(userID, 0);
                    if (userProfile != null)
                    {
                        // Update db
                        userProfile.setName(name);
                        dataAccess.updateUserProfile(userID, userProfile, 0);
                        // Data response
                        result = SUCCESS;
                        response.content().writeBytes(result.getBytes());
                        System.out.println("Update user successful");
                    }
                    else
                    {
                        // Response data
                        result = FAIL;
                        response.content().writeBytes(result.getBytes());
                        System.out.println("This user isn't exist");
                    }
                }
                ctx.writeAndFlush(response);
            }
            finally
            {
                ctx.close();
            }

        }
    }
}
