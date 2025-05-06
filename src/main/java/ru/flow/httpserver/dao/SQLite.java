package ru.flow.httpserver.dao;

import ru.flow.httpserver.entities.User;

import java.sql.*;
import java.util.Scanner;

public class SQLite {
    private static String dbPath = "jdbc:sqlite:src/main/resources/database.db";
    private static Connection connection;
    private static Statement statmt;
    private static PreparedStatement prstatmt;
    private static ResultSet resSet;
    private String username;
    private String email;
    private String password;

    public static void connect() throws ClassNotFoundException, SQLException {
        try {
            connection = null;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(dbPath);
            statmt = connection.createStatement();

            System.out.println("База Подключена!");
        } catch (SQLException e) {
            System.out.println("Не удалось подключиться к базе данных");
        }
    }

    public boolean saveUser(String username, String email, String password) throws SQLException, ClassNotFoundException {
        try {
            String insertUser = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            connect();

            prstatmt = connection.prepareStatement(insertUser);
            prstatmt.setString(1, username);
            prstatmt.setString(2, email);
            prstatmt.setString(3, password);
            prstatmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public User findByUsername(String username) throws ClassNotFoundException {
        String findUser = "SELECT * FROM users WHERE username = ?";

        try {
            connect();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

