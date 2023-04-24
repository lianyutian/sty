package com.lm.sty.netty.nio.bytebuffer;

import java.nio.ByteBuffer;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugAll;

/**
 * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
 * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
 * Hello,world\n
 * I'm zhangsan\n
 * How are you?\n
 * 变成了下面的两个 byteBuffer (黏包，半包)
 * Hello,world\nI'm zhangsan\nHo
 * w are you?\n
 * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
 *
 * @author liming
 * @version 1.0
 * @since 2023/4/20 9:52
 */
public class TestByteBufferExam {
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();
        int oldLimit = source.limit();
        for (int i = 0; i < oldLimit; i++) {
            if (source.get(i) == '\n') {
                System.out.println(i);
                ByteBuffer target = ByteBuffer.allocate(i + 1 - source.position());
                // 0 ~ limit
                source.limit(i + 1);
                target.put(source); // 从source 读，向 target 写
                debugAll(target);
                source.limit(oldLimit);
            }
        }
        source.compact();
    }

}
