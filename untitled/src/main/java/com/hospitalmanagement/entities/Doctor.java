package com.hospitalmanagement.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Doctors")
public class Doctor extends User {
    private String specialization;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private boolean availability;

    // Getters and Setters
}
