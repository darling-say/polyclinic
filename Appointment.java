package com.polyclinic.registry;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int doctorId;
    private int patientPolicy;
    private LocalDateTime appointmentDateTime;
    private String notes;
    private boolean completed;
    private String status; // "запланировано", "выполнено", "отменено"

    // Конструкторы
    public Appointment() {}

    public Appointment(int doctorId, int patientPolicy, LocalDateTime appointmentDateTime) {
        this.doctorId = doctorId;
        this.patientPolicy = patientPolicy;
        this.appointmentDateTime = appointmentDateTime;
        this.completed = false;
        this.status = "запланировано";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public int getPatientPolicy() { return patientPolicy; }
    public void setPatientPolicy(int patientPolicy) { this.patientPolicy = patientPolicy; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.status = completed ? "выполнено" : "запланировано";
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Запись #%d на %s (Статус: %s)",
                id, appointmentDateTime.toString(), status);
    }
}