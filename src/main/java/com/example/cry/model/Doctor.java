package com.example.cry.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Doctors")
@PrimaryKeyJoinColumn(name = "doctorID") // FK to Users(userID)
public class Doctor extends User {

    private String specialization;

    private String licenseNumber;

    private boolean availability;

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }
}
