package ru.flow.httpserver.dao;

import ru.flow.httpserver.entities.Post;
import ru.flow.httpserver.utils.PasswordUtils;
import ru.flow.httpserver.entities.User;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLite {
    private static final String DB_URL = "jdbc:sqlite:C:/httpserverDB/database.db";
    private static Connection connection;
    private static PreparedStatement prstatmt;
    private static ResultSet resSet;

    // Инициализация соединения и создание таблицы при первом подключении
    public static void connect() throws ClassNotFoundException, SQLException {
        try {
            String folderPath = "C:/httpserverDB";
            File folder = new File(folderPath);

            if (!folder.exists()) {
                boolean createrd = folder.mkdir();
                if (createrd) {
                    System.out.println("Папка httpserverDB создана на диске C");
                } else {
                    System.out.println("Не удалось создать папку: " + folderPath);
                }
            } else {
                System.out.println("Папка уже существует: " + folderPath);
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createUsersTable(); // Создаём таблицу при подключении
            System.out.println("База подключена и таблицы проверены!");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            throw e;
        }
    }

    // Метод для создания таблицы users
    private static void createUsersTable() throws SQLException {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "username TEXT NOT NULL UNIQUE,"
                + "email TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "socialrating INTEGER DEFAULT 0)";

        String createFriendRequestsTableSQL = "CREATE TABLE IF NOT EXISTS friend_requests ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "sender TEXT NOT NULL,"
                + "receiver TEXT NOT NULL,"
                + "status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING', 'ACCEPTED', 'REJECTED')),"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (sender) REFERENCES users(username) ON DELETE CASCADE,"
                + "FOREIGN KEY (receiver) REFERENCES users(username) ON DELETE CASCADE,"
                + "UNIQUE(sender, receiver))";
        String createPostsTableSQL = "CREATE TABLE IF NOT EXISTS posts ("
                + "post_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "content TEXT NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "like_count INTEGER DEFAULT 0,"
                + "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTableSQL);
            stmt.execute(createFriendRequestsTableSQL);
            stmt.execute(createPostsTableSQL);
            System.out.println("Таблицы users, friend_requests, posts проверены/созданы");
        }
    }
    /**-------------------------------------------users--------------------------------------------------------**/
    public boolean saveUser(String username, String email, String password, int socialrating) {
        String insertUser = "INSERT INTO users (username, email, password, socialrating) VALUES (?, ?, ?, ?)";
        String hashedPassword = PasswordUtils.hashPassword(password);

        try {
            connect(); // Убедимся, что соединение установлено
            prstatmt = connection.prepareStatement(insertUser);
            prstatmt.setString(1, username);
            prstatmt.setString(2, email);
            prstatmt.setString(3, hashedPassword);
            prstatmt.setInt(4, socialrating);

            int affectedRows = prstatmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка при сохранении пользователя: " + e.getMessage());
            return false;
        } finally {
            closeStatement(prstatmt);
        }
    }

    public User findByUsername(String username) {
        String findUser = "SELECT * FROM users WHERE username = ?";

        try {
            connect(); // Убедимся, что соединение установлено
            prstatmt = connection.prepareStatement(findUser);
            prstatmt.setString(1, username);
            resSet = prstatmt.executeQuery();

            if (resSet.next()) {
                return new User(
                        resSet.getString("username"),
                        resSet.getString("email"),
                        resSet.getString("password"),
                        resSet.getInt("socialrating")
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка при поиске пользователя: " + e.getMessage());
        } finally {
            closeResultSet(resSet);
            closeStatement(prstatmt);
        }
        return null;
    }
    /**------------------------------------------------------------------------------------------------------------------**/
    /**-------------------------------------------friend_requests--------------------------------------------------------**/
    public boolean sendFriendRequest(String sender, String receiver) throws SQLException {
        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("Нельзя отправить запрос самому себе");
        }

        String sql = "INSERT INTO friend_requests (sender, receiver, status) VALUES (?, ?, 'PENDING')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean acceptFriendRequest(int requestId, String receiver) throws SQLException, ClassNotFoundException {
        // Убедимся, что соединение активно
        connect();

        String sql = "UPDATE friend_requests SET status = 'ACCEPTED' " +
                "WHERE id = ? AND receiver = ? AND status = 'PENDING'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.setString(2, receiver);
            int updated = stmt.executeUpdate();

            if (updated == 0) {
                System.err.println("Не удалось обновить запрос. Возможные причины:");
                System.err.println("- Неправильный ID запроса: " + requestId);
                System.err.println("- Получатель не совпадает: " + receiver);
                System.err.println("- Запрос уже не в статусе PENDING");
            }

            return updated > 0;
        }
    }

    // Отклонить запрос
    public boolean rejectFriendRequest(int requestId, String receiver) throws SQLException, ClassNotFoundException {
        connect();
        
        String sql = "UPDATE friend_requests SET status = 'REJECTED' " +
                "WHERE id = ? AND receiver = ? AND status = 'PENDING'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.setString(2, receiver);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean cancelFriendRequest(int requestId, String senderUsername) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE id = ? AND sender = ? AND status = 'PENDING'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.setString(2, senderUsername);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean removeFriend(String user1, String user2) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE " +
                "((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) " +
                "AND status = 'ACCEPTED'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);
            return stmt.executeUpdate() > 0;
        }
    }

    public String getFriendshipStatus(String user1, String user2) throws SQLException {
        String sql = "SELECT status FROM friend_requests WHERE " +
                "(sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                "ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);

            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("status") : "NOT_EXISTS";
        }
    }
    /**
     * Получает ID активного запроса в друзья между пользователями
     * @param sender Отправитель запроса
     * @param receiver Получатель запроса
     * @return ID запроса или -1 если не найден
     */
    public int getRequestId(String sender, String receiver) throws SQLException {
        String sql = "SELECT id FROM friend_requests " +
                "WHERE sender = ? AND receiver = ? AND status = 'PENDING' LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    /**
     * Проверяет, является ли пользователь отправителем запроса
     * @param potentialSender Проверяемый отправитель
     * @param receiver Получатель
     * @return true если пользователь - отправитель активного запроса
     */
    public boolean isRequestSender(String potentialSender, String receiver) throws SQLException {
        String sql = "SELECT 1 FROM friend_requests " +
                "WHERE sender = ? AND receiver = ? AND status = 'PENDING'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, potentialSender);
            stmt.setString(2, receiver);
            return stmt.executeQuery().next();
        }
    }
    public List<String> getFriendRequestSenders(String receiver) throws SQLException, ClassNotFoundException {
        List<String> senders = new ArrayList<>();
        String sql = "SELECT sender FROM friend_requests WHERE receiver = ? AND status = 'PENDING'";

        connect(); // Убедимся, что соединение установлено

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, receiver);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                senders.add(rs.getString("sender"));
            }
        }

        return senders;
    }
    /**
     * Получает список друзей пользователя (взаимно принятые заявки)
     * @param username имя пользователя для которого ищем друзей
     * @return список имен друзей
     */
    public List<String> getFriendsList(String username) throws SQLException, ClassNotFoundException {
        List<String> friends = new ArrayList<>();
        String sql = "SELECT sender, receiver FROM friend_requests " +
                "WHERE (sender = ? OR receiver = ?) AND status = 'ACCEPTED'";

        connect(); // Убедимся, что соединение установлено

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                // Добавляем в список противоположного пользователя
                if (sender.equals(username)) {
                    friends.add(receiver);
                } else {
                    friends.add(sender);
                }
            }
        }

        return friends;
    }
    /**------------------------------------------------------------------------------------------------------------------**/
    /**-------------------------------------------posts--------------------------------------------------------**/
    public boolean createPost(String username, String content) throws SQLException {
        String sql = "INSERT INTO posts (username, content) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, content);
            return stmt.executeUpdate() > 0;
        }
    }
    public List<Post> getUserPostsList(String username) throws SQLException, ClassNotFoundException {
        List<Post> userPostsList = new ArrayList<>();
        String sql = "SELECT username, content, like_count FROM posts WHERE username = ?";

        connect();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getString("username"),
                            rs.getString("content"),
                            rs.getInt("like_count")
                    );
                    userPostsList.add(post);
                }
            }
        }
        return userPostsList;
    }

    /**------------------------------------------------------------------------------------------------------------------**/
    // Вспомогательные методы для закрытия ресурсов
    private static void closeStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии statement: " + e.getMessage());
            }
        }
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии result set: " + e.getMessage());
            }
        }
    }
}