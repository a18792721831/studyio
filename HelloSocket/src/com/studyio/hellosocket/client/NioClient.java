package com.studyio.hellosocket.client;

import com.studyio.hellosocket.util.SocketUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author jiayq
 * @Date 2021-03-04
 */
public class NioClient {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 8989;

    private static final String QUIT = "quit";

    private static final LinkedBlockingQueue<String> msg = new LinkedBlockingQueue<>();

    private static final Runnable consoleReader = () -> {
        try (Scanner scanner = new Scanner(System.in)) {
            String string;
            while (true) {
                string = scanner.nextLine();
                if (!Optional.ofNullable(string).isEmpty()) {
                    msg.offer(string);
                }
            }
        }
    };

    public static void main(String[] args) {
        Thread reader = new Thread(consoleReader);
        reader.setDaemon(true);
        reader.start();
        try (SocketChannel clientChannel = SocketChannel.open();
             Selector selector = Selector.open()) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            clientChannel.connect(new InetSocketAddress(HOST, PORT));
            Set<SelectionKey> selectionKeys;
            String message;
            do {
                message = msg.poll();
                selector.select();
                for (SelectionKey key : (selectionKeys = selector.selectedKeys()).stream().filter(SelectionKey::isValid).collect(Collectors.toSet())) {
                    if (key.isReadable()) {
                        System.out.println(SocketUtils.getSocketReader().apply((SocketChannel) key.channel()));
                    } else if (key.isWritable() && !Optional.ofNullable(message).isEmpty()) {
                        SocketUtils.getSocketWriter().accept((SocketChannel) key.channel(), message);
                    } else if (key.isConnectable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        if (!socketChannel.isConnected()) {
                            SocketUtils.getSocketConnecter().accept(socketChannel);
                            socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                            System.out.println("client start up");
                        }
                    }
                }
                selectionKeys.clear();
            } while (!QUIT.equals(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
