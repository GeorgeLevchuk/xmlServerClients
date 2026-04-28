package com.example.xmlserver.xmlclient;

import com.messaging.schema.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class XmlClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 9090;

        ClientConnection connection = new ClientConnection(host, port);
        boolean connected = connection.tryConnect();

        if (!connected) {
            System.out.println("❌ Не удалось подключиться к серверу");
            return;
        }

        try {
            connection.connect();
            System.out.println("✅ Connected to server");

            // поток чтения с сервера
            Thread serverReader = new Thread(connection::readFromServer);
            serverReader.start();

            // поток чтения с клавиатуры
            ConsoleReader consoleReader = new ConsoleReader(connection);
            Thread consoleThread = new Thread(new ConsoleReader(connection));
            consoleThread.start();

            // ждём завершения
            serverReader.join();
            consoleThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}