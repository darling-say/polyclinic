package com.polyclinic.registry;

public class Doctor extends Person {
    private int id;
    private String specialty;

    // Конструкторы
    public Doctor() {}

    public Doctor(String name, String surname, String secondName, int id, String specialty) {
        super(name, surname, secondName);
        this.id = id;
        this.specialty = specialty;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String toString() {
        return getFullName() + " - " + specialty + " (ID: " + id + ")";
    }
}