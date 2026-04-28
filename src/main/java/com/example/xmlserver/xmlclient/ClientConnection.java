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
            System.out.println("❌ Error sending message");
            stop();
        }
    }

    public void readFromServer() {
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                System.out.println("📥 Server: " + line);
            }
        } catch (IOException e) {
            System.out.println("❌ Connection lost");
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

        System.out.println("🔌 Disconnected");
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