package com.example.cry.system;

import com.example.cry.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.Arrays;

public class SystemManager {

    private final SessionFactory factory;
    public void storePatient(Patient patient) {
        Session session = getSession();
        session.beginTransaction();
        session.persist(patient);
        session.getTransaction().commit();
        System.out.println("Patient stored successfully.");

    }
    private void sendConfirmationEmail(String email, String name) {
        System.out.println("📧 Confirmation email sent to " + name + " at " + email);
    }

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
    public void addUserAsAdmin(String role, Scanner scanner) {
        switch (role.toUpperCase()) {
            case "PATIENT" -> {
                System.out.print("👤 Name: ");
                String name = scanner.nextLine();
                System.out.print("📧 Email: ");
                String email = scanner.nextLine();
                System.out.print("📞 Phone: ");
                String phone = scanner.nextLine();
                System.out.print("🔐 Password: ");
                String password = scanner.nextLine();
                System.out.print("🩺 Medical History: ");
                String history = scanner.nextLine();

                if (checkIfUserExists(email)) {
                    System.out.println("❌ User with this email already exists.");
                } else {
                    registerPatient(name, email, phone, password, history);
                    sendConfirmationEmail(email,name);
                    System.out.println("✅ Patient added by admin.");
                }
            }

            case "DOCTOR" -> {
                System.out.print("👨‍⚕️ Name: ");
                String name = scanner.nextLine();
                System.out.print("📧 Email: ");
                String email = scanner.nextLine();
                System.out.print("📞 Phone: ");
                String phone = scanner.nextLine();
                System.out.print("🔐 Password: ");
                String password = scanner.nextLine();
                System.out.print("💊 Specialization: ");
                String specialization = scanner.nextLine();
                System.out.print("🪪 License Number: ");
                String license = scanner.nextLine();

                if (checkIfUserExists(email)) {
                    System.out.println("❌ User with this email already exists.");
                } else {
                    registerDoctor(name, email, phone, password, specialization, license,false,"-");
                    sendConfirmationEmail(email,name);
                    System.out.println("✅ Doctor added by admin.");
                }
            }

            case "ADMIN" -> {
                System.out.print("👨‍💼 Name: ");
                String name = scanner.nextLine();
                System.out.print("📧 Email: ");
                String email = scanner.nextLine();
                System.out.print("📞 Phone: ");
                String phone = scanner.nextLine();
                System.out.print("🔐 Password: ");
                String password = scanner.nextLine();

                if (checkIfUserExists(email)) {
                    System.out.println("❌ User with this email already exists.");
                } else {
                    Session session = getSession();
                    try {
                        session.beginTransaction();
                        Admin admin = new Admin();
                        admin.setName(name);
                        admin.setEmail(email);
                        admin.setPhone(phone);
                        admin.setPasswordHash(password);
                        admin.setRole("ADMIN");
                        admin.setPermissions("ALL");
                        session.persist(admin);
                        session.getTransaction().commit();
                        sendConfirmationEmail(email,name);
                        System.out.println("✅ Admin added.");
                    } catch (Exception e) {
                        session.getTransaction().rollback();
                        System.err.println("❌ Error: " + e.getMessage());
                    }
                }
            }

            default -> System.out.println("❌ Invalid role selected.");
        }
    }
    private Session getSession() {
        return factory.getCurrentSession();
    }
    public void registerPatient(String name, String email, String phone, String password, String history) {
        if (checkIfUserExists(email)) {
            System.out.println("❌ Registration failed: User with this email already exists.");
            return;
        }

        Patient p = new Patient();
        p.setName(name);
        p.setEmail(email);
        p.setPhone(phone);
        p.setPasswordHash(password);
        p.setRole("PATIENT");
        p.setMedicalHistory(history);

        storePatient(p);
            sendConfirmationEmail(email, name);
            System.out.println("✅ Patient registered.");

    }
    public boolean authenticateUser(String email, String password) {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            Long count = session.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.passwordHash = :pass", Long.class)
                    .setParameter("email", email)
                    .setParameter("pass", password)
                    .uniqueResult();

            session.getTransaction().commit();
            return count != null && count > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error during authentication: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }
    public void registerDoctor(String name, String email, String phone, String password, String specialization, String license,boolean availability,String availabilityTime ) {
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
            d.setAvailabilityHours(availabilityTime);
            session.persist(d);
            session.getTransaction().commit();
            System.out.println("✅ Doctor registered.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    private boolean isTimeWithinAvailability(String availabilityHours, String day, LocalTime time) {
        String[] entries = availabilityHours.split(",");
        for (String entry : entries) {
            String[] parts = entry.trim().split(" ");
            if (parts.length < 2) continue;

            String days = parts[0];
            String times = parts[1];

            List<String> daysCovered = new ArrayList<>();

            if (days.contains("-")) {
                String[] range = days.split("-");
                List<String> allDays = List.of("Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun");
                int start = allDays.indexOf(range[0]);
                int end = allDays.indexOf(range[1]);
                daysCovered.addAll(allDays.subList(start, end + 1));
            } else {
                daysCovered.add(days);
            }

            if (!daysCovered.contains(day)) continue;

            for (String timeSlot : times.split(",")) {
                String[] range = timeSlot.split("-");
                if (range.length != 2) continue;
                LocalTime from = LocalTime.parse(range[0]);
                LocalTime to = LocalTime.parse(range[1]);
                if (!time.isBefore(from) && !time.isAfter(to)) return true;
            }
        }
        return false;
    }
    public void scheduleAppointment(int patientId) {
        Session session = getSession();
        try {
            session.beginTransaction();

            // 1. Show available doctors
            List<Doctor> availableDoctors = session.createQuery(
                    "FROM Doctor WHERE availability = true AND availabilityHours IS NOT NULL", Doctor.class
            ).getResultList();

            if (availableDoctors.isEmpty()) {
                System.out.println("⚠️ No available doctors at the moment.");
                return;
            }

            System.out.println("🩺 Available Doctors:");
            for (Doctor doc : availableDoctors) {
                System.out.println("Doctor ID: " + doc.getUserId() +
                        " | Name: " + doc.getName() +
                        " | Available Hours: " + doc.getAvailabilityHours());
            }

            // 2. Select doctor
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter Doctor ID to book with: ");
            int doctorId = sc.nextInt();
            sc.nextLine(); // clear buffer

            Doctor doctor = session.get(Doctor.class, doctorId);
            if (doctor == null || doctor.getAvailabilityHours() == null) {
                System.out.println("❌ Invalid or unavailable doctor selected.");
                return;
            }

            System.out.println("📅 Doctor " + doctor.getName() + " is available at: " + doctor.getAvailabilityHours());

            // 3. Input appointment time
            System.out.print("Enter appointment date (yyyy-MM-dd): ");
            String date = sc.nextLine();
            System.out.print("Enter appointment time (HH:mm): ");
            String time = sc.nextLine();

            LocalDateTime dateTime = LocalDateTime.parse(date + "T" + (time.length() == 5 ? time : "0" + time));
            String day = dateTime.getDayOfWeek().toString().substring(0, 1).toUpperCase() +
                    dateTime.getDayOfWeek().toString().substring(1, 3).toLowerCase(); // Converts MONDAY to "Mon"

            // 4. Check if day/time is in availability
            if (!isTimeWithinAvailability(doctor.getAvailabilityHours(), day, dateTime.toLocalTime())) {
                System.out.println("❌ Selected time is not within doctor's available hours.");
                return;
            }

            // 5. Check if already booked
            boolean isBooked = session.createQuery(
                            "FROM Appointment WHERE doctor.id = :docId AND appointmentDate = :dateTime", Appointment.class)
                    .setParameter("docId", doctorId)
                    .setParameter("dateTime", dateTime)
                    .getResultList().size() > 0;

            if (isBooked) {
                System.out.println("❌ This slot is already booked. Please choose another time.");
                return;
            }

            // 6. Book appointment
            Patient patient = session.get(Patient.class, patientId);
            if (patient == null) {
                System.out.println("❌ Patient not found.");
                return;
            }

            Appointment appt = new Appointment();
            appt.setDoctor(doctor);
            appt.setPatient(patient);
            appt.setAppointmentDate(dateTime);
            appt.setStatus("SCHEDULED");

            session.persist(appt);

            // 7. Send Notification
            Notification notification = new Notification();
            notification.setUser(patient);
            notification.setMessage("✅ Appointment booked with Dr. " + doctor.getName() +
                    " on " + dateTime.toLocalDate() + " at " + dateTime.toLocalTime());
            notification.setTimestamp(LocalDateTime.now());
            session.persist(notification);

            session.getTransaction().commit();
            System.out.println("✅ Appointment booked and confirmation notification sent!");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error scheduling appointment: " + e.getMessage());
        }
    }
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
    public void viewAllPatients() {
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
    public User loginUser(String email, String password) {
        if (!authenticateUser(email, password)) {
            System.out.println("❌ Login failed. Invalid credentials.");
            return null;
        }

        Session session = factory.openSession();
        User user = null;

        try {
            session.beginTransaction();

            user = session.createQuery(
                            "FROM User WHERE email = :email AND passwordHash = :pass", User.class)
                    .setParameter("email", email)
                    .setParameter("pass", password)
                    .uniqueResult();

            session.getTransaction().commit();

            if (user != null) {
                System.out.println("✅ Login successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error during login: " + e.getMessage());
        } finally {
            session.close();
        }

        return user;
    }
    public void viewAllDoctors() {
        Session session = getSession();
        try {
            session.beginTransaction();
            List<Doctor> docs = session.createQuery("FROM Doctor", Doctor.class).getResultList();
            for (Doctor d : docs) {
                System.out.println("👨‍⚕️ ID: " + d.getUserId() + ", Name: " + d.getName() + ", Spec: " + d.getSpecialization());
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error listing doctors: " + e.getMessage());
        }
    }
    public void removeDoctor(int doctorId) {
        Session session = getSession();
        try {
            session.beginTransaction();
            Doctor d = session.get(Doctor.class, doctorId);
            if (d != null) session.remove(d);
            session.getTransaction().commit();
            System.out.println("🗑️ Doctor removed.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error removing doctor: " + e.getMessage());
        }
    }
    public void removeUser(int userId) {
        Session session = getSession();
        try {
            session.beginTransaction();
            User u = session.get(User.class, userId);
            if (u != null) session.remove(u);
            session.getTransaction().commit();
            System.out.println("🗑️ User removed.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error removing user: " + e.getMessage());
        }
    }
    public void managePatientHistory() {
        Session session = getSession();
        try {
            session.beginTransaction();
            List<Patient> patients = session.createQuery("FROM Patient", Patient.class).getResultList();
            if (patients.isEmpty()) {
                System.out.println("❌ No patients found.");
                return;
            }
            //printing all patients
            System.out.println("📋 All Patients:");
            for (Patient p : patients) {
                System.out.println("ID: " + p.getUserId() + ", Name: " + p.getName() + ", Email: " + p.getEmail());
            }
            //taking patient id
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Patient ID to manage: ");
            int patientId = Integer.parseInt(scanner.nextLine());
            Patient patient = session.get(Patient.class, patientId);
            if (patient == null) {
                System.out.println("❌ Patient not found.");
                return;
            }
            //printing that patient details
            System.out.println("🧍 Patient: " + patient.getName());
            System.out.println("📞 Phone: " + patient.getPhone());
            System.out.println("📧 Email: " + patient.getEmail());
            System.out.println("📝 Current Medical History: " + patient.getMedicalHistory());
            //getting new patient medical history
            System.out.print("Enter new medical history: ");
            String newHistory = scanner.nextLine();
            //updating it
            updatePatientHistory(patientId, newHistory);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error managing patient history: " + e.getMessage());
        }
    }
    public void updatePatientHistory(int patientId, String history) {
        Session session = getSession();
        try {
            session.beginTransaction();
            Patient p = session.get(Patient.class, patientId);
            if (p == null) throw new RuntimeException("Patient not found");
            //setting new medical history in the db
            p.setMedicalHistory(history);
            session.getTransaction().commit();
            System.out.println("📋 Medical history updated.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error updating history: " + e.getMessage());
        }
    }
    public void viewNotifications(int userId) {
        Session session = getSession();
        try {
            session.beginTransaction();

            List<Notification> notifications = session.createQuery("FROM Notification WHERE user.id = :uid", Notification.class)
                    .setParameter("uid", userId).getResultList();

            for (Notification note : notifications) {
                System.out.println("🔔 " + note.getTimestamp() + " - " + note.getMessage());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error fetching notifications: " + e.getMessage());
        }
    }
    public void cancelAppointment(int userId, String role) {
        Session session = getSession();
        try {
            session.beginTransaction();

            //Fetch relevant appointments based on role
            String query;
            switch (role.trim().toUpperCase()) {
                case "PATIENT":
                    query = "FROM Appointment WHERE patient.id = :uid AND status = 'SCHEDULED'";
                    break;
                case "DOCTOR":
                    query = "FROM Appointment WHERE doctor.id = :uid AND status = 'SCHEDULED'";
                    break;
                default:
                    query = "FROM Appointment WHERE status = 'SCHEDULED'";
                    break;
            }

            List<Appointment> appointments = session.createQuery(query, Appointment.class)
                    .setParameter("uid", userId)
                    .getResultList();

            if (appointments.isEmpty()) {
                System.out.println("📭 No appointments to cancel.");
                return;
            }

            System.out.println("📋 Your Scheduled Appointments:");
            for (Appointment appt : appointments) {
                System.out.println("ID: " + appt.getAppointmentId() +
                        " | Patient: " + appt.getPatient().getName() +
                        " | Doctor: " + appt.getDoctor().getName() +
                        " | DateTime: " + appt.getAppointmentDate());
            }

            //Ask user to select an appointment to cancel
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter appointment ID to cancel: ");
            int appointmentId = sc.nextInt();
            sc.nextLine(); // consume newline

            Appointment appt = session.get(Appointment.class, appointmentId);
            if (appt == null || !appt.getStatus().equalsIgnoreCase("SCHEDULED")) {
                System.out.println("❌ Appointment not found or already canceled.");
                return;
            }

            //Cancel the appointment
            appt.setStatus("CANCELED");

            //Send notifications to both patient and doctor
            LocalDateTime now = LocalDateTime.now();
            String cancelMsg = "Appointment on " + appt.getAppointmentDate() +
                    " has been canceled by " + role + ".";

            Notification toPatient = new Notification();
            toPatient.setUser(appt.getPatient());
            toPatient.setMessage("Dear " + appt.getPatient().getName() + ", " + cancelMsg);
            toPatient.setTimestamp(now);
            session.persist(toPatient);

            Notification toDoctor = new Notification();
            toDoctor.setUser(appt.getDoctor());
            toDoctor.setMessage("Dear Dr. " + appt.getDoctor().getName() + ", " + cancelMsg);
            toDoctor.setTimestamp(now);
            session.persist(toDoctor);

            session.getTransaction().commit();
            System.out.println("❌ Appointment canceled successfully.");

        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error canceling appointment: " + e.getMessage());
        }
    }
    public void viewAppointmentsForUser(int userId) {
        Session session = getSession();
        try {
            session.beginTransaction();

            User user = session.get(User.class, userId);
            if (user == null) {
                throw new RuntimeException("User not found.");
            }

            String role = user.getRole();
            List<Appointment> appointments;

            if ("PATIENT".equalsIgnoreCase(role)) {
                appointments = session.createQuery(
                                "FROM Appointment WHERE patient.id = :id AND status != 'CANCELED'", Appointment.class)
                        .setParameter("id", userId)
                        .getResultList();
            } else if ("DOCTOR".equalsIgnoreCase(role)) {
                appointments = session.createQuery(
                                "FROM Appointment WHERE doctor.id = :id AND status != 'CANCELED'", Appointment.class)
                        .setParameter("id", userId)
                        .getResultList();
            } else {
                throw new RuntimeException("Role not authorized to view appointments.");
            }

            if (appointments.isEmpty()) {
                System.out.println("📭 No upcoming appointments found.");
            } else {
                for (Appointment appt : appointments) {
                    System.out.println("📅 Appointment ID: " + appt.getAppointmentId()
                            + ", Patient: " + appt.getPatient().getName()
                            + ", Doctor: " + appt.getDoctor().getName()
                            + ", Date: " + appt.getAppointmentDate());
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error fetching appointments: " + e.getMessage());
        }
    }
    public boolean checkIfUserExists(String email) {
        Session session = getSession();
        try {
            session.beginTransaction();

            Long count = session.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .uniqueResult();

            session.getTransaction().commit();
            return count != null && count > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error checking user existence: " + e.getMessage());
            return false;
        }
    }
    public void setDoctorAvailability(int doctorId, boolean isAvailable, String hours) {
        Session session = getSession();
        try {
            session.beginTransaction();
            Doctor doctor = session.get(Doctor.class, doctorId);
            if (doctor == null) throw new RuntimeException("Doctor not found");
            doctor.setAvailability(isAvailable);
            doctor.setAvailabilityHours(hours);
            session.getTransaction().commit();
            System.out.println("✅ Doctor availability updated.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    public void viewPatientMedicalHistory(int patientId) {
        Session session = getSession();
        try {
            session.beginTransaction();

            Patient patient = session.get(Patient.class, patientId);
            if (patient != null) {
                System.out.println("🧍 Patient: " + patient.getName());
                System.out.println("📝 Medical History: " + patient.getMedicalHistory());
            } else {
                System.out.println("❌ Patient not found.");
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error retrieving history: " + e.getMessage());
        }
    }

}
