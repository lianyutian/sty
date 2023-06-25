package com.lm.sty.netty.nio.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugAll;

/**
 * 多线程版selector
 *
 * @author liming
 * @version 1.0
 * @since 2023/6/16 14:18
 */
@Slf4j
public class Server5 {

    public static void main(String[] args) throws IOException {

    }

    static class BoosEventLoop implements Runnable {
        private Selector boss;
        private boolean start = false;
        private WorkerEventLoop[] workers;
        private AtomicInteger index = new AtomicInteger();

        public void register() throws IOException {
            if (!start) {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.bind(new InetSocketAddress(8080));
                serverSocketChannel.configureBlocking(false);
                boss = Selector.open();
                SelectionKey selectionKey = serverSocketChannel.register(boss, 0, null);
                selectionKey.interestOps(SelectionKey.OP_ACCEPT);
                workers = initWorkerEventLoop();
                new Thread(this, "boss").start();
                log.debug("boss start...");
                start = true;
            }
        }

        private WorkerEventLoop[] initWorkerEventLoop() {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            WorkerEventLoop[] workerEventLoops = new WorkerEventLoop[cpuCount];
            for (int i = 0; i < cpuCount; i++) {
                workers[i] = new WorkerEventLoop(i);
            }
            return workerEventLoops;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    boss.select();
                    Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            // 处理连接事件
                            SocketChannel sc = ssc.accept();
                            sc.configureBlocking(false);
                            log.debug("{} connected", sc.getRemoteAddress());
                            workers[index.getAndIncrement() % workers.length].register(sc);
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class WorkerEventLoop implements Runnable {
        private Selector worker;
        private volatile boolean start = false;
        private int index;

        public WorkerEventLoop(int index) {
            this.index = index;
        }

        public void register(SocketChannel socketChannel) throws IOException {
            if (!start) {
                worker = Selector.open();
                new Thread(this, "worker-" + index).start();
                start = true;
            }
            worker.wakeup();
            socketChannel.register(worker, SelectionKey.OP_READ);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    worker.select();
                    Iterator<SelectionKey> iterator = worker.selectedKeys().iterator();
                    while (iterator.hasNext()) {

                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {

                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();

                            try {
                                int read = sc.read(buffer);
                                if (read != -1) {
                                    buffer.flip();
                                    log.debug("{} message:", sc.getRemoteAddress());
                                    debugAll(buffer);
                                } else {
                                    // 读取完成
                                    key.cancel();
                                    sc.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                // 客户端强制关闭连接
                                key.cancel();
                                sc.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
