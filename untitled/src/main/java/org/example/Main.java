package org.example;

import com.hospitalmanagement.entities.Patient;
import com.hospitalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        // Open a Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // Create a new patient
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhone("123456789");
        patient.setPasswordHash("securepassword");

        // Save patient to the database
        session.save(patient);
        tx.commit();
        session.close();

        System.out.println("Database connection successful! Patient added.");
    }
}
