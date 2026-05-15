package com.example.xmlserver.xmlclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReader implements Runnable {
    private final ClientConnection connection;

    public ConsoleReader(ClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try (BufferedReader console =
                     new BufferedReader(new InputStreamReader(System.in))) {

            String input;
            String user = System.getProperty("user.name");

            while ((input = console.readLine()) != null) {
                input = input.trim();

                if ("-h".equalsIgnoreCase(input)) {
                    System.out.println("Завершение работы");
                    connection.stop();
                    break;
                }

                if (input.toLowerCase().startsWith("-m ")) {
                    String text = input.substring(3).trim();

                    if (text.isEmpty()) {
                        System.out.println("Введите текст сообщения");
                        continue;
                    }

                    String xml = "<message  xmlns=\"http://messaging.com/schema\">" +
                            "<request>" +
                            "<user>" + user + "</user>" +
                            "<text>" + text + "</text>" +
                            "</request>" +
                            "</message>";

                    connection.send(xml);
                    continue;
                }

                System.out.println("Неизвестная команда");
                System.out.println("Используй:");
                System.out.println("  -m <text>  → отправить сообщение");
                System.out.println("  -h         → выход");
            }

        } catch (Exception e) {
            System.out.println("Error read console" + e.getMessage());
        }
    }
}