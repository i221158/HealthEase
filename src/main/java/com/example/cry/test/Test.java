package com.example.cry.test;
import com.example.cry.model.Patient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.List;

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
            SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            Session session = factory.openSession();
            session.beginTransaction();

            // Run SQL Query
            //Object result = session.createNativeQuery("SELECT COUNT(*) FROM Patients").getSingleResult();
            //System.out.println("🩺 Total Patients: " + result);
            //System.out.println("✅ Query executed successfully, result: " + result);
            // ✅ HQL to get all patients
            List<Patient> patients = session.createQuery("FROM Patient", Patient.class).getResultList();
            for (Patient p : patients) {
                System.out.println("🧍 " + p.getUserId() + " - " + p.getName() + " - History: " + p.getMedicalHistory());
            }


            session.getTransaction().commit();
            session.close();
            factory.close();
        } catch (Exception e) {
            System.err.println("❌ Hibernate connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
