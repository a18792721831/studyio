package com.studyio.hellosocket.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jiayq
 * @Date 2021-01-26
 */
public class Server {

    private static final int PORT = 8989;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("start up , port : " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                String clientLog = "client [ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "] ";
                System.out.println(clientLog);
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                    String msg;
                    while (!"quit".equals(msg = bufferedReader.readLine())) {
                        System.out.println(clientLog + " , msg : " + msg);
                        bufferedWriter.write(msg + " by server !\n");
                        bufferedWriter.flush();
                    }
                }
                System.out.println(clientLog + " quit !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
