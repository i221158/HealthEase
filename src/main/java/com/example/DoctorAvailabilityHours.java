package com.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DoctorAvailabilityHours")
public class DoctorAvailabilityHours {

    @EmbeddedId
    private DoctorAvailabilityHoursId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorID", referencedColumnName = "doctorID", insertable = false, updatable = false)
    private Doctor doctor;

    @Column(name = "status")
    private boolean status = true;

    // Getter for appointmentDate (use id.getAppointmentDate())
    public LocalDateTime getAppointmentDate() {
        return id.getAppointmentDate();
    }

    // Getter and setter for status
    public boolean getStatus() {
        return status;
    }
    public DoctorAvailabilityHoursId getId() {
        return id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    // Optional: Override toString() if needed for debugging
    @Override
    public String toString() {
        return "DoctorAvailabilityHours{" +
                "appointmentDate=" + getAppointmentDate() +
                ", status=" + status +
                '}';
    }
}
