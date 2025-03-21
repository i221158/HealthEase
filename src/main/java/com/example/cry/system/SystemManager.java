package com.example.cry.system;

import com.example.cry.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;
import java.util.List;

public class SystemManager {

    private final SessionFactory factory;

    public SystemManager() {
        factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Patient.class)
                .addAnnotatedClass(Doctor.class)
                .addAnnotatedClass(Admin.class)
                .addAnnotatedClass(Appointment.class)
                .addAnnotatedClass(Notification.class)
                .buildSessionFactory();
    }

    public void close() {
        factory.close();
    }

    private Session getSession() {
        return factory.getCurrentSession();
    }

    // 🔹 Register Patient
    public void registerPatient(String name, String email, String phone, String password, String history) {
        Session session = getSession();
        try {
            session.beginTransaction();

            Patient p = new Patient();
            p.setName(name);
            p.setEmail(email);
            p.setPhone(phone);
            p.setPasswordHash(password);
            p.setRole("PATIENT");
            p.setMedicalHistory(history);

            session.persist(p);

            session.getTransaction().commit();
            System.out.println("✅ Patient registered.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    // 🔹 Register Doctor
    public void registerDoctor(String name, String email, String phone, String password, String specialization, String license) {
        Session session = getSession();
        try {
            session.beginTransaction();

            Doctor d = new Doctor();
            d.setName(name);
            d.setEmail(email);
            d.setPhone(phone);
            d.setPasswordHash(password);
            d.setRole("DOCTOR");
            d.setSpecialization(specialization);
            d.setLicenseNumber(license);
            d.setAvailability(true);

            session.persist(d);

            session.getTransaction().commit();
            System.out.println("✅ Doctor registered.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    //Schedule Appointment
    public void scheduleAppointment(int patientId, int doctorId, LocalDateTime date) {
        Session session = getSession();
        try {
            session.beginTransaction();

            Patient patient = session.get(Patient.class, patientId);
            Doctor doctor = session.get(Doctor.class, doctorId);

            if (patient == null || doctor == null) {
                throw new RuntimeException("Patient or Doctor not found.");
            }

            Appointment appt = new Appointment();
            appt.setPatient(patient);
            appt.setDoctor(doctor);
            appt.setAppointmentDate(date);
            appt.setStatus("SCHEDULED");

            session.persist(appt);

            session.getTransaction().commit();
            System.out.println("📅 Appointment scheduled.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    //Send Notification
    public void sendNotification(int userId, String message) {
        Session session = getSession();
        try {
            session.beginTransaction();

            User user = session.get(User.class, userId);
            if (user == null) throw new RuntimeException("User not found.");

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notification.setTimestamp(LocalDateTime.now());

            session.persist(notification);

            session.getTransaction().commit();
            System.out.println("📢 Notification sent.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    //List All Appointments
    public void listAppointments() {
        Session session = getSession();
        try {
            session.beginTransaction();

            List<Appointment> appointments = session.createQuery("FROM Appointment", Appointment.class).getResultList();
            for (Appointment appt : appointments) {
                System.out.println("📆 Appt ID: " + appt.getAppointmentId()
                        + ", Patient: " + appt.getPatient().getName()
                        + ", Doctor: " + appt.getDoctor().getName()
                        + ", Date: " + appt.getAppointmentDate());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    //list all patients
    public void listPatients() {
        Session session = getSession();
        try {
            session.beginTransaction();
            List<Patient> patients = session.createQuery("FROM Patient", Patient.class).getResultList();
            for (Patient p : patients) {
                System.out.println("🧍 Patient ID: " + p.getUserId() + ", Name: " + p.getName());
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

}
