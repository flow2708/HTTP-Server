package ru.flow.httpserver.dao;

import ru.flow.httpserver.Utils.PasswordUtils;
import ru.flow.httpserver.entities.User;

import java.io.File;
import java.sql.*;

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
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "username TEXT NOT NULL UNIQUE,"
                + "email TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "balance INTEGER DEFAULT 0)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Таблица users проверена/создана");
        }
    }

    public boolean saveUser(String username, String email, String password, int balance) {
        String insertUser = "INSERT INTO users (username, email, password, balance) VALUES (?, ?, ?, ?)";
        String hashedPassword = PasswordUtils.hashPassword(password);

        try {
            connect(); // Убедимся, что соединение установлено
            prstatmt = connection.prepareStatement(insertUser);
            prstatmt.setString(1, username);
            prstatmt.setString(2, email);
            prstatmt.setString(3, hashedPassword);
            prstatmt.setInt(4, balance);

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
                        resSet.getInt("balance")
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