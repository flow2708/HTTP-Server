package ru.flow.httpserver.core.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpHandler {
    public static void accept(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept(); // Принимаем подключение
        client.configureBlocking(false); // Делаем неблокирующим
        client.register(selector, SelectionKey.OP_READ); // Ждём, когда клиент пришлёт данные
        System.out.println("Новое соединение: " + client.getRemoteAddress());
    }
    public static void handleRequest(SelectionKey key) throws IOException {
        // 1. Получаем канал клиента из ключа селектора
        SocketChannel client = (SocketChannel) key.channel();

        try {
            // 2. Создаём буфер для чтения данных от клиента
            ByteBuffer buffer = ByteBuffer.allocate(1024); // 1 КБ буфер

            // 3. Читаем данные из канала в буфер
            client.read(buffer);

            // 4. Переводим буфер в режим чтения (flip)
            buffer.flip();

            // 5. Загружаем HTML-файл с диска
            Path htmlPath = Paths.get("src/main/resources/index.html");
            String htmlContent = new String(Files.readAllBytes(htmlPath));

            // 6. Формируем HTTP-ответ
            String response = "HTTP/1.1 200 OK\r\n" +  // Статус-строка
                    "Content-Type: text/html\r\n" +  // Заголовок типа контента
                    "Content-Length: " + htmlContent.length() + "\r\n" +  // Размер HTML
                    "Connection: close\r\n\r\n" +  // Закрыть соединение после ответа
                    htmlContent;  // Тело ответа (наш HTML)

            // 7. Отправляем ответ клиенту
            client.write(ByteBuffer.wrap(response.getBytes()));

        } catch (Exception e) {
            // 8. Если произошла ошибка (например, файл не найден)
            String error = "HTTP/1.1 500 Internal Error\r\n\r\n";
            client.write(ByteBuffer.wrap(error.getBytes()));

        } finally {
            // 9. В любом случае закрываем соединение
            client.close();
        }
    }
}
