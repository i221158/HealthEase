package com.example.cry.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Admins") // Optional: You can use Users table only if you want
@PrimaryKeyJoinColumn(name = "adminID")
public class Admin extends User {
    // You can add admin-specific fields here if needed

    // Getters and Setters
}
