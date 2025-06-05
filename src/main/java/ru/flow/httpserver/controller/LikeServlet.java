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
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/like")
public class LikeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SQLite db = new SQLite();

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        String username = currentUser.getUsername();
        int post_id = Integer.parseInt(req.getParameter("post_id"));
        String action = req.getParameter("action");

        PrintWriter out = resp.getWriter();
        boolean success = false;

        try {
            switch (action) {
                case "like":
                    success = db.addLikeToPost(post_id) && db.createLike(post_id, username);

                    break;
                case "unlike":
                    success = db.removeLikeFromPost(post_id) && db.removeLike(post_id, username);

                    break;
            }

            if (success) {
                req.setAttribute("message", "Лайк успешно " + ("like".equals(action) ? "добавлен" : "удален"));
            } else {
                req.setAttribute("error", "Не удалось обработать лайк");
            }

            String referer = req.getHeader("Referer");
            if (referer != null) {
                resp.sendRedirect(referer);
            } else {
                resp.sendRedirect(req.getContextPath() + "mainpage.html");
            }
        } catch (SQLException e) {

        }
    }
}
