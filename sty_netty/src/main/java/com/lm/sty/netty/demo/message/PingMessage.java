package com.lm.sty.netty.demo.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author liming
 * @version 1.0
 * @since 2023/7/7 10:27
 */
@Data
@ToString(callSuper = true)
public class PingMessage extends Message {
    private String content;

    public PingMessage(String content) {
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
