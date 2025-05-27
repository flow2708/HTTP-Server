package ru.flow.httpserver.entities;

public class Post {
    String username;
    String content;
    int like_count;
    public Post(String username, String content, int like_count) {
        this.username = username;
        this.content = content;
        this.like_count = like_count;
    }
    public String getUsername() {
        return this.username;
    }
    public String getContent() {
        return this.content;
    }
    public int getLike_count() {
        return this.like_count;
    }
}
