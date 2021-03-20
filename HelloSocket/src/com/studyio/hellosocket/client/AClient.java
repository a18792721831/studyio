package com.studyio.hellosocket.client;

import com.studyio.hellosocket.exce.QuitException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author jiayq
 * @Date 2021-03-13
 */
public class AClient {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 8989;

    private static final int SIZE = 2048;

    private static final String QUIT = "quit";

    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static void main(String[] args) {
        try (AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            clientChannel.connect(new InetSocketAddress(HOST, PORT), clientChannel, new ConnectHandler());
            String string;
            while (true) {
                string = scanner.nextLine();
                if (!string.trim().isEmpty()) {
                    WriteHandler writeHandler = new WriteHandler();
                    writeHandler.getByteBuffer().clear();
                    writeHandler.getByteBuffer().put(string.getBytes());
                    writeHandler.getByteBuffer().clear();
                    clientChannel.write(writeHandler.getByteBuffer(), clientChannel, writeHandler);
                }
                if (QUIT.equals(string)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (QuitException e) {
        }
    }

    private static class ConnectHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

        @Override
        public void completed(Void result, AsynchronousSocketChannel attachment) {
            System.out.println("已经建立连接");
            ReadHandler readHandler = new ReadHandler();
            readHandler.getByteBuffer().clear();
            attachment.read(readHandler.getByteBuffer(), attachment, readHandler);
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("连接过程发生异常");
            throw new QuitException("发送信息异常");
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
                System.out.println("连接已断开");
                throw new RuntimeException("连接已断开");
            } else if (result == 0) {
                System.out.println("传输了空数据");
            } else {
                byteBuffer.position(result);
                byteBuffer.flip();
                System.out.println("发送信息 : " + CHARSET.decode(byteBuffer).toString().trim());
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("发送信息异常");
            throw new QuitException("发送信息异常");
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
                System.out.println("连接已断开");
                throw new QuitException("连接已断开");
            } else if (result == 0) {
                System.out.println("收到了空数据");
            } else {
                byteBuffer.position(result);
                byteBuffer.flip();
                System.out.println("收到信息 : " + CHARSET.decode(byteBuffer).toString().trim());
                byteBuffer.clear();
                attachment.read(byteBuffer, attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("接收信息异常");
        }
    }

}
