package com.polyclinic.registry;

import java.time.LocalDate;

public class MedicalCertificate {
    private int id;
    private int patientPolicy;
    private LocalDate issueDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String diagnosis;
    private String recommendations;
    private int doctorId;
    private String status; // "active", "closed"

    // Конструкторы
    public MedicalCertificate() {}

    public MedicalCertificate(int patientPolicy, LocalDate issueDate, LocalDate startDate,
                              LocalDate endDate, String diagnosis, String recommendations,
                              int doctorId, String status) {
        this.patientPolicy = patientPolicy;
        this.issueDate = issueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.diagnosis = diagnosis;
        this.recommendations = recommendations;
        this.doctorId = doctorId;
        this.status = status;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientPolicy() { return patientPolicy; }
    public void setPatientPolicy(int patientPolicy) { this.patientPolicy = patientPolicy; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "MedicalCertificate{" +
                "id=" + id +
                ", patientPolicy=" + patientPolicy +
                ", issueDate=" + issueDate +
                ", diagnosis='" + diagnosis + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}