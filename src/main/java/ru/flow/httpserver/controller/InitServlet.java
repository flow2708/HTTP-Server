package ru.flow.httpserver.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import ru.flow.httpserver.dao.SQLite;

@WebServlet(loadOnStartup = 1)
public class InitServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Инициализация БД...");
            SQLite db = new SQLite();
            db.connect();
            System.out.println("БД успешно инициализирована");
        } catch (Exception e) {
            System.err.println("Ошибка инициализации БД:");
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}