package ru.flow.httpserver.core.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpHandler {
    public static void accept(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept(); // Принимаем подключение
        client.configureBlocking(false); // Делаем неблокирующим
        client.register(selector, SelectionKey.OP_READ); // Ждём, когда клиент пришлёт данные
        System.out.println("Новое соединение: " + client.getRemoteAddress());
    }
    public static void handleRequest(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = client.read(buffer);

        if (bytesRead == -1) {
            client.close();
            return;
        }

        buffer.flip();
        String request = new String(buffer.array(), 0, bytesRead);
        System.out.println("Запрос:\n" + request);

        // Отправляем простой HTTP-ответ
        String response = "HTTP/1.1 200 OK\r\nContent-Length: 13\r\n\r\nHello, Habr!";
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        client.write(responseBuffer);
        client.close(); // Закрываем соединение
    }
}
