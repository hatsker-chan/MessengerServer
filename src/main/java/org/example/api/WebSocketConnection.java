package org.example.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/ws/chat")
public class WebSocketConnection {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Set<Session> clients = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws JsonProcessingException {
        String messageData = String.format("{\"message\": \"%s\"}", message);
        System.out.println(messageData);
        String json = objectMapper.writeValueAsString(messageData);
        broadcast(messageData);
    }

    public static void broadcast(String message) {
        for (Session client : clients) {
            if (client.isOpen()) {
                try {
                    client.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void notifyChat() {
        for (Session client : clients) {
            if (client.isOpen()) {
                try {
                    client.getBasicRemote().sendText("{\"update\":true}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
