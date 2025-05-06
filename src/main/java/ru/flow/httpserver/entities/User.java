package ru.flow.httpserver.entities;


public class User {
    private String username;
    private String email;
    private String password;
    private int balance;
    public User(String username,String email, String password, int balance) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }
    public String getUsername() {
        return this.username;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public int getBalance() {
        return this.balance;
    }
}
