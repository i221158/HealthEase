package com.hospitalmanagement.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MedicalRecords")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordID;

    @ManyToOne
    @JoinColumn(name = "patientID")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctorID")
    private Doctor doctor;

    private String diagnosis;
    private String prescription;

    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate = new Date();

    // Getters and Setters
}
