package com.studyio.hellosocket.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author jiayq
 * @Date 2021-03-08
 */
public class AioClient {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 8989;

    private static final String QUIT = "quit";

    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static void main(String[] args) {
        try (AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            clientChannel.connect(new InetSocketAddress(HOST, PORT), clientChannel, new ConnecterHandler());
            String string;
            while (true) {
                string = scanner.nextLine();
                if (!Optional.ofNullable(string.trim()).isEmpty()) {
                    WriterHandler writerHandler = new WriterHandler();
                    writerHandler.getByteBuffer().clear();
                    writerHandler.getByteBuffer().put(string.getBytes());
                    writerHandler.getByteBuffer().clear();
                    clientChannel.write(writerHandler.getByteBuffer(), clientChannel, new WriterHandler());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConnecterHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

        @Override
        public void completed(Void result, AsynchronousSocketChannel socketChannel) {
            ReaderHandler readerHandler = new ReaderHandler();
            readerHandler.getByteBuffer().clear();
            socketChannel.read(readerHandler.getByteBuffer(), socketChannel, readerHandler);
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {

        }
    }

    private static class WriterHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

        public ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        @Override
        public void completed(Integer result, AsynchronousSocketChannel socketChannel) {
            byteBuffer.flip();
            System.out.print(CHARSET.decode(byteBuffer));
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println(exc.getMessage());
        }
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
                System.out.println(CHARSET.decode(byteBuffer));
                getByteBuffer().clear();
                socketChannel.read(getByteBuffer(), socketChannel, this);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel socketChannel) {
            System.out.println(exc.getMessage());
        }
    }
}
