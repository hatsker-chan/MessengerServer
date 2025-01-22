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
    private static final String COLUMN_MAX_CHAT_ID = "max_id";

    private static final String INSERT_MESSAGE = "INSERT INTO messages (sender_id, message_text, chat_id) values (?, ?, ?)";
    private static final String SELECT_MESSAGES = "SELECT MESSAGES.MESSAGE_ID,\n" +
            "\tMESSAGES.MESSAGE_TEXT,\n" +
            "\tMESSAGES.SENDER_ID,\n" +
            "\tUSERS.NICKNAME,\n" +
            "\tMESSAGES.CHAT_ID\n" +
            "FROM MESSAGES\n" +
            "JOIN CHAT_USERS ON CHAT_USERS.CHAT_ID = MESSAGES.CHAT_ID\n" +
            "AND CHAT_USERS.USER_ID = MESSAGES.SENDER_ID\n" +
            "JOIN USERS ON USERS.USER_ID = MESSAGES.SENDER_ID\n" +
            "WHERE MESSAGES.CHAT_ID = (?)\n" +
            "ORDER BY MESSAGES.MESSAGE_ID";
    private static final String CREATE_CHAT = "INSERT INTO chat_users (chat_id, user_id) VALUES (?, ?), (?, ?)";

    private static final String SAVE_USER = "INSERT INTO users (nickname, email, password) values (?, ?, ?)";
    private static final String CHECK_USER = "SELECT users.nickname FROM users WHERE users.email = ? AND users.password = ?";

    private static final String GET_CHAT_USERS_COUNT = "SELECT MAX(chat_id) AS \"max_id\" FROM chat_users";

    private ConnectionSource connectionSource = new DatabaseConnection();

    public MessangerDao() {}

    public MessangerDao(ConnectionSource source) {
        this.connectionSource = source;
    }

    @Override
    public void saveMessage(Message message) throws SQLException {
        try (
                Connection connection = connectionSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE)
        ) {
            statement.setInt(1, message.sender().id());
            statement.setString(2, message.text());
            statement.setInt(3, message.chatId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Message> getAllMessagesForChat(int chat_id) throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();

        try (Connection connection = connectionSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_MESSAGES)
        ) {
            statement.setInt(1, chat_id);
            ResultSet result = statement.executeQuery();
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
    public void createChat(User user1, User user2) throws SQLException {
        try (Connection connection = connectionSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_CHAT)) {
            int chatId = getNewChatId();
            statement.setInt(1, chatId);
            statement.setInt(2, user1.id());
            statement.setInt(3, chatId);
            statement.setInt(4, user2.id());
            statement.executeUpdate();
        }
    }

    @Override
    public void saveUser(RegisterData registerData) throws SQLException {
        try (
                Connection connection = connectionSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SAVE_USER)
        ) {
            statement.setString(1, registerData.nickname());
            statement.setString(2, registerData.email());
            statement.setString(3, registerData.password());
            statement.executeUpdate();
        }
    }

    @Override
    public boolean checkUser(LoginData loginData) throws SQLException {
        try (
                Connection connection = connectionSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(CHECK_USER)
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

    private int getNewChatId() throws SQLException {
        try (
                Connection connection = connectionSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            ResultSet result = statement.executeQuery(GET_CHAT_USERS_COUNT);
            if (result.next()) {
                int maxChatId = result.getInt(COLUMN_MAX_CHAT_ID);
                return maxChatId + 1;
            } else {
                throw new SQLException("Can't get count of chat_users table records");
            }
        }
    }
}