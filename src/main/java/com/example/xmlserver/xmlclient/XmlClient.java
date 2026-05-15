package com.example.xmlserver.xmlclient;

public class XmlClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 9090;
        ClientConnection connection = new ClientConnection(host, port);
        boolean connected = connection.tryConnect();

        if (!connected) {
            System.out.println("Не удалось подключиться к серверу");
            return;
        }

        try {
            connection.connect();
            System.out.println("Connected to server");
            Thread serverReader = new Thread(connection::readFromServer);
            serverReader.start();
            Thread consoleThread = new Thread(new ConsoleReader(connection));
            consoleThread.start();
            serverReader.join();
            consoleThread.join();

        } catch (Exception e) {
            System.out.println("Exception job client - " + e.getMessage());
        } finally {
            connection.close();
        }
    }
}