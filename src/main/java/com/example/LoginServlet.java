package com.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

// ❌ FIX THIS: "System.*" is invalid in Java, remove this line
// import System.*;

// ✅ Instead, import your custom SystemManager class correctly:
import System.*;// Replace with actual package // Replace with actual package

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private SystemManager sys = new SystemManager(); // This contains your loginUser() logic

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = sys.loginUser(email, password);
        if (user == null) {
            response.sendRedirect("login.html?error=1");
            return;
        }

        // Store user in session
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userType", user.getRole());
        session.setAttribute("userName", user.getEmail());

        // Redirect to appropriate dashboard
        switch (user.getRole().toUpperCase()) {
            case "PATIENT" -> response.sendRedirect("dashboard.html");
            case "DOCTOR" -> response.sendRedirect("doctor-dashboard.html");
            case "ADMIN" -> response.sendRedirect("admin-dashboard.html");
            default -> response.sendRedirect("login.html?error=role");
        }
    }
}
