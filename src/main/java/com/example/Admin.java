package com.example;

import jakarta.persistence.*;

@Entity
@Table(name = "Admins")
@PrimaryKeyJoinColumn(name = "adminID")
public class Admin extends User {
    //private String permissions;

    // Getters and Setters
    /*public Admin() {
        super();
    }*/

}
