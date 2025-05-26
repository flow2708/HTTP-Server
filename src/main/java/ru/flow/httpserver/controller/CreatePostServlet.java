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

@WebServlet("/createPost")
public class CreatePostServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SQLite db = new SQLite();
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
        String content = req.getParameter("content");

        if (currentUser == null) {
            resp.sendRedirect("login.html");
        }

        if (content == null || content.trim().isEmpty()) {
            resp.sendError(400, "Текст поста не может быть пустым!");
            return;
        }

        try {
            if (db.createPost(currentUser.getUsername(), content)) {
                resp.sendRedirect("profile");
            } else {
                resp.sendError(500, "Ошибка при создании поста!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
