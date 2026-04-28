package com.example.xmlserver.server;

import com.example.xmlserver.service.MessageProcessor;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler implements Runnable {

    private Socket socket;

    // 🔥 пул потоков (общий для всех клиентов)
    private static final ExecutorService pool =
            Executors.newFixedThreadPool(10);

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()))
        ) {

            String xml;

            while ((xml = in.readLine()) != null) {

                System.out.println("Received: " + xml);

                String message = xml;

                // 🔥 ВАЖНО: передаём в другой поток
                pool.submit(() -> {
                    try {
                        String response = MessageProcessor.process(message);

                        // ⚠ синхронизация записи в сокет
                        synchronized (out) {
                            out.write(response);
                            out.newLine();
                            out.flush();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}