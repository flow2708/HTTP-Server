package ru.flow.httpserver.Utils;

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
}
