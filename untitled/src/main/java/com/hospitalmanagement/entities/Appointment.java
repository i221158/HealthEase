package com.hospitalmanagement.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentID;

    @ManyToOne
    @JoinColumn(name = "patientID")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctorID")
    private Doctor doctor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status { SCHEDULED, CANCELED }

    // Getters and Setters
}
