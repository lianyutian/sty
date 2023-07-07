package com.lm.sty.netty.demo.handler;

import com.lm.sty.netty.demo.message.GroupMembersRequestMessage;
import com.lm.sty.netty.demo.message.GroupMembersResponseMessage;
import com.lm.sty.netty.demo.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * @author liming
 * @version 1.0
 * @since 2023/7/7 9:58
 */
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        Set<String> members = GroupSessionFactory.getGroupSession()
                .getMembers(msg.getGroupName());
        ctx.writeAndFlush(new GroupMembersResponseMessage(members));
    }
}
