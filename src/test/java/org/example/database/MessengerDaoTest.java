package org.example.database;

import org.example.entities.Message;
import org.example.entities.RegisterData;
import org.example.entities.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessengerDaoTest {
    static private ConnectionSource connectionSource;
    static private MessangerDao messangerDao;

    static private void createTables() {
        final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users\n" +
                "(\n" +
                "    user_id serial PRIMARY KEY,\n" +
                "    nickname character varying(32),\n" +
                "    password character varying(32),\n" +
                "    email character varying(32)\n" +
                ")";

        final String CREATE_CHAT_USERS_TABLE = "CREATE TABLE IF NOT EXISTS chat_users\n" +
                "(\n" +
                "    chat_id integer NOT NULL,\n" +
                "    user_id integer NOT NULL,\n" +
                "    CONSTRAINT chat_users_pkey PRIMARY KEY (chat_id, user_id),\n" +
                "    CONSTRAINT chat_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES users (user_id) \n" +
                ")";
        final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages\n" +
                "(\n" +
                "    message_id serial PRIMARY KEY,\n" +
                "    sender_id integer,\n" +
                "    message_text character varying(1024),\n" +
                "    chat_id integer NOT NULL\n" +
                ")";

        try (
                Connection connection = connectionSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
            statement.execute(CREATE_USERS_TABLE);
            statement.execute(CREATE_CHAT_USERS_TABLE);
            statement.execute(CREATE_MESSAGES_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static private void dropTables() {
        final String DROP_CHAT_USERS_TABLE = "DROP TABLE IF EXISTS chat_users";
        final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS users";

        final String DROP_MESSAGES_TABLE = "DROP TABLE IF EXISTS messages";

        try (Connection connection = connectionSource.getConnection();
             Statement statement = connection.createStatement();) {
            statement.execute(DROP_CHAT_USERS_TABLE);
            statement.execute(DROP_USERS_TABLE);

            statement.execute(DROP_MESSAGES_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void setUp() throws Exception {
        connectionSource = () -> {
            final String URL = "jdbc:postgresql://localhost:5432/testmessage";
            final String USER = "postgres";
            final String PASSWORD = "85493fjsvns2";
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                return connection;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        };

        messangerDao = new MessangerDao(connectionSource);
        dropTables();
        createTables();
    }

    @AfterAll
    public static void cleanUp() throws Exception {
        dropTables();
    }

    @Test
    public void test() {
        long startTime = System.currentTimeMillis();
        try {
            User user1 = new User(1, "alex");
            User user2 = new User(2, "mike");
            User user3 = new User(3, "tom");
            messangerDao.saveUser(new RegisterData(
                    "alex", "test@gmail.com", "qwerty"
            ));
            messangerDao.saveUser(new RegisterData(
                    "mike", "test2@gmail.com", "qwerty"
            ));
            messangerDao.saveUser(new RegisterData(
                    "tom", "test@gmail.com", "asdfg"
            ));

            messangerDao.createChat(user1, user2);
            messangerDao.createChat(user2, user3);
            messangerDao.createChat(user3, user1);

            messangerDao.saveMessage(
                    new Message(user1, "Привет!", 1)
            );

            messangerDao.saveMessage(
                    new Message(user2, "И тебе привет!", 1)
            );

            messangerDao.saveMessage(new Message(user3, "Новое сообщение от user3 к user2", 2));
            messangerDao.saveMessage(new Message(user3, "Новое сообщение от user3 к user1", 3));

            List<Message> messages = messangerDao.getAllMessagesForChat(1);

            assertEquals(2, messages.size());

            assertEquals(1, messages.get(0).sender().id());
            assertEquals("alex", messages.get(0).sender().name());
            assertEquals(1, messages.get(0).chatId());
            assertEquals("Привет!", messages.get(0).text());

            assertEquals(2, messages.get(1).sender().id());
            assertEquals("mike", messages.get(1).sender().name());
            assertEquals(1, messages.get(1).chatId());
            assertEquals("И тебе привет!", messages.get(1).text());

            List<Message> messages2 = messangerDao.getAllMessagesForChat(2);
            assertEquals(1, messages2.size());
            assertEquals(new Message(user3, "Новое сообщение от user3 к user2", 2), messages2.get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration + " milliseconds");
    }
}
