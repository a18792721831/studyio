package com.studyio.hellosocket.util;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author jiayq
 * @Date 2021-03-06
 */
public class SocketUtils {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final int BUFFER_SIZE = 2048;

    private static final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private static final ConcurrentHashMap<Socket, Integer> errors = new ConcurrentHashMap<>();

    private static final int MAX_ERRORS = 100;

    private static final BiConsumer<SocketChannel, Exception> error = (socketChannel, e) -> {
        int times;
        if (errors.containsKey(socketChannel.socket())) {
            errors.put(socketChannel.socket(), (times = errors.get(socketChannel.socket())) + 1);
        } else {
            errors.put(socketChannel.socket(), (times = 1));
        }
        if (times >= MAX_ERRORS) {
            errors.remove(socketChannel.socket());
            throw new RuntimeException(e.getMessage());
        }
    };

    private static final Function<SocketChannel, String> socketReader = socketChannel -> {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            socketChannel.configureBlocking(false);
            buffer.clear();
            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    stringBuilder.append(CHARSET.decode(buffer));
                }
                buffer.clear();
            }
        } catch (IOException e) {
            error.accept(socketChannel, e);
            System.out.println("socket reader error , message [" + stringBuilder.toString() + "] , error info [" + e.getMessage() + "] !");
        }
        if (stringBuilder.isEmpty()) {
            throw new RuntimeException(" message is null ");
        }
        return stringBuilder.toString();
    };

    public static Function<SocketChannel, String> getSocketReader() {
        return socketReader;
    }

    private static final BiConsumer<SocketChannel, String> socketWriter = (socketChannel, message) -> {
        try {
            socketChannel.configureBlocking(false);
            buffer.clear();
            buffer.put((message).getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        } catch (IOException e) {
            error.accept(socketChannel, e);
            System.out.println("socket writer error , message [" + message + "] , error info [" + e.getMessage() + "] !");
        }
    };

    public static BiConsumer<SocketChannel, String> getSocketWriter() {
        return socketWriter;
    }

    private static final Consumer<SocketChannel> socketConnecter = socketChannel -> {
        try {
            while (!socketChannel.isConnectionPending()) {
                ;
            }
            socketChannel.finishConnect();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            System.out.println("socket connection error , exists : 3 !");
            throw new RuntimeException(e.getMessage());
        }
    };

    public static Consumer<SocketChannel> getSocketConnecter() {
        return socketConnecter;
    }

}
