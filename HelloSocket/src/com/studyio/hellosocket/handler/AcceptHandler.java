package com.studyio.hellosocket.handler;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jiayq
 * @Date 2021-03-13
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private ReaderHandler readerHandler = new ReaderHandler().serverHandler();

    @Override
    public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
        if (result != null && result.isOpen()) {
            try {
                System.out.println(" accept : " + result.getRemoteAddress().toString());
                readerHandler.getByteBuffer().clear();
                result.read(readerHandler.getByteBuffer(), result, readerHandler);
                attachment.accept(attachment, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
        // 连接
        System.out.println("read exception : " + exc.getMessage());
        // 为下一次接受
        attachment.accept(attachment, this);
    }
}
