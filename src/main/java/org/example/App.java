package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {


    public static void main(String[] args) throws IOException {
        Server server = new Server(new InetSocketAddress(8000));

        server.getHttpServer().setExecutor(null);
        server.getHttpServer().start();
        System.out.println("Server started");
    }
}
