package com.studyio.hellosocket.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author jiayq
 * @Date 2021-01-26
 */
public class Server3 {

    private static final int PORT = 8989;

    private static final Map<Socket, BufferedReader> ins = new HashMap<>();

    private static final Map<Socket, BufferedWriter> outs = new HashMap<>();

    private static final StringBuilder message = new StringBuilder();

    private static final ReentrantLock lock = new ReentrantLock();

    private static final String QUIT = "quit";

    private static final BiConsumer<Map<Socket, BufferedWriter>, Socket> removeWriter = (map, socket) -> {
        Iterator<Map.Entry<Socket, BufferedWriter>> outIter = map.entrySet().iterator();
        while (outIter.hasNext()) {
            Map.Entry<Socket, BufferedWriter> out = outIter.next();
            if (out.getKey().equals(socket)) {
                try {
                    out.getValue().flush();
                    out.getValue().close();
                } catch (IOException e) {
                }
                outIter.remove();
            }
        }
    };

    private static final BiConsumer<Map<Socket, BufferedReader>, Socket> removeReader = (map, socket) -> {
        Iterator<Map.Entry<Socket, BufferedReader>> inIter = map.entrySet().iterator();
        while (inIter.hasNext()) {
            Map.Entry<Socket, BufferedReader> in = inIter.next();
            if (in.getKey().equals(socket)) {
                try {
                    in.getValue().close();
                } catch (IOException e) {
                }
                inIter.remove();
            }
        }
    };

    private static final BiFunction<Map<Socket, BufferedReader>, BufferedReader, Socket> getSocketByReader = (map, value) -> {
        for (Map.Entry<Socket, BufferedReader> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    };

    private static final BiFunction<Map<Socket, BufferedWriter>, BufferedWriter, Socket> getSocketByWriter = (map, value) -> {
        for (Map.Entry<Socket, BufferedWriter> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    };

    private static final Consumer<Integer> sleepSeconds = second -> {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
        }
    };

    private static final Runnable readTask = () -> {
        while (true) {
            try {
                lock.lock();
                Iterator<Map.Entry<Socket, BufferedReader>> iterator = ins.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Socket, BufferedReader> entry = iterator.next();
                    try {
                        if (entry.getValue().ready() && message.isEmpty()) {
                            try {
                                String msg = entry.getValue().readLine();
                                message.append(entry.getKey().getPort() + " : " + msg);
                                if (QUIT.equals(msg)) {
                                    outs.get(entry.getKey()).write(message.toString());
                                    iterator.remove();
                                    removeWriter.accept(outs, entry.getKey());
                                }
                                System.out.println(message);
                            } catch (IOException e) {
                                iterator.remove();
                                removeWriter.accept(outs, entry.getKey());
                            }
                        }
                    } catch (IOException e) {
                        iterator.remove();
                        removeWriter.accept(outs, entry.getKey());
                    }
                }
            } finally {
                lock.unlock();
            }
            sleepSeconds.accept(100);
        }
    };

    private static final Runnable writeTask = () -> {
        while (true) {
            BufferedWriter remover = null;
            try {
                lock.lock();
                if (!message.isEmpty()) {
                    for (BufferedWriter writer : outs.values()) {
                        remover = writer;
                        writer.write(message.toString() + "\n");
                        writer.flush();
                    }
                    message.delete(0, message.length());
                }
            } catch (IOException e) {
                Socket socket = getSocketByWriter.apply(outs, remover);
                removeReader.accept(ins, socket);
                removeWriter.accept(outs, socket);
            } finally {
                lock.unlock();
            }
            sleepSeconds.accept(100);
        }
    };

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("start up , port : " + PORT);
            new Thread(readTask).start();
            new Thread(writeTask).start();
            while (true) {
                Socket socket = serverSocket.accept();
                String clientLog = "client [ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " ] ";
                System.out.println(Thread.currentThread().getName() + clientLog);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                try {
                    lock.lock();
                    ins.put(socket, reader);
                    outs.put(socket, writer);
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
        }
    }

}
