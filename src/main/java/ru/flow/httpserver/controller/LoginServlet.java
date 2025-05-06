package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.flow.httpserver.dao.SQLite;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SQLite db = new SQLite();
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

        try {
            boolean isSaved = db.saveUser(username, email, password);
            if (isSaved) {
                response.getWriter().println("Вы успешно зарегистрировались!");
            }
            response.sendRedirect("src/main/resources/page1.html");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
