package com.hospitalmanagement.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();
}
