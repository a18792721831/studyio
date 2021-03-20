package com.studyio.hellosocket.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * @author jiayq
 * @Date 2021-03-16
 */
public class WriterHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

    private Charset CHARSET = Charset.forName("UTF-8");

    private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    private Consumer<AsynchronousSocketChannel> consumer = x->{};

    private Consumer<AsynchronousSocketChannel> serverHandler = channel -> {
        ReaderHandler readerHandler = new ReaderHandler().serverHandler();
        readerHandler.getByteBuffer().clear();
        channel.read(readerHandler.getByteBuffer(), channel, readerHandler);
    };

    public WriterHandler server() {
        consumer = serverHandler;
        return this;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachment) {
        // 连接被关闭
        if (result < 0) {
            System.out.println("connection is closed");
        }
        // 空写入
        else if (result == 0) {
            System.out.println("channel empty write");
        }
        // 写入成功
        else {
            getByteBuffer().flip();
            System.out.println(" write : " + CHARSET.decode(getByteBuffer()));
            consumer.accept(attachment);
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        // 写入异常
        System.out.println(" write exception : " + exc.getMessage());
    }
}
