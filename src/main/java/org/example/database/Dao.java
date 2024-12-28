package org.example.database;

import org.example.entities.Message;
import org.example.entities.User;

import java.util.List;

public interface Dao {
    void saveMessage(Message message);

    List<Message> getAllMessagesForChat(int chat_id);
}
