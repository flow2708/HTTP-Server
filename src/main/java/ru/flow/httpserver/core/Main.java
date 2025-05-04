package ru.flow.httpserver.core;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.start();
    }
}
