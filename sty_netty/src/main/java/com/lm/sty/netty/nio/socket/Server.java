package com.lm.sty.netty.nio.socket;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        // 1. 使用 nio 来理解阻塞模式，单线程
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 2. 创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 3. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 4. 连接集合
        List<SocketChannel> channelList = new ArrayList<>();

        while (true) {

            // 5. accept 建立与客户端连接，SocketChannel 用来与客户端之间通信
            log.debug("connecting...");

            SocketChannel socketChannel = serverSocketChannel.accept(); // 阻塞方法，线程停止运行

            log.debug("connected.. {}", socketChannel);

            channelList.add(socketChannel);

            for (SocketChannel channel : channelList) {

                // 6. 接收客户端发送的数据
                log.debug("before read... {}", channel);

                channel.read(buffer); // 阻塞方法，线程停止运行

                buffer.flip();

                debugRead(buffer);

                buffer.clear();

                log.debug("after read... {}", channel);
            }

        }


    }
}
