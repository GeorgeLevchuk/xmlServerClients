package com.example.xmlserver.server;

import com.example.xmlserver.service.MessageProcessor;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(10);

    private static final String MESSAGE_DELIMITER = "###END###";

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
            StringBuilder messageBuffer = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                if (line.equals(MESSAGE_DELIMITER)) {
                    String xml = messageBuffer.toString().trim();
                    messageBuffer.setLength(0);

                    if (!xml.isEmpty()) {
                        System.out.println("Received XML:\n" + xml);

                        final String message = xml;
                        pool.submit(() -> {
                            try {
                                String response = MessageProcessor.process(message);

                                synchronized (out) {
                                    out.write(response);
                                    out.newLine();
                                    out.write(MESSAGE_DELIMITER);
                                    out.newLine();
                                    out.flush();
                                }
                            } catch (Exception e) {
                                System.out.println("Exception write response" + e.getMessage());
                            }
                        });
                    }
                } else {
                    messageBuffer.append(line).append("\n");
                }
            }

        } catch (Exception e) {
            System.out.println("Exception write response" + e.getMessage());
        }
    }
}