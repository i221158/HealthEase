package com.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import System.*; // Your package

@WebServlet("/bookAppointment")
public class BookAppointmentServlet extends HttpServlet {
    private SystemManager sys = new SystemManager(); // Central class

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getDoctors".equals(action)) {
            List<Doctor> availableDoctors = sys.getAvailableDoctors();

            if (availableDoctors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"No available doctors found.\"}");
                return;
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < availableDoctors.size(); i++) {
                Doctor doc = availableDoctors.get(i);
                json.append("{")
                        .append("\"userId\":").append(doc.getUserId()).append(",")
                        .append("\"name\":\"").append(doc.getName()).append("\",")
                        .append("\"specialization\":\"").append(doc.getSpecialization()).append("\"")
                        .append("}");
                if (i < availableDoctors.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            response.getWriter().write(json.toString());
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SystemManager sys = new SystemManager();
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("user") == null) {
            response.sendRedirect("login.html?error=session");
            return;
        }

        Patient patient = (Patient) httpSession.getAttribute("user");

        if (patient == null) {
            response.sendRedirect("login.html");
            return;
        }

        // Get doctorId and slotId from the form
        String doctorIdParam = request.getParameter("doctorId");
        String slotIdParam = request.getParameter("slotId");

        if (doctorIdParam == null || slotIdParam == null || doctorIdParam.isEmpty() || slotIdParam.isEmpty()) {
            response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("All fields must be filled out."));
            return;
        }

        if (!doctorIdParam.matches("\\d+")) {
            response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("Doctor ID must be a valid number."));
            return;
        }

        try {
            int doctorId = Integer.parseInt(doctorIdParam);

            // New: slotId format is doctorId_appointmentDate
            String[] slotParts = slotIdParam.split("_", 2); // split into 2 parts only
            if (slotParts.length != 2) {
                response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("Invalid slot format: " + slotIdParam));
                return;
            }

            String slotDoctorId = slotParts[0];
            String appointmentDateString = slotParts[1];

            // Validate that the doctor ID from slot matches selected doctor
            if (!slotDoctorId.equals(doctorIdParam)) {
                response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("Doctor mismatch in selected slot."));
                return;
            }

            // Parse appointmentDate
            LocalDateTime appointmentDate;
            try {
                appointmentDate = LocalDateTime.parse(appointmentDateString.replace(" ", "T")); // Convert to ISO format
            } catch (Exception e) {
                response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("Invalid appointment date format."));
                return;
            }

            // Attempt to book the appointment
            boolean success = sys.bookAppointment(patient.getUserId(), doctorId, appointmentDate);

            String message = success ? "Appointment successfully booked!" : "Failed to book appointment. Please try again.";
            response.sendRedirect("book-appointment.html?message=" + encodeURIComponent(message));

        } catch (NumberFormatException e) {
            System.out.println("Invalid input: doctorId=" + doctorIdParam + ", slotId=" + slotIdParam);
            e.printStackTrace();
            response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("Invalid input format."));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("book-appointment.html?message=" + encodeURIComponent("Server error during booking."));
        }
    }

    // Utility to safely encode messages into URL
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }


}
