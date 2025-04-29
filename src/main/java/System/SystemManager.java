package System;
import com.example.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.mail.*;
import javax.mail.internet.*;
import com.example.Patient;
import com.example.DoctorAvailabilityHours;





public class SystemManager {

    public SessionFactory factory;
    private Session session=factory.openSession();
    public void storePatient(Patient patient) {
        Session session = getSession();
        session.beginTransaction();
        session.persist(patient);
        session.getTransaction().commit();
        System.out.println("Patient stored successfully.");

    }
    public SystemManager(Session session) {
        this.session = session;
    }
    public Patient get_patient(int id) {
        Session session = getSession();
        Transaction tx = null;
        Patient patient = null;

        try {
            tx = session.beginTransaction();
            patient = session.get(Patient.class, id);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return patient;
    }
    public Doctor get_doctor(int id) {
        Session session = getSession();
        Transaction tx = null;
        Doctor doctor = null;

        try {
            tx = session.beginTransaction();
            doctor = session.get(Doctor.class, id);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return doctor;
    }
    public void submitFeedback( Patient patient, Doctor doctor, int rating, String comments) {
        Transaction tx = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();

            Feedback feedback = new Feedback();
            feedback.setPatient(patient);
            feedback.setDoctor(doctor);
            feedback.setRating(rating);
            feedback.setComments(comments);
            feedback.setTimestamp(new java.util.Date());

            session.save(feedback);

            tx.commit();
            System.out.println("✅ Feedback submitted.");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
    public void sendConfirmationEmail(String email, String name, String message) {
        sendEmail(email,"Notification from Hospital Management System",message);
        //System.out.println("📧 Confirmation email sent to " + name + " at " + email);
    }
    public static void sendEmail(String to, String subject, String messageText) {
        final String from = "i220807@nu.edu.pk";
        final String password = "hopc gfii otry vvzv";  // App Password
        //to="i220807@nu.edu.pk";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        javax.mail.Session mailSession = javax.mail.Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });


        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(messageText);

            System.out.println("📨 Attempting to send email...");
            Transport.send(message);
            System.out.println("✅ Email sent successfully to " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
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
                    sendConfirmationEmail(email,name,"You are added as a patient in to the system");
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
                System.out.print("Available:");
                String availability = scanner.nextLine();
                System.out.print("Available Hours(e.g Mon-Wed 08:00-17:00:");
                String availabilityHours = scanner.nextLine();

                if (checkIfUserExists(email)) {
                    System.out.println("❌ User with this email already exists.");
                } else {
                    registerDoctor(name, email, phone, password, specialization, license,false,"-");
                    sendConfirmationEmail(email,name,"You are added as a doctor in to the system");
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
                        session.persist(admin);
                        session.getTransaction().commit();
                        sendConfirmationEmail(email,name,"You are added as an admin in to the system");
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
    public Session getSession() {
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
        sendConfirmationEmail(email, name,"You are registered as a patient.");
        System.out.println("✅ Patient registered.");

    }
    public boolean authenticateUser(String email, String password) {
        Session session = null;
        try {
            session = factory.openSession();
            if (session == null) {
                throw new RuntimeException("Failed to open Hibernate session.");
            }
            session.beginTransaction();
            Long count = session.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.passwordHash = :pass", Long.class)
                    .setParameter("email", email)
                    .setParameter("pass", password)
                    .uniqueResult();

            session.getTransaction().commit();
            return count != null && count > 0;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.err.println("Error during authentication: " + e.getMessage());
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    public void registerDoctor(String name, String email, String phone, String password,
                               String specialization, String license,boolean availability,String availabilityTime ) {
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
            sendConfirmationEmail(email, name,"You are registered as a doctor.");
            System.out.println("✅ Doctor registered.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    public boolean isTimeWithinAvailability(String availabilityHours, String day, LocalTime time) {
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
    public boolean scheduleAppointment(User user, String doctorName, LocalDateTime appointmentDateTime) {
        if (!(user instanceof Patient)) {
            //System.out.println("❌ Invalid user type. Expected a patient.");
            return false;
        }

        Patient patient = (Patient) user;
        int patientId = patient.getUserId();  // Now you have the patientId if needed
        Session session = getSession();

        try {
            Transaction tx = null;
            tx=session.beginTransaction();
            // Get the doctor by name or ID
            Doctor doctor = session.createQuery(
                            "FROM Doctor WHERE name = :doctorName AND availability = true", Doctor.class)
                    .setParameter("doctorName", doctorName)
                    .uniqueResult();

            if (doctor == null || doctor.getAvailabilityHours() == null) {
                System.out.println("❌ Invalid or unavailable doctor selected.");
                return false;
            }

            // Check if the selected time is within doctor's availability
            String day = appointmentDateTime.getDayOfWeek().toString().substring(0, 1).toUpperCase() +
                    appointmentDateTime.getDayOfWeek().toString().substring(1, 3).toLowerCase(); // Converts MONDAY to "Mon"

            if (!isTimeWithinAvailability(doctor.getAvailabilityHours(), day, appointmentDateTime.toLocalTime())) {
                System.out.println("❌ Selected time is not within doctor's available hours.");
                return false;
            }

            // Check if the slot is already booked
            boolean isBooked = session.createQuery(
                            "SELECT COUNT(a) FROM Appointment a " +
                                    "WHERE a.doctor.id = :docId AND a.appointmentDate = :dateTime AND a.status != 'CANCELED'", Long.class)
                    .setParameter("docId", doctor.getUserId())
                    .setParameter("dateTime", appointmentDateTime)
                    .uniqueResult() > 0;

            if (isBooked) {
                //System.out.println("❌ This slot is already booked. Please choose another time.");
                return false;
            }

            // Book the appointment
            Appointment appt = new Appointment();
            appt.setDoctor(doctor);
            appt.setPatient(patient);
            appt.setAppointmentDate(appointmentDateTime);
            appt.setStatus("SCHEDULED");

            session.persist(appt);

            // Send Notification
            Notification notification = new Notification();
            notification.setUser(patient);
            notification.setMessage(" Appointment booked with Dr. " + doctor.getName() +
                    " on " + appointmentDateTime.toLocalDate() + " at " + appointmentDateTime.toLocalTime());
            notification.setTimestamp(LocalDateTime.now());
            session.persist(notification);

            // Send confirmation email
            sendConfirmationEmail(patient.getEmail(), patient.getName(), "Appointment is booked with doctor " + doctor.getName() + " on " + appointmentDateTime.toLocalDate() + " at " + appointmentDateTime.toLocalTime());
            sendConfirmationEmail(doctor.getEmail(), doctor.getName(), "Patient " + patient.getName() + " has booked an appointment with you on " + appointmentDateTime.toLocalDate() + " at " + appointmentDateTime.toLocalTime());

            tx.commit();
            System.out.println("Appointment booked and confirmation notification sent!");
            return true;

        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println(" Error scheduling appointment: " + e.getMessage());
            return false;
        }
    }
    public boolean bookAppointment(int patientID, int doctorID, LocalDateTime appointmentDate) {
        Session session = null;  // Initialize session outside try block to ensure proper scope
        Transaction transaction = null;
        String status = "SCHEDULED";

        try {
            // Get session
            session = getSession();

            // Start a new transaction
            transaction = session.beginTransaction();

            // Get Patient and Doctor objects
            Patient patient = session.get(Patient.class, patientID);
            Doctor doctor = session.get(Doctor.class, doctorID);

            // Check if both Patient and Doctor are valid
            if (patient == null || doctor == null) {
                throw new RuntimeException("Invalid Patient or Doctor ID.");
            }



            // Create the Appointment object and set values
            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setStatus(status);

            // Save the appointment
            session.save(appointment);

            // Commit the transaction after successfully saving
            transaction.commit();

            return true; // Appointment booked successfully
        } catch (Exception e) {
            // If transaction is not null, rollback
            if (transaction != null) {
                transaction.rollback();
            }

            // Log the exception for debugging purposes
            e.printStackTrace();

            // Return false if there was an issue booking the appointment
            return false;
        } finally {
            // Ensure the session is closed in the finally block
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    public LocalDateTime getAppointmentDateTimeFromSlot(int doctorId, int slotId) {
        Transaction transaction = null;
        LocalDateTime appointmentDateTime = null;

        try (Session session = getSession()) {
            transaction = session.beginTransaction();

            // Fetch the DoctorAvailabilityHours by doctorId and slotId (assuming slotId represents appointmentDate)
            DoctorAvailabilityHours availability = session.get(DoctorAvailabilityHours.class, slotId);

            if (availability != null) {
                // Now we have the appointmentDate from the DoctorAvailabilityHours entity
                appointmentDateTime = availability.getAppointmentDate();
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Or log the error properly
        }

        return appointmentDateTime;
    }
    public List<DoctorAvailabilityHours> getAvailableSlots(int doctorID) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        Transaction tx = null;

        List<DoctorAvailabilityHours> availableSlots = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            String hql = "FROM DoctorAvailabilityHours d WHERE d.id.doctorID = :doctorID AND d.status = true";
            availableSlots = session.createQuery(hql, DoctorAvailabilityHours.class)
                    .setParameter("doctorID", doctorID)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }

        return availableSlots;
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
        //sendEmail("", "Hi", "OHO hello");
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
            sendConfirmationEmail(appt.getDoctor().getEmail(), appt.getDoctor().getName(), cancelMsg);
            Notification toDoctor = new Notification();
            toDoctor.setUser(appt.getDoctor());
            toDoctor.setMessage("Dear Dr. " + appt.getDoctor().getName() + ", " + cancelMsg);
            toDoctor.setTimestamp(now);
            session.persist(toDoctor);
            sendConfirmationEmail(appt.getPatient().getEmail(), appt.getPatient().getName(), cancelMsg);
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
    public List<Doctor> getAvailableDoctors() {
        Session session = getSession();
        Transaction tx = null;
        try {
            // Start a new transaction
            tx = session.beginTransaction();

            // Using HQL to get doctors where availability = 1
            List<Doctor> doctors = session.createQuery(
                            "FROM Doctor d WHERE d.availability = 1", Doctor.class)
                    .getResultList(); // Use getResultList() instead of .list()

            // Check if the result list is empty
            if (doctors.isEmpty()) {
                // Log the error before throwing the exception
                System.err.println("No available doctors found!");
                throw new RuntimeException("No available doctors found!");
            }

            // Commit the transaction
            tx.commit();

            return doctors;
        } catch (Exception e) {
            // Rollback in case of an error
            if (tx != null) {
                tx.rollback();
            }
            // Log the exception (optional, for debugging purposes)
            e.printStackTrace();
            throw new RuntimeException("An error occurred while fetching available doctors.", e);
        } finally {
            // Always close the session in the finally block
            session.close();
        }
    }
    public void addDoctor(Doctor doctor) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.save(doctor); // Save the doctor in the database
            session.getTransaction().commit();
            System.out.println("✅ Doctor added successfully!");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("❌ Error adding doctor: " + e.getMessage());
        } finally {
            session.close();
        }
    }
    public void setSession(Session session) {
        this.factory = session.getSessionFactory();  // Assuming factory is being used in your real code
    }



}