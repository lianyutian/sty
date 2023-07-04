package com.lm.sty.netty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author liming
 * @version 1.0
 * @since 2023/7/4 16:58
 */
public class HelloClient {

    static final Logger log = LoggerFactory.getLogger(HelloClient.class);

    public static void main(String[] args) {

        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            log.debug("connected...");
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("sending...");
                                    Random random = new Random();
                                    char c = 'a';
                                    for (int i = 0; i < 10; i++) {
                                        ByteBuf buf = ctx.alloc().buffer();
                                        buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
                                        ctx.writeAndFlush(buf);
                                    }
                                }
                            });
                        }
                    }).connect("localhost", 8080).sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
