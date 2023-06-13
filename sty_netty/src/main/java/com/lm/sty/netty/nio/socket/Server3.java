package com.lm.sty.netty.nio.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugAll;
import static com.lm.sty.netty.utils.ByteBufferUtil.debugRead;

/**
 * @author : [lm]
 * @version : [v1.0]
 * @description [selector模式]
 * @createTime : [2023/6/8 22:23]
 */
@Slf4j
public class Server3 {

    public static void main(String[] args) throws IOException {

        // 1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个 channel 的事件
        SelectionKey selectionKey = ssc.register(selector, 0, null);
        // 只关注 accept 事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", selectionKey);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            // 3. select 方法，没有事件发生时，线程阻塞（cpu不会一直空转），有事件，线程才会回复执行
            // select 事件在未处理时，不会阻塞，事件发生后要么处理要么取消不能置之不理
            selector.select();

            // 4. 处理事件，SelectionKey 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                // 处理key时，要从 SelectionKeys 集合中删除，否则下次处理就会有问题
                iterator.remove();
                log.debug("key: {}", key);
                // 5. 区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 获取 selectionKey 上关联的附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        // 如果是正常断开，read 返回值是 -1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            // 需要扩容
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer); // 0123456789abcdef3333\n
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 因为客户端断开了，因此需要将 key 取消
                        key.cancel();
                    }
                }
            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact(); // 0123456789abcdef  position 16 limit 16
    }
}
