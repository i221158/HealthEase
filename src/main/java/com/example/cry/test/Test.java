package com.example.cry.test;
import com.example.cry.model.*;
import com.example.cry.system.SystemManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.List;
import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {
        try {
            System.setProperty("java.library.path", "C:\\Users\\sawab\\.jdks\\openjdk-24\\bin");

            try {
                System.load("C:\\Users\\sawab\\.jdks\\openjdk-24\\bin\\mssql-jdbc_auth-12.4.2.x64.dll");
                System.out.println("✅ DLL loaded manually.");
            } catch (UnsatisfiedLinkError e) {
                System.out.println("❌ Failed to load DLL manually: " + e.getMessage());
                e.printStackTrace();
            }

            // Load SQL Server Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ SQL Server Driver Loaded Successfully!");

            // Load Hibernate configuration
//            SessionFactory factory = new Configuration()
//                    .configure("hibernate.cfg.xml")
//                    .addAnnotatedClass(User.class)
//                    .addAnnotatedClass(Patient.class)
//                    .addAnnotatedClass(Doctor.class)
//                    .addAnnotatedClass(Appointment.class)
//                    .addAnnotatedClass(Notification.class)
//                    .buildSessionFactory();

            SystemManager sys = new SystemManager();

            // Test: Add a new patient
            sys.registerPatient("Test Patient", "test@patient.com", "0987654321", "hashed123", "No known allergies.");

            // Test: Add a new doctor
            sys.registerDoctor("Dr. Zain", "zain@clinic.com", "0987654321", "docpass123", "Neurology", "D1009");

            sys.sendNotification(2, "Your appointment is tomorrow at 9 AM.");
            sys.scheduleAppointment(2, 7, LocalDateTime.now().plusDays(1));
            sys.listAppointments();
            sys.close();
//            factory.close();
        } catch (Exception e) {
            System.err.println("❌ Hibernate connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
