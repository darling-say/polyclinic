package com.polyclinic.registry;

public class Person {
    private String name;
    private String surname;
    private String secondName;

    // Конструкторы
    public Person() {}

    public Person(String name, String surname, String secondName) {
        this.name = name;
        this.surname = surname;
        this.secondName = secondName;
    }

    // Геттеры
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getSecondName() { return secondName; }

    // Сеттеры
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public String getFullName() {
        String fullName = surname + " " + name;
        if (secondName != null && !secondName.trim().isEmpty()) {
            fullName += " " + secondName;
        }
        return fullName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}