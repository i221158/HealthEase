package com.example.cry.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Patients")
@PrimaryKeyJoinColumn(name = "patientID") // FK to Users(userID)
public class Patient extends User {

    private String medicalHistory;

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
}
