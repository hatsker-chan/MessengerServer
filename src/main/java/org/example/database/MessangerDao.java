package org.example.database;

import org.example.entities.Message;
import org.example.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessangerDao implements Dao {
    @Override
    public void saveMessage(Message message) {
        String insertQuery = "INSERT INTO messages (sender_id, message_text, chat_id) values (?, ?, ?)";
        try (
                Connection connection = DatabaseConnection.getConnection();
        ) {
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, message.sender().id());
            statement.setString(2, message.text());
            statement.setInt(3, 1);
            statement.executeUpdate();
        } catch (SQLException sqlException){
            System.out.println("Невозможно вставить сообщение в таблицу:");
            sqlException.printStackTrace();
        }
    }

    @Override
    public List<Message> getAllMessagesForChat(int chat_id) {
        ArrayList<Message> messages = new ArrayList<>();
        String query = "SELECT messages.message_id, messages.message_text, messages.sender_id, users.nickname, messages.chat_id  FROM messages \n" +
                "JOIN chat_users ON chat_users.chat_id = messages.chat_id AND chat_users.user_id = messages.sender_id\n" +
                "join users on users.user_id = messages.sender_id\n" +
                "order by messages.message_id";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
        ) {
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Message message = new Message(
                        new User(result.getInt("sender_id"), result.getString("nickname")),
                        result.getString("message_text"),
                        result.getInt("chat_id")
                );
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
