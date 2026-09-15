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
        // In a production app, you might map this to EmergencyAlertDTO to hide sensitive info.
        // For now, returning the active requests provides the JS with the data it needs.
        return emergencyService.getActiveEmergencies();
    }

    // Show the Fast-Track Intake Form
    @GetMapping("/request")
    public String showEmergencyRequestForm() {
        return "emergency/emergency_request";
    }

    // Process the Intake Form Submission
    @PostMapping("/submit")
    public String submitEmergencyRequest(@ModelAttribute EmergencyRequest req, RedirectAttributes redirectAttributes) {
        boolean success = emergencyService.createEmergencyRequest(req);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "🚨 Emergency alert triggered! Ambulance and staff notified.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to trigger alert. Please try again.");
        }

        // Redirects back to the request form so they see the success message
        return "redirect:/emergency/request";
    }
}