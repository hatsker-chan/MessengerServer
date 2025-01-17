package org.example.api;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/ws/chat")
public class WebSocketConnection {
    private static final String UPDATE_JSON = "{\"update\":true}";
    private static final Set<Session> clients = new HashSet<>();

    public static void notifyChat() {
        for (Session client : clients) {
            if (client.isOpen()) {
                try {
                    client.getBasicRemote().sendText(UPDATE_JSON);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

}
