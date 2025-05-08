package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.flow.httpserver.entities.User;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Проверка авторизации
        HttpSession session = req.getSession(false);
        if(session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        // Отображение профиля
        User user = (User) session.getAttribute("user");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Ваш профиль</h1>");
        out.println("<p>Логин: " + user.getUsername() + "</p>");
        out.println("<p>Email: " + user.getEmail() + "</p>");
        out.println("<a href='index'>Выйти</a>");
    }
}