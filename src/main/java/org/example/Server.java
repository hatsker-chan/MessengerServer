package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.database.MessangerDao;
import org.example.entities.Message;
import org.example.entities.User;
import org.example.pojo.MessageDto;
import org.example.pojo.MessagesResponse;
import org.example.pojo.UserDto;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Server {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessangerDao dao = new MessangerDao();

    private final HttpServer httpServer;

    public Server(InetSocketAddress address) throws IOException {
        httpServer = HttpServer.create(address, 0);
        setupContext(httpServer);
    }


    private void setupContext(HttpServer httpServer) {
        httpServer.createContext(API_PATH_MESSAGES, (HttpExchange exchange) -> {
            System.out.println("Начало обработки" + API_PATH_MESSAGES);
            System.out.println(exchange.getRequestMethod());
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                    List<Message> messages = dao.getAllMessagesForChat(1);

                    List<MessageDto> dtos = messages.stream().map((Message message) -> {
                        User user = message.sender();
                        return new MessageDto(
                                new UserDto(
                                        user.id(),
                                        user.name()
                                ),
                                message.text()
                        );
                    }).toList();

                    String responseString = objectMapper.writeValueAsString(new MessagesResponse(dtos));
                    System.out.println("Response:");
                    System.out.println(responseString);
                    byte[] bs = responseString.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bs.length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bs);
                    } catch (IOException e) {
                        exchange.sendResponseHeaders(502, 0);
                        e.printStackTrace();
                        System.out.println("IO problem");
                    }
                }
                case "POST" -> {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                    exchange.getResponseHeaders().add("Accept", "application/json");
                    exchange.sendResponseHeaders(201, 1);


                    try (BufferedInputStream bis = new BufferedInputStream(exchange.getRequestBody())) {
                        Message message = objectMapper.readValue(bis, Message.class);
                        System.out.println("Post message: " + message.toString());
                        dao.saveMessage(message);
                    }
                }
                case "OPTIONS" -> {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(204, -1); // 204 No Content
                }
            }
            System.out.println("Конец обработки");
        });
    }

    private void showExchangeInfo(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();

        System.out.println("Получен запрос (заголовки)");
        headers.forEach((String key, List<String> list) -> {
            System.out.println(key + " " + headers.get(key));
        });

        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
             BufferedReader reader = new BufferedReader(isr)
        ) {
            System.out.println("Получен запрос (тело)");
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.out.println("Невозможно прочитать тело запроса:");
        }
        System.out.println("Метод: " + exchange.getRequestMethod());
        System.out.println("URI: " + exchange.getRequestURI());
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    private static final String API_PATH_HELLO = "/api/hello";
    private static final String API_PATH_MESSAGES = "/api/messages";

}
