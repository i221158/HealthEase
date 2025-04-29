package com.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import System.*; // Assuming your SystemManager is in System package

@WebServlet("/availableSlots")
public class GetAvailableSlotsServlet extends HttpServlet {

    private SystemManager sys = new SystemManager(); // Central system manager

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("getAvailableSlots".equals(action)) {
            String doctorIdParam = request.getParameter("doctorID");

            if (doctorIdParam == null || doctorIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing doctorID parameter.\"}");
                return;
            }

            try {
                int doctorID = Integer.parseInt(doctorIdParam);

                List<DoctorAvailabilityHours> slots = sys.getAvailableSlots(doctorID);

                if (slots.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"No available slots found for doctorID " + doctorID + ".\"}");
                    return;
                }

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                StringBuilder json = new StringBuilder("[");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                for (int i = 0; i < slots.size(); i++) {
                    DoctorAvailabilityHours slot = slots.get(i);
                    json.append("{")
                            .append("\"appointmentDate\":\"").append(slot.getAppointmentDate().format(formatter)).append("\",")
                            .append("\"status\":").append(slot.getStatus())
                            .append("}");
                    if (i < slots.size() - 1) {
                        json.append(",");
                    }
                }

                json.append("]");
                response.getWriter().write(json.toString());

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid doctorID format. Must be a number.\"}");
            }
        }
    }
}
