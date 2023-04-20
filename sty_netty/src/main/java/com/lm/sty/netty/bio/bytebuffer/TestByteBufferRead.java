package com.lm.sty.netty.bio.bytebuffer;

import java.nio.ByteBuffer;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugAll;

/**
 * @author liming
 * @version 1.0
 * @since 2023/4/20 9:34
 */
public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // rewind从头开始读
        buffer.get();
        debugAll(buffer);
        buffer.rewind();
        debugAll(buffer);

        // mark做一个标记，记录position的位置，reset是将position重置到mark位置
        System.out.println((char) buffer.get());
        buffer.mark(); // 加标记索引1的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset(); // 重置到索引1的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        // get(i)不会改变position的位置
        System.out.println((char) buffer.get(3));
        debugAll(buffer);

    }
}
