package com.lm.sty.netty.nio.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liming
 * @version 1.0
 * @since 2023/4/19 9:25
 */
@Slf4j
public class Demo01 {
    public static void main(String[] args) {
        try (RandomAccessFile file = new RandomAccessFile("sty_netty/input/data.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            do {
                // 向 buffer 写入
                int len = channel.read(buffer);
                log.debug("读到的字节数：{}", len);
                if (len == -1) {
                    break;
                }
                // 切换 buffer 模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    log.debug("{}", (char) buffer.get());
                }
                // 切换 buffer 写模式
                buffer.clear();
            } while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
