package ru.flow.httpserver.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.sql.SQLException;
import java.util.List;

@WebServlet("/friendsList")
public class FriendsListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        SQLite db = new SQLite();
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
        try {
            List<String> friendsList = db.getFriendsList(currentUser.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
