package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.flow.httpserver.Utils.PasswordUtils;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            SQLite db = new SQLite();
            User user = db.findByUsername(username);

            if (user == null) {
                resp.sendRedirect("login.html?error=not_found");
                return;
            }

            if (!PasswordUtils.checkPassword(password, user.getPassword())) {
                resp.sendRedirect("login.html?error=wrong_pass");
                return;
            }

            req.getSession().setAttribute("user", user);
            resp.sendRedirect("profile");

        } catch (Exception e) {
            resp.sendRedirect("error500.html");
        }
    }
}