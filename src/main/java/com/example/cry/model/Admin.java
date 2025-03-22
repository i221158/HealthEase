package com.example.cry.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Admins")
@PrimaryKeyJoinColumn(name = "adminID")
public class Admin extends User {
    private String permissions;

    // Getters and Setters
    public Admin() {
        super();
    }
    public String getPermissions() {
        return permissions;
    }
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

}
