package com.webmedicalportaldemo.controller;
import com.webmedicalportaldemo.dao.AppointmentDAOInterface;
import com.webmedicalportaldemo.dao.DoctorDAO;
import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.model.Doctor;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.ApptFileService;
import com.webmedicalportaldemo.model.Appointment;
import com.webmedicalportaldemo.model.Consultation;
import com.webmedicalportaldemo.model.Surgery;
import java.math.BigDecimal;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class AppointmentController {
    private final AppointmentDAOInterface apptDAO;
    private final ApptFileService apptFileService;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;
    public AppointmentController(AppointmentDAOInterface apptDAO, ApptFileService apptFileService, DoctorDAO doctorDAO, PatientDAO patientDAO) {
        this.apptDAO = apptDAO;
        this.apptFileService = apptFileService;
        this.doctorDAO = doctorDAO;
        this.patientDAO = patientDAO;
    }
    /**
     * Returns available appointment slots for a selected doctor and date.
     * doctorID represents users.user_id because AppointmentDAO resolves the doctor entity ID internally.
     */
    @GetMapping("/getAvailableSlots")
    @ResponseBody
    public List<String> getAvailableSlots(@RequestParam("doctorID") int doctoruserID, @RequestParam("date") String date, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null || doctoruserID <= 0 || date == null || date.isBlank()) {
            return Collections.emptyList();
        }
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            if (selectedDate.isBefore(LocalDate.now())) {
                return Collections.emptyList();
            }
        } catch (Exception exception) {
            return Collections.emptyList();
        }
        return apptDAO.getAvailableSlots(doctoruserID, date);
    }
    /**
     * Returns doctors by specialization as JSON.
     */
    @GetMapping("/getDoctorsBySpec")
    @ResponseBody
    public List<Doctor> getDoctorsBySpecialization(@RequestParam(value = "specialization", required = false) String specialization, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return Collections.emptyList();
        }
        if (specialization == null || specialization.isBlank() || "All Specializations".equalsIgnoreCase(specialization.trim())) {
            return doctorDAO.getAllDoctors();
        }
        return doctorDAO.getDoctorsBySpecialization(specialization.trim());
    }
    /**
     * Returns the logged-in patient's appointment history with a selected doctor.
     */
    @GetMapping("/history")
    @ResponseBody
    public List<String> getDoctorPatientHistory(@RequestParam("doctorID") int doctoruserID, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "PATIENT") || doctoruserID <= 0) {
            return Collections.emptyList();
        }
        Integer patientID = patientDAO.getPatientIDByuserID(currentUser.getuserID());
        Integer doctorID = doctorDAO.getDoctorIDByuserID(doctoruserID);
        if (patientID == null || patientID <= 0 || doctorID == null || doctorID <= 0) {
            return Collections.emptyList();
        }
        return apptFileService.readFile(patientID, doctorID);
    }
    /**
     * Displays the patient appointment-booking page.
     */
    @GetMapping("/patient/book_appointment")
    public String showBookingPage(HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "PATIENT")) {
            return currentUser == null ? "redirect:/login" : redirectByRole(currentUser);
        }
        List<Doctor> doctorList = doctorDAO.getAllDoctors();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("doctorList", doctorList);
        return "patient/book_appointment";
    }
    /**
     * Books a new appointment for the logged-in patient.
     */
    @PostMapping("/bookAppointment")
    public String bookAppointment(@RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("doctorID") int doctoruserID, @RequestParam("appointmentType") String appointmentType, @RequestParam(value = "additionalCharge", required = false) String additionalCharge, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "PATIENT")) {
            return currentUser == null ? "redirect:/login" : redirectByRole(currentUser);
        }
        if (doctoruserID <= 0 || date == null || date.isBlank() || time == null || time.isBlank()) {
            return "redirect:/patient/book_appointment?error=invalidInput";
        }
        if (!isValidFutureAppointment(date, time)) {
            return "redirect:/patient/book_appointment?error=invalidDateTime";
        }
        String normalizedType = normalizeAppointmentType(appointmentType);
        String normalizedCharge = normalizeAdditionalCharge(additionalCharge);
        if (!"SURGERY".equalsIgnoreCase(normalizedType)) {
            normalizedCharge = "NONE";
        }
        Appointment appointment;
        if ("SURGERY".equalsIgnoreCase(normalizedType)) {
            Surgery surgery = new Surgery(doctoruserID, currentUser.getuserID(), date, time, "UNASSIGNED");
            surgery.setAddCharge(normalizedCharge);
            appointment = surgery;
        } else {
            appointment = new Consultation(doctoruserID, currentUser.getuserID(), date, time, "UNASSIGNED");
        }
        BigDecimal totalFee = BigDecimal.valueOf(appointment.calculateFee()).setScale(2);
        boolean success = apptDAO.bookAppointment(doctoruserID, currentUser.getuserID(), date, time, normalizedType, normalizedCharge, totalFee);
        return success ? "redirect:/patientDashboard?msg=bookingSuccess" : "redirect:/patient/book_appointment?error=slotUnavailable";
    }

    private String normalizeAdditionalCharge(String additionalCharge) {
        if (additionalCharge == null || additionalCharge.isBlank()) {
            return "NONE";
        }
        String normalizedCharge = additionalCharge.trim().toUpperCase(Locale.ROOT);
        return switch (normalizedCharge) {
            case "NONE", "ANESTHESIA", "FACILITY", "EQUIPMENT", "OTHER" -> normalizedCharge;
            default -> "NONE";
        };
    }
    /**
     * Accepts or cancels an appointment.
     */
    @GetMapping("/updateAppointment")
    public String updateAppointmentStatus(@RequestParam("id") int appointmentID, @RequestParam("action") String action, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (appointmentID <= 0 || action == null || action.isBlank()) {
            return redirectByRoleWithMessage(currentUser, "error");
        }
        boolean success;
        if ("accept".equalsIgnoreCase(action)) {
            if (!hasRole(currentUser, "DOCTOR")) {
                return redirectByRoleWithMessage(currentUser, "unauthorized");
            }
            success = apptDAO.updateAppointmentStatus(appointmentID, "CONFIRMED", null, null, currentUser.getuserID());
        } else if ("complete".equalsIgnoreCase(action)) {
            if (!hasRole(currentUser, "DOCTOR")) {
                return redirectByRoleWithMessage(currentUser, "unauthorized");
            }
            success = apptDAO.updateAppointmentStatus(appointmentID, "COMPLETED", null, null, currentUser.getuserID());
        } else if ("cancel".equalsIgnoreCase(action)) {
            if (!hasRole(currentUser, "PATIENT") && !hasRole(currentUser, "DOCTOR") && !hasRole(currentUser, "HOSPITAL_ADMIN")) {
                return redirectByRoleWithMessage(currentUser, "unauthorized");
            }
            success = apptDAO.cancelAppointment(appointmentID);
        } else {
            return redirectByRoleWithMessage(currentUser, "invalidAction");
        }
        return redirectByRoleWithMessage(currentUser, success ? action.toLowerCase(Locale.ROOT) + "Success" : "error");
    }
    /**
     * Reschedules an appointment.
     */
    @PostMapping("/updateAppointment")
    public String rescheduleAppointment(@RequestParam("id") int appointmentID, @RequestParam("action") String action, @RequestParam("newDate") String newDate, @RequestParam("newTime") String newTime, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!hasRole(currentUser, "DOCTOR") && !hasRole(currentUser, "PATIENT")) {
            return redirectByRoleWithMessage(currentUser, "unauthorized");
        }
        if (appointmentID <= 0 || !"rescheduled".equalsIgnoreCase(action) || !isValidFutureAppointment(newDate, newTime)) {
            return redirectByRoleWithMessage(currentUser, "invalidReschedule");
        }
        boolean success = apptDAO.updateAppointmentStatus(appointmentID, "RESCHEDULED", newDate, newTime, currentUser.getuserID());
        return redirectByRoleWithMessage(currentUser, success ? "rescheduled" : "error");
    }
    /**
     * Updates a doctor's recurring weekly availability.
     */
    @PostMapping("/updateAvailability")
    public String updateAvailability(@RequestParam(value = "workDays", required = false) List<Integer> days, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime, HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "DOCTOR")) {
            return currentUser == null ? "redirect:/login" : redirectByRoleWithMessage(currentUser, "unauthorized");
        }
        if (days == null || days.isEmpty() || !isValidTimeRange(startTime, endTime)) {
            return "redirect:/doctorDashboard?msg=invalidAvailability";
        }
        boolean allUpdated = true;
        for (Integer dayOfWeek : days) {
            if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
                allUpdated = false;
                continue;
            }
            boolean updated = apptDAO.setDoctorAvailability(currentUser.getuserID(), dayOfWeek, startTime, endTime);
            if (!updated) {
                allUpdated = false;
            }
        }
        return allUpdated ? "redirect:/doctorDashboard?msg=availabilityUpdated" : "redirect:/doctorDashboard?msg=availabilityPartial";
    }
    /**
     * Safely retrieves the current user from the HTTP session.
     */
    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object sessionUser = session.getAttribute("user");
        return sessionUser instanceof User ? (User) sessionUser : null;
    }
    /**
     * Performs a null-safe role check.
     */
    private boolean hasRole(User user, String requiredRole) {
        return user != null && user.getRole() != null && requiredRole.equalsIgnoreCase(user.getRole());
    }
    /**
     * Validates that an appointment date and time are not in the past.
     */
    private boolean isValidFutureAppointment(String date, String time) {
        if (date == null || date.isBlank() || time == null || time.isBlank()) {
            return false;
        }
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);
            LocalDate currentDate = LocalDate.now();
            if (appointmentDate.isBefore(currentDate)) {
                return false;
            }
            return !appointmentDate.equals(currentDate) || appointmentTime.isAfter(LocalTime.now());
        } catch (Exception exception) {
            return false;
        }
    }
    /**
     * Validates a doctor's working-hour range.
     */
    private boolean isValidTimeRange(String startTime, String endTime) {
        if (startTime == null || startTime.isBlank() || endTime == null || endTime.isBlank()) {
            return false;
        }
        try {
            return LocalTime.parse(startTime).isBefore(LocalTime.parse(endTime));
        } catch (Exception exception) {
            return false;
        }
    }
    /**
     * Normalizes appointment types against the database values.
     */
    private String normalizeAppointmentType(String appointmentType) {
        if (appointmentType == null || appointmentType.isBlank()) {
            return "CONSULTATION";
        }
        String normalizedType = appointmentType.trim().toUpperCase(Locale.ROOT);
        return switch (normalizedType) {
            case "CONSULTATION", "SURGERY", "FOLLOW_UP", "EMERGENCY" -> normalizedType;
            default -> "CONSULTATION";
        };
    }
    /**
     * Redirects users to the dashboard associated with their role.
     */
    private String redirectByRole(User user) {
        if (user == null || user.getRole() == null) {
            return "redirect:/login";
        }
        return switch (user.getRole().toUpperCase(Locale.ROOT)) {
            case "PATIENT" -> "redirect:/patientDashboard";
            case "DOCTOR" -> "redirect:/doctorDashboard";
            case "PHARMACIST" -> "redirect:/pharmacistDashboard";
            case "LAB_TECHNICIAN" -> "redirect:/labDashboard";
            case "HOSPITAL_ADMIN" -> "redirect:/adminDashboard";
            case "SYSTEM_ADMIN" -> "redirect:/systemAdminDashboard";
            default -> "redirect:/login?error=invalidRole";
        };
    }
    /**
     * Redirects users to the correct dashboard with a status message.
     */
    private String redirectByRoleWithMessage(User user, String message) {
        String redirect = redirectByRole(user);
        if (message == null || message.isBlank()) {
            return redirect;
        }
        return redirect.contains("?") ? redirect + "&msg=" + message : redirect + "?msg=" + message;
    }
}