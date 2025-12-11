package com.polyclinic.registry;

//Пользовательский класс исключений для поликлиники

public class PolyclinicException extends Exception {

    // Коды ошибок
    public enum ErrorCode {
        DATABASE_ERROR("Ошибка базы данных"),
        PATIENT_CONFLICT("У пациента уже есть запись на это время"),
        VALIDATION_ERROR("Ошибка валидации данных"),
        XML_ERROR("Ошибка работы с XML"),
        PDF_ERROR("Ошибка генерации PDF"),
        EXCEL_ERROR("Ошибка работы с Excel"),
        SCHEDULE_CONFLICT("Конфликт расписания"),
        APPOINTMENT_CONFLICT("Конфликт записи на приём"),
        DOCTOR_NOT_FOUND("Врач не найден"),
        PATIENT_NOT_FOUND("Пациент не найден"),
        INVALID_DATA("Некорректные данные"),
        FILE_ERROR("Ошибка работы с файлом"),
        UNKNOWN_ERROR("Неизвестная ошибка");


        private final String description;

        ErrorCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ErrorCode errorCode;
    private final String additionalInfo;

    // Конструкторы
    public PolyclinicException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.additionalInfo = "";
    }

    public PolyclinicException(ErrorCode errorCode, String message) {
        super(errorCode.getDescription() + ": " + message);
        this.errorCode = errorCode;
        this.additionalInfo = message;
    }

    public PolyclinicException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getDescription() + ": " + message, cause);
        this.errorCode = errorCode;
        this.additionalInfo = message;
    }

    // Геттеры
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public String toString() {
        return "PolyclinicException{" +
                "errorCode=" + errorCode +
                ", message='" + getMessage() + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}