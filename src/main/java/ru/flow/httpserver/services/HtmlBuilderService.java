package ru.flow.httpserver.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ru.flow.httpserver.dao.SQLite;
import ru.flow.httpserver.entities.Post;
import ru.flow.httpserver.entities.User;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class HtmlBuilderService {
    public static void renderUserPostsList(PrintWriter out, SQLite db, String username)
            throws SQLException, ClassNotFoundException {

        List<Post> posts = db.getUserPostsList(username);

        if (posts == null || posts.isEmpty()) {
            out.println("<p>Нет постов</p>");
            return;
        }

        out.println("<div class='posts'>");
        for (Post post : posts) {
            String firstLetter = post.getUsername().substring(0, 1).toUpperCase();
            out.println("<div class='post'>");
            out.printf("<div class='user-avatar'>%s</div>", firstLetter);
            out.printf("<h3>%s</h3>", post.getUsername());
            out.printf("<p>%s</p>", post.getContent());
            out.printf("<div>Лайков: %d</div>", post.getLike_count());
            out.println("</div>");
        }
        out.println("</div>");
    }
}
