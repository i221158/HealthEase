package com.hospitalmanagement.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Patients")
public class Patient extends User {
    private String medicalHistory;

    // Getters and Setters
}
