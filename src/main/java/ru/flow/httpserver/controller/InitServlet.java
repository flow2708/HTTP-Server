package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import ru.flow.httpserver.dao.SQLite;

@WebServlet(name = "InitServlet", urlPatterns = "/init", loadOnStartup = 1)
public class InitServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        try {
            SQLite db = new SQLite();
            db.connect(); // Создаст БД при старте приложения
        } catch (Exception e) {
            throw new ServletException("DB initialization failed", e);
        }
    }
}