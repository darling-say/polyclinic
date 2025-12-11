package com.polyclinic.registry;

public class AddressValidator {

    public static class AddressValidationResult {
        private final boolean valid;
        private final String message;
        private final String standardizedAddress;

        public AddressValidationResult(boolean valid, String message, String standardizedAddress) {
            this.valid = valid;
            this.message = message;
            this.standardizedAddress = standardizedAddress;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getStandardizedAddress() { return standardizedAddress; }
    }

    // Основной метод валидации
    public static AddressValidationResult validateAndStandardize(String address) {
        if (address == null || address.trim().isEmpty()) {
            return new AddressValidationResult(false, "Адрес не может быть пустым", address);
        }

        String trimmed = address.trim();

        // 1. Проверка длины
        if (trimmed.length() < 5) {
            return new AddressValidationResult(false, "Адрес слишком короткий (минимум 5 символов)", trimmed);
        }

        if (trimmed.length() > 200) {
            return new AddressValidationResult(false, "Адрес слишком длинный (максимум 200 символов)", trimmed);
        }

        // 2. Должен содержать буквы
        if (!trimmed.matches(".*[а-яА-ЯёЁa-zA-Z].*")) {
            return new AddressValidationResult(false, "Адрес должен содержать буквы", trimmed);
        }

        // 3. Проверка на недопустимые символы
        if (trimmed.matches(".*[<>{}|~`@#$%^&*_=+].*")) {
            return new AddressValidationResult(false,
                    "Адрес содержит недопустимые символы: < > { } | ~ ` @ # $ % ^ & * _ = +", trimmed);
        }

        // 4. Стандартизация адреса
        String standardized = standardizeAddress(trimmed);

        return new AddressValidationResult(true, "Адрес корректен", standardized);
    }

    // Метод для стандартизации адреса
    private static String standardizeAddress(String address) {
        String result = address;

        // Убираем лишние пробелы
        result = result.replaceAll("\\s+", " ");

        // Стандартизируем сокращения
        result = result.replaceAll("(?i)ул\\.", "ул.");
        result = result.replaceAll("(?i)улица", "ул.");
        result = result.replaceAll("(?i)пр\\.", "пр.");
        result = result.replaceAll("(?i)проспект", "пр.");
        result = result.replaceAll("(?i)д\\.", "д.");
        result = result.replaceAll("(?i)дом", "д.");
        result = result.replaceAll("(?i)кв\\.", "кв.");
        result = result.replaceAll("(?i)квартира", "кв.");
        result = result.replaceAll("(?i)корп\\.", "корп.");
        result = result.replaceAll("(?i)корпус", "корп.");
        result = result.replaceAll("(?i)стр\\.", "стр.");
        result = result.replaceAll("(?i)строение", "стр.");

        // Первая буква каждого слова - заглавная
        String[] words = result.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                // Для номеров домов/квартир не меняем регистр
                if (word.matches("\\d+[а-яА-Я]?") ||
                        word.matches("д\\.\\d+") ||
                        word.matches("кв\\.\\d+")) {
                    sb.append(word);
                } else {
                    // Первая буква заглавная, остальные строчные
                    String firstLetter = word.substring(0, 1).toUpperCase();
                    String rest = word.length() > 1 ? word.substring(1).toLowerCase() : "";
                    sb.append(firstLetter).append(rest);
                }

                if (i < words.length - 1) {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }

    // Примеры правильных адресов для тестирования
    public static String[] getValidAddressExamples() {
        return new String[] {
                "ул. Ленина, д. 10, кв. 5",
                "пр. Мира, 25, корп. 2",
                "г. Москва, ул. Пушкина, д. 15А",
                "Санкт-Петербург, Невский пр., 50",
                "д. 7, ул. Центральная"
        };
    }

    // Проверка формата "улица, дом, квартира"
    public static boolean hasCompleteAddress(String address) {
        if (address == null) return false;

        String lower = address.toLowerCase();
        boolean hasStreet = lower.matches(".*ул\\.|улица.*");
        boolean hasHouse = lower.matches(".*д\\.\\s*\\d+|дом\\s*\\d+.*");
        boolean hasApartment = lower.matches(".*кв\\.\\s*\\d+|квартира\\s*\\d+.*");

        // Не обязательно иметь все три компонента
        // Но желательно иметь хотя бы улицу и дом
        return hasStreet && hasHouse;
    }
}