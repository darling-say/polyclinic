package com.polyclinic.registry;

import java.time.LocalDate;

public class Patient extends Person {
    private int policy;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;

    // Конструкторы
    public Patient() {}

    public Patient(String name, String surname, String secondName, int policy, String phone, String address, LocalDate dateOfBirth) {
        super(name, surname, (secondName != null && !secondName.trim().isEmpty()) ? secondName : "");
        this.policy = policy;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    // Альтернативный конструктор без отчества
    public Patient(String name, String surname, int policy, String phone, String address, LocalDate dateOfBirth) {
        super(name, surname, "");
        this.policy = policy;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    // Геттеры и сеттеры
    public int getPolicy() { return policy; }
    public void setPolicy(int policy) { this.policy = policy; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @Override
    public String toString() {
        String fullName = getSurname() + " " + getName();
        if (getSecondName() != null && !getSecondName().isEmpty()) {
            fullName += " " + getSecondName();
        }

        String info = fullName + " (Полис: " + String.format("%06d", policy);

        if (phone != null && !phone.isEmpty()) {
            info += ", Телефон: " + phone;
        }

        if (address != null && !address.isEmpty()) {
            info += ", Адрес: " + address;
        }

        return info + ")";
    }

    // Дополнительный метод для красивого отображения
    public String getFormattedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSurname()).append(" ").append(getName());

        if (getSecondName() != null && !getSecondName().isEmpty()) {
            sb.append(" ").append(getSecondName());
        }

        sb.append("\nПолис: ").append(String.format("%06d", policy));
        sb.append(" | Тел: ").append(phone != null ? phone : "не указан");

        if (address != null && !address.isEmpty()) {
            sb.append("\nАдрес: ").append(address);
        }

        if (dateOfBirth != null) {
            sb.append(" | Рожд: ").append(dateOfBirth);
        }

        return sb.toString();
    }
}