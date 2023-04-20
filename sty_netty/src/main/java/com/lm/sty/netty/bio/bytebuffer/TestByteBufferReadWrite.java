package com.lm.sty.netty.bio.bytebuffer;

import java.nio.ByteBuffer;

import static com.lm.sty.netty.utils.ByteBufferUtil.debugAll;

/**
 * @author liming
 * @version 1.0
 * @since 2023/4/20 9:11
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61); // 'a'
        debugAll(buffer);

        buffer.put(new byte[]{0x62, 0x63, 0x64});
        debugAll(buffer);

        // 此时position在4读取不到数据
        System.out.println(buffer.get());

        // 切换读模式
        buffer.flip();
        // 使用get方法会移动position每读一位移动一位
        System.out.println(buffer.get());
        debugAll(buffer);

        // compact()会将未读得数据往前移动压缩
        buffer.compact();
        debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x6f});
        debugAll(buffer);
    }
}
