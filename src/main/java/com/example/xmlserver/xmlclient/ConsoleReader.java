package com.example.xmlserver.xmlclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReader implements Runnable {

    private static final String MESSAGE_DELIMITER = "###END###";
    private final ClientConnection connection;

    public ConsoleReader(ClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            String user = System.getProperty("user.name");
            printHelp();
            String input;

            while ((input = console.readLine()) != null) {
                input = input.trim();

                if ("-h".equalsIgnoreCase(input)) {
                    System.out.println("Завершение работы");
                    connection.stop();
                    break;
                }

                if ("-m".equalsIgnoreCase(input) || input.toLowerCase().startsWith("-m ")) {
                    System.out.println("Введите текст (###END### для завершения):");
                    StringBuilder textBuffer = new StringBuilder();
                    String inlineText = input.length() > 2 ? input.substring(2).trim() : "";
                    if (!inlineText.isEmpty()) {
                        textBuffer.append(inlineText).append("\n");
                    }

                    String line;
                    while ((line = console.readLine()) != null) {
                        if (MESSAGE_DELIMITER.equals(line.trim())) break;
                        textBuffer.append(line).append("\n");
                    }

                    String text = textBuffer.toString().trim();
                    if (text.isEmpty()) {
                        System.out.println("Текст не может быть пустым");
                        continue;
                    }

                    String xml = "<message xmlns=\"http://messaging.com/schema\">" +
                            "<request>" +
                            "<user>" + escapeXml(user) + "</user>" +
                            "<text>" + escapeXml(text) + "</text>" +
                            "</request>" +
                            "</message>";

                    connection.send(xml);
                    continue;
                }

                System.out.println("Неизвестная команда");
                printHelp();
            }

        } catch (Exception e) {
            System.out.println("Error read console" + e.getMessage());
        }
    }

    private String escapeXml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private void printHelp() {
        System.out.println("Команды:");
        System.out.println("  -m <text>  → многострочный режим (###END### для завершения)");
        System.out.println("  -m         → многострочный режим (###END### для завершения)");
        System.out.println("  -h         → выход");
    }
}