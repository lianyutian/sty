package com.lm.sty.netty.demo.server;

import com.lm.sty.netty.demo.handler.ChatRequestMessageHandler;
import com.lm.sty.netty.demo.handler.GroupChatRequestMessageHandler;
import com.lm.sty.netty.demo.handler.GroupCreateRequestMessageHandler;
import com.lm.sty.netty.demo.handler.GroupJoinRequestMessageHandler;
import com.lm.sty.netty.demo.handler.LoginRequestMessageHandler;
import com.lm.sty.netty.demo.handler.QuitHandler;
import com.lm.sty.netty.demo.message.GroupCreateResponseMessage;
import com.lm.sty.netty.demo.message.LoginRequestMessage;
import com.lm.sty.netty.demo.message.LoginResponseMessage;
import com.lm.sty.netty.demo.protocol.MessageCodecSharable;
import com.lm.sty.netty.demo.protocol.ProtocolFrameDecoder;
import com.lm.sty.netty.demo.server.service.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoginRequestMessageHandler LOGIN_MESSAGE_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHART_MESSAGE_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_MESSAGE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_MESSAGE_HANDLER = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_MESSAGE_HANDLER = new GroupJoinRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(LOGIN_MESSAGE_HANDLER);
                    ch.pipeline().addLast(CHART_MESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_MESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_MESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_MESSAGE_HANDLER);
                    ch.pipeline().addLast(QUIT_HANDLER);
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了读空闲事件
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("已经 5s 没有读到数据了");
                                ctx.channel().close();
                            }
                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
