package com.example.xmlserver.server;

import java.net.*;
import java.util.concurrent.*;

public class XmlServer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(PORT);

        ExecutorService pool = Executors.newFixedThreadPool(10);

        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket client = serverSocket.accept();

            pool.submit(new ClientHandler(client));
        }
    }
}