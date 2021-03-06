package com.studyio.hellosocket.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author jiayq
 * @Date 2021-01-26
 */
public class Server2 {

    private static final int PORT = 8989;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("start up , port : " + PORT);
            ExecutorService service = new ThreadPoolExecutor(3, 5, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            Consumer<Socket> backer = socket -> {
                String clientLog = "client [ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " ] ";
                System.out.println(Thread.currentThread().getName() + clientLog);
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                    String msg;
                    while (!"quit".equals(msg = bufferedReader.readLine())) {
                        System.out.println(Thread.currentThread().getName() + clientLog + " , msg : " + msg);
                        bufferedWriter.write(msg + " by server !\n");
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + clientLog + " quit !");
            };
            Consumer<Socket> tasker = socket -> {
                Runnable runnable = () -> {
                    backer.accept(socket);
                };
                service.execute(runnable);
            };
            while (true) {
                Socket socket = serverSocket.accept();
                tasker.accept(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
