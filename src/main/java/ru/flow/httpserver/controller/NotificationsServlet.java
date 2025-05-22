package ru.flow.httpserver.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.flow.httpserver.Utils.HtmlEscapingUtils;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SQLite db = new SQLite();
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
        PrintWriter out = resp.getWriter();
        List<String> requests;

        if(currentUser == null) {
            resp.sendRedirect("register.html");
        }

        try {
            requests = db.getFriendRequestSenders(currentUser.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        resp.setContentType("text/html;charset=UTF-8");

        for (String sender : requests) {
            try {
                int requestId = db.getRequestId(sender, currentUser.getUsername());

                out.println("<div class='sender'>");
                out.println("<p>" + sender + "</p>");
                out.println("<form action='friendship' method='POST'>");
                out.println("<input type='hidden' name='action' value='accept_request'>");
                out.println("<input type='hidden' name='request_id' value='" + requestId + "'>");
                out.println("<button type='submit' class='active-button'>Принять</button>");
                out.println("</form>");
                out.println("</div>");
            } catch (SQLException e) {
                out.println("<p>Ошибка при обработке запроса</p>");
                throw new RuntimeException(e);
            }
        }
    }
}
