package ru.flow.httpserver.controller.search;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/friendship")
public class FriendShipServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SQLite db = new SQLite();
        String action = req.getParameter("action");
        String targetUsername = req.getParameter("target");
        String requestId;
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            resp.sendRedirect("register.html");
        }

        switch (action) {
            case "send_request":
                try {
                    db.sendFriendRequest(currentUser.getUsername(), targetUsername);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "accept_request":
                requestId = req.getParameter("request_id");
                try {
                    db.acceptFriendRequest(Integer.parseInt(requestId), targetUsername);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "reject_request":
                requestId = req.getParameter("request_id");
                try {
                    db.rejectFriendRequest(Integer.parseInt(requestId), targetUsername);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "cancel_request":
                requestId = req.getParameter("request_id");
                try {
                    db.cancelFriendRequest(Integer.parseInt(requestId), currentUser.getUsername());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "remove_friend":
                try {
                    db.removeFriend(currentUser.getUsername(), targetUsername);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }
}
