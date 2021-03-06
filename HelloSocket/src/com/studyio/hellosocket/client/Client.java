package com.studyio.hellosocket.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author jiayq
 * @Date 2021-01-26
 */
public class Client {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 8989;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("client start up");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 Scanner scanner = new Scanner(System.in);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                String inMsg;
                while (!"quit".equals(inMsg = scanner.next())) {
                    writer.write(inMsg + "\n");
                    writer.flush();
                    String message = reader.readLine();
                    System.out.println(message);
                }
                writer.write(inMsg + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
