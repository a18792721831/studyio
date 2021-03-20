package com.studyio.hellosocket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author jiayq
 * @Date 2021-03-20
 */
public class AServerGroup {

    private static final int PORT = 8989;

    private static final String QUIT = "quit";

    private static final String HOST_NAME = "127.0.0.1";

    private static final int SIZE = 2048;

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static List<AsynchronousSocketChannel> allChannels = Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(2, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(20));
        AsynchronousChannelGroup group = null;
        try {
            group = AsynchronousChannelGroup.withThreadPool(executor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(group)) {
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

    private static class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

        @Override
        public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
            if (result != null && result.isOpen()) {
                try {
                    System.out.println("接受了连接 : " + result.getRemoteAddress().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                allChannels.add(result);
                ReadHandler readHandler = new ReadHandler();
                readHandler.getByteBuffer().clear();
                result.read(readHandler.getByteBuffer(), result, readHandler);
            }
            if (attachment != null && attachment.isOpen()) {
                attachment.accept(attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
            System.out.println("接受连接异常");
        }
    }

    private static class ReadHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer byteBuffer = ByteBuffer.allocate(SIZE);

        public ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        @Override
        public void completed(Integer result, AsynchronousSocketChannel attachment) {
            if (result < 0) {
                System.out.println("连接已经关闭");
                Iterator<AsynchronousSocketChannel> iterator = allChannels.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().equals(attachment)) {
                        iterator.remove();
                    }
                }
            } else if (result == 0) {
                System.out.println("接收到了空信息");
            } else {
                // 转换为读模式
                byteBuffer.position(result);
                byteBuffer.flip();
                try {
                    System.out.println(attachment.getRemoteAddress().toString() + " : " + CHARSET.decode(byteBuffer).toString().trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 重复读取
                byteBuffer.position(result);
                byteBuffer.flip();
                if (QUIT.equals(CHARSET.decode(byteBuffer).toString().trim())) {
                    Iterator<AsynchronousSocketChannel> iterator = allChannels.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().equals(attachment)) {
                            iterator.remove();
                        }
                    }
                }
                allChannels.stream().forEach(x -> {
                    WriteHandler writeHandler = new WriteHandler();
                    writeHandler.getByteBuffer().clear();
                    // 重复读取
                    byteBuffer.position(result);
                    byteBuffer.flip();
                    try {
                        writeHandler.getByteBuffer().put((attachment.getRemoteAddress().toString() + " : " + CHARSET.decode(byteBuffer).toString().trim()).getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    writeHandler.getByteBuffer().clear();
                    x.write(writeHandler.getByteBuffer(), attachment, writeHandler);
                });
                byteBuffer.clear();
                attachment.read(byteBuffer, attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("接收信息发生异常");
            Iterator<AsynchronousSocketChannel> iterator = allChannels.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(attachment)) {
                    iterator.remove();
                }
            }
        }
    }

    private static class WriteHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer byteBuffer = ByteBuffer.allocate(SIZE);

        public ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        @Override
        public void completed(Integer result, AsynchronousSocketChannel attachment) {
            if (result < 0) {
                System.out.println("连接已经关闭");
                Iterator<AsynchronousSocketChannel> iterator = allChannels.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().equals(attachment)) {
                        iterator.remove();
                    }
                }
            } else if (result == 0) {
                System.out.println("发送了空信息");
            } else {
                byteBuffer.position(result);
                byteBuffer.flip();
                System.out.println("发送信息 : " + CHARSET.decode(byteBuffer).toString().trim());
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("发送信息发生异常");
            Iterator<AsynchronousSocketChannel> iterator = allChannels.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(attachment)) {
                    iterator.remove();
                }
            }
        }
    }


}
