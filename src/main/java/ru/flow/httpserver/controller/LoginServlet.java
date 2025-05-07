package ru.flow.httpserver.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.io.IOException;
/** Пока что не используется, post запрос обрабатывается в HttpHandler **/
@WebServlet("/register")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            SQLite db = new SQLite();
            User user = db.findByUsername(username);

            if (user != null) {
                // Вход
                if (user.getPassword().equals(password)) {
                    req.getSession().setAttribute("user", user);
                    resp.sendRedirect("profile.html");
                } else {
                    resp.sendRedirect("login.html?error=wrong_password");
                }
            } else {
                // Регистрация
                if (db.saveUser(username, email, password, 0)) {
                    req.getSession().setAttribute("user", new User(username, email, password, 0));
                    resp.sendRedirect("profile.html");
                } else {
                    resp.sendRedirect("login.html?error=registration_failed");
                }
            }
        } catch (Exception e) {
            resp.sendRedirect("login.html?error=server_error");
        }
    }
}