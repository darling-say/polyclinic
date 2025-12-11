package com.polyclinic.registry;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/polyclinic";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "pr1vetPoka!";

    private Connection connection;

    // Добавьте статический экземпляр для синглтона
    private static DatabaseManager instance;

    public DatabaseManager() {
        connect();
    }

    // Добавьте метод для получения статического экземпляра
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void connect() {
        try {
            String fullUrl = URL + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
            connection = DriverManager.getConnection(fullUrl, USERNAME, PASSWORD);
            connection.setAutoCommit(true);
            System.out.println("Подключение к базе данных установлено!");
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
            System.out.println("Проверь:");
            System.out.println("   - Запущен ли MySQL сервер");
            System.out.println("   - Правильный ли пароль в DatabaseManager.java");
            System.out.println("   - Существует ли база данных 'polyclinic'");
        }
    }

    public void reconnect() {
        closeConnection();
        connect();
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Подключение разорвано, переподключаемся...");
                connect();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка проверки подключения: " + e.getMessage());
            connect();
        }
        return connection;
    }

    // Добавьте статический метод для получения соединения
    public static Connection getConnectionStatic() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance.getConnection();
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Подключение к базе данных закрыто");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при закрытии подключения: " + e.getMessage());
        }
    }

    public void testConnection() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 1");
            if (resultSet.next()) {
                System.out.println("Тест подключения к БД пройден успешно!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка теста подключения: " + e.getMessage());
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        return statement.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        return statement.executeUpdate(sql);
    }
}