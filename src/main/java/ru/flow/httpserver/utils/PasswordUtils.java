package ru.flow.httpserver.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static final int ROUNDS = 12;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(ROUNDS));
    }
    public static boolean checkPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
            public static void validate(String password) {
                if (password == null) {
                    throw new IllegalArgumentException("Пароль не может быть пустым");
                }
                if (!password.matches(".*[A-Z].*")) {
                    throw new IllegalArgumentException("Пароль должен содержать не менее одной буквы в верхнем регистре");
                }
                if (!password.matches(".*[a-z].*")) {
                    throw new IllegalArgumentException("Пароль должен содержать не менее одной буквы в нижнем регистре");
                }
                if (!password.matches(".*\\d.*")) {
                    throw new IllegalArgumentException("Пароль должен содержать не менее одной цифры");
                }
            }
    }
