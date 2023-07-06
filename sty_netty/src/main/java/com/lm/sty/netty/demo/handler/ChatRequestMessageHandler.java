package com.lm.sty.netty.demo.handler;

import com.lm.sty.netty.demo.message.ChatRequestMessage;
import com.lm.sty.netty.demo.message.ChatResponseMessage;
import com.lm.sty.netty.demo.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author liming
 * @version 1.0
 * @since 2023/7/6 17:13
 */
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String to = msg.getTo();
        String content = msg.getContent();

        Channel channel = SessionFactory.getSession().getChannel(to);

        // 对方在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(from, content));
        } else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方不在线"));
        }
    }
}
