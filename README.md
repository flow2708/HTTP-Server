# Java NIO HTTP Server

[![Java Version](https://img.shields.io/badge/Java-21%2B-blue)](https://openjdk.org/)
[![Gradle Build](https://img.shields.io/badge/Gradle-8.5%2B-green)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue)](LICENSE)

Легковесный HTTP-сервер на чистом Java NIO для учебных целей. Демонстрирует принципы асинхронной обработки запросов.

## 📌 Особенности

- Полностью на Java NIO API (java.nio)
- Поддержка статических HTML/CSS/JS файлов
- Неблокирующий ввод/вывод через Selector
- Минимальная конфигурация
- Сборка через Gradle
- Лицензия GPL-3.0

## 🚀 Запуск сервера

### Требования
- **JDK 21** (LTS версия)
- **Gradle 8.5+**

### Стандартный запуск
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/flow2708/HTTP-Server.git
   cd HTTP-Server
2. Запустите сервер через Gradle:
   ```bash
   ./gradlew run
Сервер автоматически запустится на:
   ```bash
   http://localhost:8080
