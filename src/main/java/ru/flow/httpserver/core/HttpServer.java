package ru.flow.httpserver.core;

import ru.flow.httpserver.core.handlers.HttpHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class HttpServer {
    private static final int PORT = 8080;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private boolean isRunning = false;
    public void start() throws IOException {
        // Открываем неблокирующий серверный сокет
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(PORT));
        serverSocket.configureBlocking(false);

        // Создаём селектор для обработки событий
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        isRunning = true;
        System.out.println("Сервер запущен на порту " + PORT);

        while(true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) HttpHandler.accept(selector, serverSocket);
                if (key.isReadable()) HttpHandler.handleRequest(key);
            }
            selector.selectedKeys().clear();
        }
    }
    public void stop() {
        isRunning = false;
        try {
            if (selector != null) {
                selector.wakeup(); // Прерываем блокирующий select()
                selector.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Сервер остановлен");
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }
}