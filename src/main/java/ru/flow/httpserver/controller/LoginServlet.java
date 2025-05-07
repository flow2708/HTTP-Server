package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.io.IOException;
import java.sql.SQLException;
/** Пока что не используется, post запрос обрабатывается в HttpHandler **/
@WebServlet("/register")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (username == null) {
            response.sendError(400, "Имя пользователя обязательно!");
        } else if (email == null) {
            response.sendError(400, "Email обязателен!");
        } else if (password == null) {
            response.sendError(400, "Пароль обязателен!");
        }

        SQLite db = new SQLite();
        try {
            User existingUser = db.findByUsername(username);
            if (existingUser != null) {
                if (password.equals(existingUser.getPassword())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", existingUser);
                    response.sendRedirect("src/main/resources/profile.html");
                } else {
                    response.sendError(401, "Неверный пароль");
                }
            } else {
            boolean isSaved = db.saveUser(username, email, password, 0);
            if (isSaved) {
                User newUser = new User(username, email, password, 0);
                HttpSession session = request.getSession();
                session.setAttribute("user", newUser);
                response.sendRedirect("src/main/resources/profile.html");
            } else {
                response.sendError(500, "Ошибка при регистрации");
            }
        }
    } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
