package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        Server server = new Server(new InetSocketAddress(8000));

        server.getHttpServer().setExecutor(null);
        server.getHttpServer().start();

//        JettyServer server = new JettyServer(8000);
//        try {
//            server.start();
//            System.out.println("Server started");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

    }
}
