package ru.flow.httpserver.entities;

public class Post {
    int post_id;
    String username;
    String content;
    int like_count;
    public Post(int post_id, String username, String content, int like_count) {
        this.post_id = post_id;
        this.username = username;
        this.content = content;
        this.like_count = like_count;
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
    public int getLike_count() {
        return this.like_count;
    }
}
