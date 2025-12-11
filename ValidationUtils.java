package com.polyclinic.registry;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static javax.swing.UIManager.put;


public class ValidationUtils {

    // Фильтр для запрета цифр (для ФИО)
    public static TextFormatter<String> createNameFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[а-яА-ЯёЁa-zA-Z\\s-]*")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }

    // Фильтр для цифр (для номеров)
    public static TextFormatter<String> createDigitsFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }

    // Фильтр для телефона
    public static TextFormatter<String> createPhoneFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[+\\d]*")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }

    // НОВЫЙ ФИЛЬТЕР ДЛЯ УЛИЦЫ (только буквы и дефис)
    public static TextFormatter<String> createStreetFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Разрешаем: буквы, пробелы, дефис, точку (для "ул.", "пр.")
            // Дефис не может быть первым или последним
            if (newText.isEmpty() || newText.matches("^[а-яА-ЯёЁa-zA-Z][а-яА-ЯёЁa-zA-Z\\s.-]*$")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }

    // НОВЫЙ ФИЛЬТЕР ДЛЯ КВАРТИРЫ (только цифры)
    public static TextFormatter<String> createApartmentFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Только цифры, максимум 4 цифры (обычно квартиры до 9999)
            if (newText.isEmpty() || (newText.matches("\\d*") && newText.length() <= 4)) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filter);
    }

    // ВАЛИДАЦИЯ УЛИЦЫ
    public static InputValidator.ValidationResult validateStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            return new InputValidator.ValidationResult(false, "Улица не может быть пустой");
        }

        String trimmed = street.trim();

        // Проверка длины
        if (trimmed.length() < 2) {
            return new InputValidator.ValidationResult(false, "Название улицы слишком короткое");
        }

        if (trimmed.length() > 50) {
            return new InputValidator.ValidationResult(false, "Название улицы слишком длинное");
        }

        // Проверка на недопустимые символы
        if (!trimmed.matches("^[а-яА-ЯёЁa-zA-Z][а-яА-ЯёЁa-zA-Z\\s.-]*$")) {
            return new InputValidator.ValidationResult(false,
                    "Улица может содержать только буквы, пробелы, дефис и точку");
        }

        // Проверка дефиса (не может быть первым или последним)
        if (trimmed.startsWith("-") || trimmed.endsWith("-")) {
            return new InputValidator.ValidationResult(false,
                    "Дефис не может быть в начале или конце названия улицы");
        }

        // Проверка двух дефисов подряд
        if (trimmed.contains("--")) {
            return new InputValidator.ValidationResult(false,
                    "Не может быть два дефиса подряд");
        }

        // Проверка, что название начинается с буквы
        if (!Character.isLetter(trimmed.charAt(0))) {
            return new InputValidator.ValidationResult(false,
                    "Название улицы должно начинаться с буквы");
        }

        return new InputValidator.ValidationResult(true, "OK");
    }

    // ВАЛИДАЦИЯ ДОМА
    public static InputValidator.ValidationResult validateHouse(String house) {
        if (house == null || house.trim().isEmpty()) {
            return new InputValidator.ValidationResult(false, "Номер дома не может быть пустым");
        }

        String trimmed = house.trim();

        // Проверка на только цифры и буквы (для домов типа "15А")
        if (!trimmed.matches("^\\d+[а-яА-Яa-zA-Z]?$")) {
            return new InputValidator.ValidationResult(false,
                    "Номер дома должен содержать цифры и одну букву (например: 15А)");
        }

        // Проверка длины
        if (trimmed.length() > 10) {
            return new InputValidator.ValidationResult(false,
                    "Номер дома слишком длинный");
        }

        return new InputValidator.ValidationResult(true, "OK");
    }

    // ВАЛИДАЦИЯ КВАРТИРЫ
    public static InputValidator.ValidationResult validateApartment(String apartment) {
        // Квартира может быть пустой
        if (apartment == null || apartment.trim().isEmpty()) {
            return new InputValidator.ValidationResult(true, "OK");
        }

        String trimmed = apartment.trim();

        // Проверка на только цифры
        if (!trimmed.matches("^\\d+$")) {
            return new InputValidator.ValidationResult(false,
                    "Номер квартиры должен содержать только цифры");
        }

        // Проверка длины (обычно до 4 цифр)
        if (trimmed.length() > 4) {
            return new InputValidator.ValidationResult(false,
                    "Номер квартиры слишком длинный (макс. 4 цифры)");
        }

        // Проверка на разумный номер квартиры
        try {
            int aptNum = Integer.parseInt(trimmed);
            if (aptNum <= 0) {
                return new InputValidator.ValidationResult(false,
                        "Номер квартиры должен быть положительным числом");
            }
            if (aptNum > 9999) {
                return new InputValidator.ValidationResult(false,
                        "Номер квартиры не может быть больше 9999");
            }
        } catch (NumberFormatException e) {
            return new InputValidator.ValidationResult(false,
                    "Номер квартиры должен быть числом");
        }

        return new InputValidator.ValidationResult(true, "OK");
    }

    // Подсветка невалидных полей
    public static void highlightInvalidField(TextField field, boolean isValid) {
        if (isValid) {
            field.setStyle("-fx-border-color: green; -fx-border-width: 1px;");
        } else {
            field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
    }

    // Автоматическое приведение к правильному формату
    public static String autoFormatStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            return street;
        }

        String trimmed = street.trim();

        // Убираем лишние пробелы
        trimmed = trimmed.replaceAll("\\s+", " ");

        // Пробуем определить тип улицы
        String formatted = detectStreetType(trimmed);

        if (formatted != null) {
            // Если тип определен, возвращаем отформатированный
            return formatStreetName(formatted);
        }

        // Если тип не определен, добавляем "ул." по умолчанию
        return "ул. " + formatStreetName(trimmed);
    }
    private static String formatStreetName(String streetName) {
        if (streetName == null || streetName.trim().isEmpty()) {
            return streetName;
        }

        String trimmed = streetName.trim();
        String[] words = trimmed.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                // Для сокращений типа "ул.", "пр.", "наб." оставляем как есть
                if (word.matches("(?i)ул\\.|пр\\.|пер\\.|б-р|ал\\.|пл\\.|мкр\\.|наб\\.|ш\\.")) {
                    result.append(word.toLowerCase());
                } else {
                    // Первая буква заглавная, остальные строчные
                    word = word.substring(0, 1).toUpperCase() +
                            (word.length() > 1 ? word.substring(1).toLowerCase() : "");
                    result.append(word);
                }

                if (i < words.length - 1) {
                    result.append(" ");
                }
            }
        }

        return result.toString();
    }
    // Метод для валидации улицы с учетом типа
    public static InputValidator.ValidationResult validateStreetWithType(String street) {
        if (street == null || street.trim().isEmpty()) {
            return new InputValidator.ValidationResult(false, "Улица не может быть пустой");
        }

        String trimmed = street.trim();

        // Проверка длины
        if (trimmed.length() < 2) {
            return new InputValidator.ValidationResult(false, "Название улицы слишком короткое");
        }

        if (trimmed.length() > 50) {
            return new InputValidator.ValidationResult(false, "Название улица слишком длинное");
        }

        // Разрешенные символы: буквы, пробелы, дефис, точка, тире
        if (!trimmed.matches("^[а-яА-ЯёЁa-zA-Z][а-яА-ЯёЁa-zA-Z\\s.\\-]*$")) {
            return new InputValidator.ValidationResult(false,
                    "Улица может содержать только буквы, пробелы, дефис и точку");
        }

        // Проверка дефиса
        if (trimmed.startsWith("-") || trimmed.endsWith("-")) {
            return new InputValidator.ValidationResult(false,
                    "Дефис не может быть в начале или конце названия улицы");
        }

        // Проверка двух дефисов подряд
        if (trimmed.contains("--")) {
            return new InputValidator.ValidationResult(false,
                    "Не может быть два дефиса подряд");
        }

        return new InputValidator.ValidationResult(true, "OK");
    }


    // Список типов улиц и их сокращений (обновите существующую карту)
    private static final Map<String, String> STREET_TYPES = new HashMap<String, String>() {{
        put("улица", "ул.");
        put("ул", "ул.");
        put("проспект", "пр.");
        put("пр", "пр.");
        put("бульвар", "б-р");
        put("б-р", "б-р");
        put("бульв", "б-р");
        put("аллея", "ал.");
        put("ал", "ал.");
        put("переулок", "пер.");
        put("пер", "пер.");
        put("проезд", "пр-д");
        put("шоссе", "ш.");
        put("ш", "ш.");
        put("площадь", "пл.");
        put("пл", "пл.");
        put("микрорайон", "мкр.");
        put("мкр", "мкр.");
        put("набережная", "наб.");
        put("наб", "наб.");
        put("набер", "наб.");
    }};
    // Определение типа улицы по названию
    private static String detectStreetType(String streetName) {
        if (streetName == null || streetName.trim().isEmpty()) {
            return "ул.";
        }

        String lowerName = streetName.toLowerCase().trim();

        // Сначала проверяем, есть ли уже префикс в названии
        for (Map.Entry<String, String> entry : STREET_TYPES.entrySet()) {
            String type = entry.getKey();
            String abbr = entry.getValue();

            // Если название начинается с типа улицы (например, "проспект Ленина")
            if (lowerName.startsWith(type + " ")) {
                return abbr + " " + streetName.substring(type.length()).trim();
            }

            // Если название содержит тип в начале с точкой (например, "пр. Мира")
            if (lowerName.startsWith(abbr + " ")) {
                return streetName; // Уже отформатировано
            }
        }

        // Если не нашли тип улицы, считаем что это просто улица
        return null;
    }


}