package org.example.database;

import org.example.entities.LoginData;
import org.example.entities.Message;
import org.example.entities.RegisterData;

import java.util.List;

public interface Dao {
    void saveMessage(Message message);

    List<Message> getAllMessagesForChat(int chat_id);

    boolean saveUser(RegisterData registerData);

    boolean checkUser(LoginData loginData);
}
