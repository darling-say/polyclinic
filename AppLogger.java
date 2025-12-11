package com.polyclinic.registry;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    private static final String LOG_FILE = "polyclinic_log.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static PrintWriter writer;

    static {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true)); // true = append mode
        } catch (IOException e) {
            System.err.println("Ошибка создания логгера: " + e.getMessage());
        }
    }

    // Уровни логирования
    public static void info(String message) {
        log("INFO", message);
    }

    public static void warning(String message) {
        log("WARNING", message);
    }

    public static void error(String message, Exception e) {
        log("ERROR", message + (e != null ? " | Exception: " + e.getMessage() : ""));
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static void error(String message) {
        error(message, null);
    }

    public static void debug(String message) {
        log("DEBUG", message);
    }

    public static void audit(String action, String user, String details) {
        log("AUDIT", user + " выполнил: " + action + " | " + details);
    }

    // Основной метод логирования
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Вывод в консоль
        System.out.println(logMessage);

        // Запись в файл
        if (writer != null) {
            writer.println(logMessage);
            writer.flush();
        }
    }

    // Закрытие логгера (вызывать при завершении приложения)
    public static void close() {
        if (writer != null) {
            writer.close();
        }
    }
}