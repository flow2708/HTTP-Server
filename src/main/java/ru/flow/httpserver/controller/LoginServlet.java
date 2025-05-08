package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
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
            throws ServletException, IOException {

        // 1. Получаем параметры
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // 2. Валидация
        if(username == null || email == null || password == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 3. Логика работы с БД
        try {
            SQLite db = new SQLite();

            // Проверяем существующего пользователя
            User user = db.findByUsername(username);

            if(user != null) {
                // Аутентификация
                if(user.getPassword().equals(password)) {
                    req.getSession().setAttribute("user", user);
                    resp.sendRedirect(req.getContextPath() + "/profile.html");
                    return;
                } else {
                    resp.sendRedirect("login.html?error=auth");
                    return;
                }
            } else {
                // Регистрация
                if(db.saveUser(username, email, password, 0)) {
                    req.getSession().setAttribute("user", new User(username, email, password, 0));
                    resp.sendRedirect(req.getContextPath() + "/profile.html");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/error500.html");
        }

        // Если что-то пошло не так
        resp.sendRedirect("login.html?error=unknown");
    }
}