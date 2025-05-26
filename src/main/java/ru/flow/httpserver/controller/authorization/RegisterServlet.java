package ru.flow.httpserver.controller.authorization;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.flow.httpserver.utils.PasswordUtils;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.io.IOException;
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            SQLite db = new SQLite();

            if (db.findByUsername(username) != null) {
                resp.sendRedirect("register.html?error=exists");
                return;
            }

            try {
                PasswordUtils.validate(password);
            } catch (IllegalArgumentException e) {
                resp.sendRedirect("login.html?error=incorrect_password_format");
                return;
            }

            if (!db.saveUser(username, email, password, 0)) {
                resp.sendRedirect("register.html?error=save_failed");
                return;
            }

            req.getSession().setAttribute("user", new User(username, email, password, 0));
            resp.sendRedirect("mainpage.html");

        } catch (Exception e) {
            resp.sendRedirect("error500.html");
        }
    }
}