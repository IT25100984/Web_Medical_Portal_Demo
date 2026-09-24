package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.model.EmergencyRequest;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.EmergencyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/emergency")
public class EmergencyController {

    private static final String NAME_REGEX = "^[a-zA-Z\\s'-]+$";
    private static final String SL_PHONE_REGEX = "^(?:\\+94|0)[0-9]{9}$";

    private final EmergencyService emergencyService;

    @Autowired
    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            user = (User) session.getAttribute("user");
        }

        if (user == null || (!"HOSPITAL_ADMIN".equalsIgnoreCase(user.getRole())
                && !"SYSTEM_ADMIN".equalsIgnoreCase(user.getRole()))) {
            return "redirect:/login";
        }

        model.addAttribute("emergencies", emergencyService.getActiveEmergencies());
        model.addAttribute("availableDoctors", emergencyService.getAvailableDoctors());
        return "emergency/emergency_dashboard";
    }

    @PostMapping("/assign")
    public String assignDoctor(@RequestParam("requestId") int requestId,
                               @RequestParam("doctorId") int doctorId,
                               RedirectAttributes redirectAttributes) {
        boolean success = emergencyService.assignDoctor(requestId, doctorId);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Doctor assigned and dispatched successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to assign doctor.");
        }
        return "redirect:/emergency/dashboard";
    }

    // API Endpoint for Real-Time Polling (Emergency Alerts)
    @GetMapping("/api/alerts")
    @ResponseBody
    public List<EmergencyRequest> getLiveAlerts() {
        return emergencyService.getActiveEmergencies();
    }

    // Show the Fast-Track Intake Form
    @GetMapping("/request")
    public String showEmergencyRequestForm() {
        return "emergency/emergency_request";
    }

    // Process the Intake Form Submission with Server-Side Validation
    @PostMapping("/submit")
    public String submitEmergencyRequest(@ModelAttribute EmergencyRequest req, RedirectAttributes redirectAttributes) {
        String patientName = req.getPatientName() != null ? req.getPatientName().trim() : "";
        String contactNumber = req.getContactNumber() != null ? req.getContactNumber().replaceAll("\\s+", "") : "";

        // 1. Validate Patient Name (no numbers or prohibited special characters)
        if (patientName.isEmpty() || !patientName.matches(NAME_REGEX)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid patient name. Names can only contain letters, spaces, hyphens, and apostrophes.");
            return "redirect:/emergency/request";
        }

        // 2. Validate Sri Lankan Phone Format (e.g. 0771234567 or +94771234567)
        if (contactNumber.isEmpty() || !contactNumber.matches(SL_PHONE_REGEX)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid Sri Lankan contact number. Please use 07XXXXXXXX or +947XXXXXXXX format.");
            return "redirect:/emergency/request";
        }

        // Set sanitized inputs back into object
        req.setPatientName(patientName);
        req.setContactNumber(contactNumber);

        // 3. Process Request
        boolean success = emergencyService.createEmergencyRequest(req);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "🚨 Emergency alert triggered! Ambulance and staff notified.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to trigger alert. Please try again.");
        }

        return "redirect:/emergency/request";
    }
}