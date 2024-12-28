package org.example.database;

import org.example.entities.Message;
import org.example.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessangerDao implements Dao {
    @Override
    public void saveMessage(Message message) {
        String insertQuery = "INSERT INTO messages (sender_id, message_text) values (?, ?)";
        try (
                Connection connection = DatabaseConnection.getConnection();
        ) {
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, message.sender().id());
            statement.setString(2, message.text());
        } catch (SQLException sqlException){
            System.out.println("Невозможно вставить сообщение в таблицу:");
            sqlException.printStackTrace();
        }
    }

    @Override
    public List<Message> getAllMessagesForChat(int chat_id) {
        ArrayList<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
        ) {
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Message message = new Message(
                        result.getInt("message_id"),
                        new User(result.getInt("sender_id"), "Tom"),
                        result.getString("message_text")
                );
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
}
