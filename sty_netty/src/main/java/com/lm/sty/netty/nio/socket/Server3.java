package com.lm.sty.netty.nio.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugAll;

/**
 * 未处理消息边界
 * @author liming
 * @version 1.0
 * @since 2023/6/16 11:30
 */
@Slf4j
public class Server3 {
    public static void main(String[] args) throws IOException {

        ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {

            // 阻塞在该方法上
            int count = selector.select();

            log.debug("select count: {}", count);

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();

                log.debug("key {}", key);

                // 连接事件
                if (key.isAcceptable()) {

                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                    // 处理连接事件
                    SocketChannel sc = serverSocketChannel.accept();

                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);

                    log.debug("{}", sc);
                }
                if (key.isReadable()) {

                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SocketChannel sc = (SocketChannel) key.channel();
                    int read = sc.read(buffer);
                    if (read == -1) {
                        key.cancel();
                        sc.close();
                    } else {
                        buffer.flip();
                        debugAll(buffer);
                    }
                }

                // 处理完毕，必须将事件移除
                iterator.remove();
            }
        }
    }
}
