package com.studyio.hellosocket.handler;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jiayq
 * @Date 2021-03-16
 */
public class ConnectorHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

    private ReaderHandler readerHandler = new ReaderHandler().clientHandler();

    @Override
    public void completed(Void result, AsynchronousSocketChannel attachment) {
        if (attachment.isOpen()) {
            try {
                System.out.println(" connect : " + attachment.getRemoteAddress().toString());
                readerHandler.getByteBuffer().clear();
                attachment.read(readerHandler.getByteBuffer(), attachment, readerHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        // 连接
        System.out.println("read exception : " + exc.getMessage());
    }
}
