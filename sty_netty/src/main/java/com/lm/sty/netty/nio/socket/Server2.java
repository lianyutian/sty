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

/**
 * @description  : [非阻塞模式]
 * @author       : [lm]
 * @version      : [v1.0]
 * @createTime   : [2023/6/7 22:57]
 */
@Slf4j
public class Server2 {

    public static void main(String[] args) throws IOException {

        // 1. 使用 nio 来理解阻塞模式，单线程
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 2. 创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 设置非阻塞模式（影响 serverSocketChannel.accept()方法，设置后该方法为非阻塞）
        serverSocketChannel.configureBlocking(false);

        // 3. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 4. 连接集合
        List<SocketChannel> channelList = new ArrayList<>();

        while (true) {

            // 5. accept 建立与客户端连接，SocketChannel 用来与客户端之间通信
            // log.debug("connecting...");

            // 此时该方法为非阻塞方法,线程还会继续运行，如果没有连接建立，sc是null
            SocketChannel socketChannel = serverSocketChannel.accept();

            if (socketChannel != null) {
                log.debug("connected.. {}", socketChannel);
                // 设置非阻塞（影响 channel.read()方法，设置后该方法为非阻塞）
                socketChannel.configureBlocking(false);
                channelList.add(socketChannel);
            }

            for (SocketChannel channel : channelList) {

                // 6. 接收客户端发送的数据
                // log.debug("before read... {}", channel);

                // 设置后非阻塞方法，线程继续运行，如果没有读取到数据，read返回0
                int read = channel.read(buffer);

                if (read > 0) {
                    buffer.flip();

                    debugRead(buffer);

                    buffer.clear();

                    log.debug("after read... {}", channel);
                }

            }

        }

    }
}
