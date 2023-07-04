package com.lm.sty.netty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * 固定长度解决半包粘包问题
 * 让所有数据包长度固定（假设长度为 8 字节）
 *
 * @author liming
 * @version 1.0
 * @since 2023/7/4 17:33
 */
@Slf4j
public class Resove2Client {
    public static void main(String[] args) {

        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("sending...");
                                    // 发送内容随机的数据包
                                    Random r = new Random();
                                    char c = 'a';
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    for (int i = 0; i < 10; i++) {
                                        byte[] bytes = new byte[8];
                                        for (int j = 0; j < r.nextInt(8); j++) {
                                            bytes[j] = (byte) c;
                                        }
                                        c++;
                                        buffer.writeBytes(bytes);
                                    }
                                    ctx.writeAndFlush(buffer);
                                }
                            });
                        }
                    }).connect(new InetSocketAddress("localhost", 8080)).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

}
