package com.example.xmlserver.server;

import com.example.xmlserver.service.MessageProcessor;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;

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

            // 👇 ВАЖНО: читаем пока клиент не закроет соединение
            while ((xml = in.readLine()) != null) {

                System.out.println("Received: " + xml);

                String response = MessageProcessor.process(xml);

                out.write(response);
                out.newLine();
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}