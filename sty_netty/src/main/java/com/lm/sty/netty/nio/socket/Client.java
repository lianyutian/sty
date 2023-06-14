package com.lm.sty.netty.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("localhost", 8080));

        socketChannel.write(Charset.defaultCharset().encode("hello"));

        socketChannel.write(Charset.defaultCharset().encode("0123\n456789abcdef"));
        socketChannel.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));

        System.out.println("waiting...");
    }
}
