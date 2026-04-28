package com.example.xmlserver.xmlclient;

import com.messaging.schema.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class XmlClient {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    public static void main(String[] args) {

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server");

            while (true) {
                System.out.print("Enter command (-m message / -h): ");
                String input = scanner.nextLine();

                if (input.equals("-h")) {
                    System.out.println("Exit...");
                    break;
                }

                if (input.startsWith("-m ")) {
                    String text = input.substring(3);

                    // генерим XML
                    String xml = buildRequest(text);

                    // отправка
                    out.write(xml);
                    out.newLine();
                    out.flush();

                    // ответ
                    String responseXml = in.readLine();

                    parseResponse(responseXml);
                } else {
                    System.out.println("Unknown command");
                }
            }

        }
        catch (IOException e) {
            System.out.println("❌ Unable to connect to server (" + HOST + ":" + PORT + ")");
            System.out.println("Application terminated.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unexpected error:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    private static String buildRequest(String text) {

        MessageDocument doc = MessageDocument.Factory.newInstance();
        MessageType message = doc.addNewMessage();

        HeaderType header = message.addNewHeader();
        header.setTime(java.util.Calendar.getInstance());

        RequestType request = message.addNewRequest();
        request.setUser(System.getProperty("user.name")); // как в ТЗ
        request.setText(text);

        return doc.xmlText();
    }

    private static void parseResponse(String xml) throws Exception {

        MessageDocument doc = MessageDocument.Factory.parse(xml);
        MessageType message = doc.getMessage();

        if (message.isSetResponse()) {
            StatusType status = message.getResponse().getStatus();

            int code = status.getCode();
            String reason = status.getReason();

            if (code == 0) {
                System.out.println("✅ Message accepted: " + reason);
            } else {
                System.out.println("❌ Message rejected: " + reason);
            }
        }
    }
}