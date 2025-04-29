package com.example;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Feedback")
public class Feedback {
    //njgjg
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackID;

    @ManyToOne
    @JoinColumn(name = "patientID")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctorID")
    private Doctor doctor;

    private int rating;

    private String comments;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Getters and Setters

    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}


