package com.double0101.nerver.server;

import com.double0101.nerver.core.http.netty.ChatServer;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

public class ChatExample {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please give port as argment");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        final ChatServer endpoint = new ChatServer();
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destory();
            }
        });

        future.channel().closeFuture().syncUninterruptibly();
    }
}
