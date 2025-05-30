package ru.flow.httpserver.entities;

public class Comment {
    int post_id;
    String username;
    String content;
    public Comment (int post_id, String username, String content) {
        this.post_id = post_id;
        this.userame = username;
        this.content = content;
    }
    public int getPost_id() {
        return this.post_id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getContent() {
        return this.content;
    }
}
