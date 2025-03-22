package com.example.cry.test;

import com.example.cry.system.SystemManager;
import com.example.cry.model.Admin;
import com.example.cry.model.Doctor;
import com.example.cry.model.Patient;
import com.example.cry.model.User;
import java.util.Scanner;
import java.time.LocalDateTime;


public class Test {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SystemManager sys = new SystemManager();

    public static void main(String[] args) {
        try {
            System.load("C:\\Users\\sawab\\.jdks\\openjdk-24\\bin\\mssql-jdbc_auth-12.4.2.x64.dll");
            System.out.println("✅ DLL loaded manually.");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ SQL Server Driver Loaded Successfully!");

            while (true) {
                System.out.println("\n=== Health Clinic System ===");
                System.out.println("1. Login");
                System.out.println("2. Register Patient");
                System.out.println("3. Register Doctor");
                System.out.println("4. Exit");
                System.out.print("Select: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> loginUser();
                    case 2 -> registerPatient();
                    case 3 -> registerDoctor();
                    case 4 -> {
                        sys.close();
                        return;
                    }
                    default -> System.out.println("❌ Invalid option");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loginUser() {
        System.out.print("📧 Email: ");
        String email = scanner.nextLine();
        System.out.print("🔒 Password: ");
        String password = scanner.nextLine();

        User user = sys.loginUser(email, password);
        if (user == null) return;

        switch (user.getRole()) {
            case "PATIENT" -> patientMenu((Patient) user);
            case "DOCTOR" -> doctorMenu((Doctor) user);
            case "ADMIN" -> adminMenu((Admin) user);
            default -> System.out.println("❌ Unknown role.");
        }
    }

    private static void patientMenu(Patient patient) {
        while (true) {
            System.out.println("\n👤 Patient Menu");
            System.out.println("1. Book Appointment");
            System.out.println("2. Cancel Appointment");
            System.out.println("3. View My Appointments");
            System.out.println("4. Check Notifications");
            System.out.println("5. Logout");
            System.out.print("Select: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                        sys.scheduleAppointment(patient.getUserId());

                }
                case 2 -> {
                    sys.cancelAppointment(patient.getUserId(), "PATIENT");
                }
                case 3 -> sys.viewAppointmentsForUser(patient.getUserId());
                case 4 -> sys.viewNotifications(patient.getUserId());
                case 5 -> { return; }
                default -> System.out.println("❌ Invalid option");
            }
        }
    }

    private static void doctorMenu(Doctor doctor) {
        while (true) {
            System.out.println("\n🩺 Doctor Menu");
            System.out.println("1. Check My Appointments");
            System.out.println("2. Cancel Appointment");
            System.out.println("3. Check Notifications");
            System.out.println("4. View Patient Medical History");
            System.out.println("5. Manage Patient Medical History");
            System.out.println("6. Logout");
            System.out.print("Select: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> sys.viewAppointmentsForUser(doctor.getUserId());
                case 2 -> {

                    sys.cancelAppointment(doctor.getUserId(), "DOCTOR");
                }
                case 3 -> sys.viewNotifications(doctor.getUserId());
                case 4 -> {
                    System.out.print("Enter Patient ID to view history: ");
                    int patientId = Integer.parseInt(scanner.nextLine());
                    sys.viewPatientMedicalHistory(patientId);
                }
                case 5 -> {
                    sys.managePatientHistory();
                }
                case 6 -> { return; }
                default -> System.out.println("❌ Invalid option");
            }
        }
    }

    private static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n🔐 Admin Menu");
            System.out.println("1. Add User");
            System.out.println("2. Remove User");
            System.out.println("3. Remove Doctor");
            System.out.println("4. View All Patients");
            System.out.println("5. View All Doctors");
            System.out.println("6. View All Appointments");
            System.out.println("7. Logout");
            System.out.print("Select: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter user role to add (PATIENT/DOCTOR/ADMIN): ");
                    String role = scanner.nextLine();
                    sys.addUserAsAdmin(role, scanner);
                }
                case 2 -> {
                    System.out.print("User ID: ");
                    sys.removeUser(scanner.nextInt());
                }
                case 3 -> {
                    System.out.print("Doctor ID: ");
                    sys.removeDoctor(scanner.nextInt());
                }
                case 4 -> sys.viewAllPatients();
                case 5 -> sys.viewAllDoctors();
                case 6 -> sys.listAppointments();
                case 7 -> { return; }
                default -> System.out.println("❌ Invalid option");
            }
        }
    }

    private static void registerPatient() {
        System.out.print("👤 Name: ");
        String name = scanner.nextLine();
        System.out.print("📧 Email: ");
        String email = scanner.nextLine();
        System.out.print("📱 Phone: ");
        String phone = scanner.nextLine();
        System.out.print("🔒 Password: ");
        String pass = scanner.nextLine();
        System.out.print("📋 Medical History: ");
        String hist = scanner.nextLine();

        sys.registerPatient(name, email, phone, pass, hist);
    }

    private static void registerDoctor() {
        System.out.print("👤 Name: ");
        String name = scanner.nextLine();
        System.out.print("📧 Email: ");
        String email = scanner.nextLine();
        System.out.print("📱 Phone: ");
        String phone = scanner.nextLine();
        System.out.print("🔒 Password: ");
        String pass = scanner.nextLine();
        System.out.print("💼 Specialization: ");
        String spec = scanner.nextLine();
        System.out.print("📄 License #: ");
        String license = scanner.nextLine();
        System.out.print("Available? (true/false):");
        boolean availability = scanner.nextLine().equals("true");
        System.out.print("Available Time (Mon-Fri 12:00 AM - 11:59 PM): ");
        String availableTime = scanner.nextLine();


        sys.registerDoctor(name, email, phone, pass, spec, license,availability, availableTime );
    }
}
