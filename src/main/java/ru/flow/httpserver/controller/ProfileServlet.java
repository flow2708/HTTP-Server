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
        Integer visitCounter = (Integer) session.getAttribute("visitCounter");

        if(session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        if (visitCounter == null) {
            visitCounter = 1;
        } else {
            visitCounter++;
        }

        session.setAttribute("visitCounter", visitCounter);
        // Отображение профиля
        User user = (User) session.getAttribute("user");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<form action=\"/search\" method=\"GET\">\n" +
                "  <input type=\"text\" name=\"query\" placeholder=\"Поиск...\">\n" +
                "  <button type=\"submit\">Искать</button>\n" +
                "</form>\n");
        out.println("<h1>Ваш профиль</h1>");
        out.println("<p>Имя пользователя: " + user.getUsername() + "</p>");
        out.println("<p>Email: " + user.getEmail() + "</p>");
        out.println("Страница посещена " + visitCounter + " раз");
    }
}