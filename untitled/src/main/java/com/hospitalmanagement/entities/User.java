package com.hospitalmanagement.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)  // Allows Patient and Doctor to extend User
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role { PATIENT, DOCTOR, ADMIN }

    // Getters and Setters
}
