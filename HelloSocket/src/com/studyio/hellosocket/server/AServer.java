package com.studyio.hellosocket.server;

import com.studyio.hellosocket.handler.AcceptHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * @author jiayq
 * @Date 2021-03-16
 */
public class AServer {

    private static final int PORT = 8989;

    private static final String QUIT = "quit";

    private static final String HOST_NAME = "127.0.0.1";

    public static void main(String[] args) {
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(HOST_NAME, PORT));
            System.out.println("server start up , host : " + HOST_NAME + " , port : " + PORT);
            serverSocketChannel.accept(serverSocketChannel, new AcceptHandler());
            while (true) {
                Thread.yield();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
