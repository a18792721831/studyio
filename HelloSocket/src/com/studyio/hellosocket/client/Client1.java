package com.studyio.hellosocket.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author jiayq
 * @Date 2021-01-26
 */
public class Client1 {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 8989;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("client start up");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 Scanner scanner = new Scanner(System.in);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                Runnable readTask = () -> {
                    while (true) {
                        try {
                            if (reader.ready()) {
                                System.out.println(reader.readLine());
                            }
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (IOException e) {
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                };
                Thread read = new Thread(readTask);
                Runnable writeTask = () -> {
                    try {
                        String inMsg;
                        while (!"quit".equals(inMsg = scanner.next())) {
                            writer.write(inMsg + "\n");
                            writer.flush();
                            TimeUnit.MILLISECONDS.sleep(100);
                        }
                        writer.write(inMsg + "\n");
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                    }
                };
                read.start();
                Thread write = new Thread(writeTask);
                write.start();
                write.join();
                read.interrupt();
                read.join();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
