package org.example.database;

import org.example.entities.LoginData;
import org.example.entities.Message;
import org.example.entities.RegisterData;
import org.example.entities.User;

import java.sql.SQLException;
import java.util.List;

public interface Dao {
    void saveMessage(Message message) throws SQLException;

    List<Message> getAllMessagesForChat(int chatId) throws SQLException;

    void createChat(User user1, User user2) throws SQLException;

    boolean saveUser(RegisterData registerData) throws SQLException;

    boolean checkUser(LoginData loginData) throws SQLException;
}
