package com.studyio.hellosocket.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author jiayq
 * @Date 2021-03-13
 */
public class ReaderHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

    private Charset CHARSET = Charset.forName("UTF-8");

    private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    private Consumer<AsynchronousSocketChannel> consumer;

    public ReaderHandler clientHandler() {
        consumer = client;
        return this;
    }

    private Consumer<AsynchronousSocketChannel> server = channel -> {
        WriterHandler writerHandler = new WriterHandler().server();
        writerHandler.getByteBuffer().clear();
        getByteBuffer().flip();
        try {
            writerHandler.getByteBuffer().put((channel.getRemoteAddress().toString() + " : " + CHARSET.decode(getByteBuffer())).trim().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writerHandler.getByteBuffer().clear();
        channel.write(writerHandler.getByteBuffer(), channel, writerHandler);
    };

    public ReaderHandler serverHandler() {
        consumer = server;
        return this;
    }

    private Consumer<AsynchronousSocketChannel> client = channel -> {
        getByteBuffer().clear();
        // 重复读
        channel.read(getByteBuffer(), channel, this);
    };

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachment) {
        // 连接被关闭
        if (result < 0) {
            System.out.println("socket is closed");
        } else if (result == 0) {
            System.out.println("read empty data");
        } else {
            getByteBuffer().flip();
            System.out.println(" read : " + CHARSET.decode(getByteBuffer()));
            consumer.accept(attachment);
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        // 读取异常
        System.out.println(" read exception : " + exc.getMessage());
    }
}
