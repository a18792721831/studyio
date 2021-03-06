package com.studyio.hellosocket.server;

import com.studyio.hellosocket.domain.Messager;
import com.studyio.hellosocket.util.SocketUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author jiayq
 * @Date 2021-02-01
 */
public class NioServer {

    private static final int PORT = 8989;

    private static final String QUIT = "quit";

    private static final LinkedBlockingQueue<Messager> msg = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server start up , port : " + PORT);
            Set<SelectionKey> selectionKeys;
            Messager message;
            while (true) {
                selector.select();
                message = msg.poll();
                for (SelectionKey key : (selectionKeys = selector.selectedKeys()).stream().filter(SelectionKey::isValid).collect(Collectors.toSet())) {
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.finishConnect();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        System.out.println(socketChannel.socket().getPort() + " : login ! ");
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        Messager logMessager;
                        try {
                            msg.offer(logMessager = new Messager(socketChannel.socket().getPort(), SocketUtils.getSocketReader().apply(socketChannel)));
                        } catch (RuntimeException e) {
                            key.cancel();
                            logMessager = new Messager(socketChannel.socket().getPort(), e.getMessage());
                        }
                        System.out.println(Optional.ofNullable(logMessager).orElse(new Messager()).toString());
                    } else if (key.isWritable() && !Optional.ofNullable(message).isEmpty()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        try {
                            SocketUtils.getSocketWriter().accept(socketChannel, message.toString());
                        } catch (RuntimeException e) {
                            key.cancel();
                            System.out.println(new Messager(socketChannel.socket().getPort(), e.getMessage()));
                        }
                        if (QUIT.equals(message.getMessage()) && socketChannel.socket().getPort() == message.getPort()) {
                            key.cancel();
                        }
                    }
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
        }

    }
}