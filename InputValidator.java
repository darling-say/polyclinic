package com.polyclinic.registry;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class InputValidator {

    // Разрешаем буквы и ОДИН дефис внутри (не в начале и не в конце)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[а-яА-ЯёЁa-zA-Z]+(-[а-яА-ЯёЁa-zA-Z]+)?$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[а-яА-ЯёЁa-zA-Z0-9\\s.,-]{5,100}$");
    private static final Pattern SPECIALTY_PATTERN = Pattern.compile("^[а-яА-ЯёЁa-zA-Z\\s-]{3,50}$");
    // Валидация ФИО
    public static ValidationResult validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            // РАЗДЕЛЬНАЯ ЛОГИКА ДЛЯ ЖЕНСКОГО И МУЖСКОГО РОДА
            if (fieldName.equals("Фамилия")) {
                return new ValidationResult(false, "Фамилия не может быть пустой");
            } else if (fieldName.equals("Имя")) {
                return new ValidationResult(false, "Имя не может быть пустым");
            } else {
                return new ValidationResult(false, fieldName + " не может быть пустым");
            }
        }

        String trimmedName = name.trim();

        // 1. Запрещаем начинать с дефиса
        if (trimmedName.startsWith("-")) {
            return new ValidationResult(false, fieldName + " не может начинаться с дефиса");
        }

        // 2. Запрещаем заканчиваться дефисом
        if (trimmedName.endsWith("-")) {
            return new ValidationResult(false, fieldName + " не может заканчиваться дефисом");
        }

        // 3. Запрещаем только дефисы (например, "---", "--")
        if (trimmedName.matches("^-+$")) {
            return new ValidationResult(false, fieldName + " не может состоять только из дефисов");
        }

        // 4. Запрещаем два дефиса подряд
        if (trimmedName.contains("--")) {
            return new ValidationResult(false, fieldName + " не может содержать два дефиса подряд");
        }

        // 5. Основная проверка паттерна
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            if (fieldName.equals("Фамилия")) {
                return new ValidationResult(false, "Фамилия должна содержать только буквы и один дефис (2-50 символов)");
            } else if (fieldName.equals("Имя")) {
                return new ValidationResult(false, "Имя должно содержать только буквы и один дефис (2-50 символов)");
            } else {
                return new ValidationResult(false, fieldName + " должно содержать только буквы и один дефис (2-50 символов)");
            }
        }

        return new ValidationResult(true, "OK");
    }

    // Валидация специализации
    public static ValidationResult validateSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            return new ValidationResult(false, "Специализация не может быть пустой");
        }

        if (!SPECIALTY_PATTERN.matcher(specialty).matches()) {
            return new ValidationResult(false, "Специализация должна содержать 3-50 буквенных символов");
        }

        return new ValidationResult(true, "OK");
    }

    // Валидация телефона
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(false, "Телефон не может быть пустым");
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return new ValidationResult(false, "Телефон должен быть в формате +79991234567");
        }

        return new ValidationResult(true, "OK");
    }

    // Валидация адреса
    public static ValidationResult validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return new ValidationResult(false, "Адрес не может быть пустым");
        }

        if (address.trim().length() < 5) {
            return new ValidationResult(false, "Адрес должен содержать минимум 5 символов");
        }

        // Можно добавить дополнительные проверки
        if (address.length() > 200) {
            return new ValidationResult(false, "Адрес слишком длинный (макс. 200 символов)");
        }

        return new ValidationResult(true, "OK");
    }
    // Валидация номера полиса
    public static ValidationResult validatePolicyNumber(String policyStr) {
        if (policyStr == null || policyStr.trim().isEmpty()) {
            return new ValidationResult(false, "Номер полиса не может быть пустым");
        }

        try {
            int policy = Integer.parseInt(policyStr);
            if (policy <= 0) {
                return new ValidationResult(false, "Номер полиса должен быть положительным числом");
            }
            if (policyStr.length() != 6) {
                return new ValidationResult(false, "Номер полиса должен содержать 6 цифр");
            }
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Номер полиса должен содержать только цифры");
        }

        return new ValidationResult(true, "OK");
    }

    // Класс для возврата результата валидации
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    // Валидация отчества (необязательное поле)
    public static ValidationResult validatePatronymic(String patronymic) {
        if (patronymic == null || patronymic.trim().isEmpty()) {
            return new ValidationResult(true, "OK"); // Пустое отчество разрешено
        }

        String trimmedPatronymic = patronymic.trim();

        // 1. Запрещаем начинать с дефиса
        if (trimmedPatronymic.startsWith("-")) {
            return new ValidationResult(false, "Отчество не может начинаться с дефиса");
        }

        // 2. Запрещаем заканчиваться дефисом
        if (trimmedPatronymic.endsWith("-")) {
            return new ValidationResult(false, "Отчество не может заканчиваться дефисом");
        }

        // 3. Запрещаем только дефисы
        if (trimmedPatronymic.matches("^-+$")) {
            return new ValidationResult(false, "Отчество не может состоять только из дефисов");
        }

        // 4. Запрещаем два дефиса подряд
        if (trimmedPatronymic.contains("--")) {
            return new ValidationResult(false, "Отчество не может содержать два дефиса подряд");
        }

        // 5. Основная проверка паттерна
        if (!NAME_PATTERN.matcher(trimmedPatronymic).matches()) {
            return new ValidationResult(false, "Отчество должно содержать только буквы и один дефис (2-50 символов) или быть пустым");
        }

        return new ValidationResult(true, "OK");
    }
}