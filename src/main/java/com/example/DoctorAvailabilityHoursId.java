package com.example;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class DoctorAvailabilityHoursId implements Serializable {

    private int doctorID;
    private LocalDateTime appointmentDate;

    // Default constructor
    public DoctorAvailabilityHoursId() {}

    // Constructor with fields
    public DoctorAvailabilityHoursId(int doctorID, LocalDateTime appointmentDate) {
        this.doctorID = doctorID;
        this.appointmentDate = appointmentDate;
    }

    // Getters and setters
    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    // Override equals and hashCode for proper comparison of composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoctorAvailabilityHoursId that = (DoctorAvailabilityHoursId) o;
        return doctorID == that.doctorID && Objects.equals(appointmentDate, that.appointmentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorID, appointmentDate);
    }
}
