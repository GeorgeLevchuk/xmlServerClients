package com.example.xmlserver.xmlclient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnection {

    private final String host;
    private final int port;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private volatile boolean running = true;

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);

        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

    }

    public void send(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error sending message");
            stop();
        }
    }

    public void readFromServer() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder messageBuffer = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                if (line.equals("###END###")) {
                    String response = messageBuffer.toString().trim();
                    messageBuffer.setLength(0);

                    if (!response.isEmpty()) {
                        System.out.println("Ответ от сервера:\n" + response);
                    }
                } else {
                    messageBuffer.append(line).append("\n");
                }
            }

        } catch (Exception e) {
            System.out.println("Connection closed");
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        close();
    }

    public void close() {
        try {
            if (reader != null) reader.close();
        } catch (Exception ignored) {}

        try {
            if (writer != null) writer.close();
        } catch (Exception ignored) {}

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception ignored) {}

        System.out.println("Disconnected");
    }

    public boolean tryConnect() {
        try {
            connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}