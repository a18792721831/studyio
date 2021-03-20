package com.studyio.hellosocket.util;

import com.studyio.hellosocket.domain.Messager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author jiayq
 * @Date 2021-03-13
 */
public class AsyncSocketUtils {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final LinkedBlockingQueue<Messager> msg = new LinkedBlockingQueue<>();

    public static CompletionHandler getReaderHandler() {
        return new ReaderHandler();
    }

    public static CompletionHandler getWriterHandler() {
        return new WriterHandler();
    }

    public static CompletionHandler getAcceptHandler() {
        return new AcceptHandler();
    }

    private static class ReaderHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

        public ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        @Override
        public void completed(Integer result, AsynchronousSocketChannel socketChannel) {
            if (result > 0) {
                byteBuffer.flip();
                Messager messager = new Messager();
                try {
                    msg.offer(messager = new Messager(((InetSocketAddress) socketChannel.getRemoteAddress()).getPort(), String.valueOf(CHARSET.decode(byteBuffer))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String message = messager.toString().trim();
                WriterHandler writerHandler = new WriterHandler();
                writerHandler.getByteBuffer().clear();
                writerHandler.getByteBuffer().put(message.getBytes());
                writerHandler.getByteBuffer().clear();
                socketChannel.write(writerHandler.getByteBuffer(), socketChannel, writerHandler);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel socketChannel) {
            System.out.println(exc.getMessage());
        }
    }

    private static class WriterHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

        public ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        @Override
        public void completed(Integer result, AsynchronousSocketChannel socketChannel) {
            Messager messager;
            if (result > 0 && (messager = msg.poll()) != null) {
                System.out.println(messager);
                if (socketChannel.isOpen()) {
                    ReaderHandler readerHandler = new ReaderHandler();
                    readerHandler.getByteBuffer().clear();
                    socketChannel.read(readerHandler.getByteBuffer(), socketChannel, readerHandler);
                }
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println(exc.getMessage());
        }
    }

    private static class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

        @Override
        public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel serverSocketChannel) {
            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.accept(serverSocketChannel, this);
            }
            if (result != null && result.isOpen()) {
                ReaderHandler readerHandler = new ReaderHandler();
                readerHandler.getByteBuffer().clear();
                result.read(readerHandler.getByteBuffer(), result, readerHandler);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
            System.out.println(exc.getMessage());
        }
    }
}
