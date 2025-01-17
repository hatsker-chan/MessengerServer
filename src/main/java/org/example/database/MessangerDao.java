package org.example.database;

import org.example.entities.LoginData;
import org.example.entities.Message;
import org.example.entities.RegisterData;
import org.example.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessangerDao implements Dao {
    private static final String COLUMN_SENDER_ID = "sender_id";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_MESSAGE_TEXT = "message_text";
    private static final String COLUMN_CHAT_ID = "chat_id";

    private static final String INSERT_MESSAGE = "INSERT INTO messages (sender_id, message_text, chat_id) values (?, ?, ?)";
    private static final String SELECT_MESSAGES = "SELECT messages.message_id, messages.message_text, messages.sender_id, users.nickname, messages.chat_id  FROM messages \n" +
            "JOIN chat_users ON chat_users.chat_id = messages.chat_id AND chat_users.user_id = messages.sender_id\n" +
            "join users on users.user_id = messages.sender_id\n" +
            "order by messages.message_id";

    private static final String SAVE_USER = "INSERT INTO users (nickname, email, password) values (?, ?, ?)";
    private static final String CHECK_USER = "SELECT users.nickname FROM users WHERE users.email = ? AND users.password = ?";

    @Override
    public void saveMessage(Message message) throws SQLException {
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE);
        ) {
            statement.setInt(1, message.sender().id());
            statement.setString(2, message.text());
            statement.setInt(3, 1);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Message> getAllMessagesForChat(int chat_id) throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()
        ) {
            ResultSet result = statement.executeQuery(SELECT_MESSAGES);
            while (result.next()) {
                Message message = new Message(
                        new User(result.getInt(COLUMN_SENDER_ID), result.getString(COLUMN_NICKNAME)),
                        result.getString(COLUMN_MESSAGE_TEXT),
                        result.getInt(COLUMN_CHAT_ID)
                );
                messages.add(message);
            }
            return messages;
        }
    }

    @Override
    public boolean saveUser(RegisterData registerData) throws SQLException {
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SAVE_USER);
        ) {
            statement.setString(1, registerData.nickname());
            statement.setString(2, registerData.email());
            statement.setString(3, registerData.password());
            statement.executeUpdate();
        }
        return true;
    }

    @Override
    public boolean checkUser(LoginData loginData) throws SQLException {
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(CHECK_USER);
        ) {
            statement.setString(1, loginData.email());
            statement.setString(2, loginData.password());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(COLUMN_EMAIL).equals(loginData.email()) && resultSet.getString(COLUMN_PASSWORD).equals(loginData.password());
            }
        }
        return false;
    }
}
