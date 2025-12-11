package com.polyclinic.registry;

import java.time.LocalTime;

public class ScheduleValidator {

    public static InputValidator.ValidationResult validateSchedule(int roomNumber, LocalTime startTime, LocalTime endTime) {
        // Проверка номера кабинета
        if (roomNumber <= 0) {
            return new InputValidator.ValidationResult(false, "Номер кабинета должен быть положительным числом");
        }

        if (roomNumber > 999) {
            return new InputValidator.ValidationResult(false, "Номер кабинета слишком большой");
        }

        // Проверка времени
        if (startTime == null) {
            return new InputValidator.ValidationResult(false, "Укажите время начала");
        }

        if (endTime == null) {
            return new InputValidator.ValidationResult(false, "Укажите время окончания");
        }

        // Минимальная продолжительность приема - 15 минут
        if (endTime.isBefore(startTime.plusMinutes(15))) {
            return new InputValidator.ValidationResult(false, "Продолжительность приема должна быть не менее 15 минут");
        }

        // Максимальная продолжительность - 8 часов
        if (endTime.isAfter(startTime.plusHours(8))) {
            return new InputValidator.ValidationResult(false, "Продолжительность приема не может превышать 8 часов");
        }

        // Рабочие часы поликлиники: 8:00 - 20:00
        LocalTime clinicOpen = LocalTime.of(8, 0);
        LocalTime clinicClose = LocalTime.of(20, 0);

        if (startTime.isBefore(clinicOpen)) {
            return new InputValidator.ValidationResult(false, "Прием не может начинаться раньше 8:00");
        }

        if (endTime.isAfter(clinicClose)) {
            return new InputValidator.ValidationResult(false, "Прием не может заканчиваться позже 20:00");
        }

        return new InputValidator.ValidationResult(true, "OK");
    }
}