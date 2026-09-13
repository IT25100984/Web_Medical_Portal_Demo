package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.AppointmentDAO;
import com.webmedicalportaldemo.dao.DoctorDAO;
import com.webmedicalportaldemo.dao.EmployeeDAO;
import com.webmedicalportaldemo.dao.FeedbackDAO;
import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.dao.PrescriptionDAO;
import com.webmedicalportaldemo.dto.AppointmentDTO;
import com.webmedicalportaldemo.model.Doctor;
import com.webmedicalportaldemo.model.Employee;
import com.webmedicalportaldemo.model.Patient;
import com.webmedicalportaldemo.model.Prescription;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.LabReportService;
import com.webmedicalportaldemo.service.MedFileService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final AppointmentDAO apptDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;
    private final EmployeeDAO employeeDAO;
    private final FeedbackDAO feedbackDAO;
    private final PrescriptionDAO prescriptionDAO;
    private final MedFileService medFileService;
    private final LabReportService labReportService;

    public DashboardController(AppointmentDAO apptDAO, DoctorDAO doctorDAO, PatientDAO patientDAO,
                               EmployeeDAO employeeDAO, FeedbackDAO feedbackDAO, LabReportService labReportService,
                               PrescriptionDAO prescriptionDAO, MedFileService medFileService) {
        this.apptDAO = apptDAO;
        this.doctorDAO = doctorDAO;
        this.patientDAO = patientDAO;
        this.employeeDAO = employeeDAO;
        this.feedbackDAO = feedbackDAO;
        this.prescriptionDAO = prescriptionDAO;
        this.medFileService = medFileService;
        this.labReportService = labReportService;
    }

    /*
     * Patient dashboard
     */
    @GetMapping("/patientDashboard")
    public String showPatientDashboard(HttpSession session, Model model, HttpServletResponse response) {
        preventDashboardCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "PATIENT")) {
            return "redirect:/login";
        }

        // Existing appointments query
        List<AppointmentDTO> myAppointments = apptDAO.getAppointmentsByPatient(currentUser.getuserID());

        // Fetch prescription orders for the logged-in patient
        Integer patientID = patientDAO.getPatientIDByuserID(currentUser.getuserID());
        if (patientID != null) {
            List<Prescription> myPrescriptions = prescriptionDAO.getPrescriptionsByPatientId(patientID);
            model.addAttribute("myPrescriptions", myPrescriptions);
        }

        populateUserAttributes(model, currentUser);
        model.addAttribute("appointments", myAppointments);
        return "patient/patient_dashboard";
    }

    /*
     * Doctor dashboard
     */
    @GetMapping("/doctorDashboard")
    public String showDoctorDashboard(HttpSession session, Model model, HttpServletResponse response) {
        preventDashboardCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "DOCTOR")) {
            return "redirect:/login";
        }
        Doctor doctor = doctorDAO.getDoctorProfile(currentUser.getuserID());
        Employee employee = employeeDAO.getEmployeeByuserID(currentUser.getuserID());
        if (doctor == null || employee == null) {
            return "redirect:/login?error=doctorProfileNotFound";
        }
        List<AppointmentDTO> myAppointments = apptDAO.getAppointmentsByDoctor(currentUser.getuserID());
        populateUserAttributes(model, currentUser);
        model.addAttribute("doctor", doctor);
        model.addAttribute("employee", employee);
        model.addAttribute("myAppts", myAppointments);
        return "clinical/doctor_dashboard";
    }

    /*
     * Pharmacist dashboard
     */
    @GetMapping("/pharmacistDashboard")
    public String showPharmacistDashboard(HttpSession session, Model model, HttpServletResponse response) {
        preventDashboardCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "PHARMACIST")) {
            return "redirect:/login";
        }
        Employee employee = employeeDAO.getEmployeeByuserID(currentUser.getuserID());
        if (employee == null) {
            return "redirect:/login?error=employeeProfileNotFound";
        }

        List<Prescription> allOrders = prescriptionDAO.getAllPrescriptions();

        // Group orders by patientID
        Map<Integer, List<Prescription>> groupedOrders = (allOrders != null) ?
                allOrders.stream().collect(Collectors.groupingBy(Prescription::getPatientID))
                : Collections.emptyMap();

        populateUserAttributes(model, currentUser);
        model.addAttribute("employee", employee);
        model.addAttribute("groupedOrders", groupedOrders); // Pass grouped map to JSP

        return "pharmacy/pharmacist_dashboard";
    }
    /*
     * Hospital administrator dashboard
     */
    @GetMapping("/adminDashboard")
    public String showAdminDashboard(HttpSession session, Model model, HttpServletResponse response) {
        preventDashboardCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "HOSPITAL_ADMIN")) {
            return "redirect:/login";
        }
        Employee employee = employeeDAO.getEmployeeByuserID(currentUser.getuserID());
        if (employee == null) {
            return "redirect:/login?error=employeeProfileNotFound";
        }
        List<AppointmentDTO> allAppointments = apptDAO.getAllAppointments();
        populateUserAttributes(model, currentUser);
        model.addAttribute("employee", employee);
        model.addAttribute("adminApps", allAppointments);
        model.addAttribute("allFeedback", feedbackDAO.getAllFeedback());

        // Bind total count for the Employee Registry card on admin_dashboard.jsp
        model.addAttribute("employeeCount", employeeDAO.getEmployeeCount());

        return "admin/admin_dashboard";
    }

    /*
     * System administrator dashboard
     */
    @GetMapping("/systemAdminDashboard")
    public String showSystemAdminDashboard(HttpSession session, Model model, HttpServletResponse response) {
        preventDashboardCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "SYSTEM_ADMIN")) {
            return "redirect:/login";
        }
        Employee employee = employeeDAO.getEmployeeByuserID(currentUser.getuserID());
        if (employee == null) {
            return "redirect:/login?error=employeeProfileNotFound";
        }
        populateUserAttributes(model, currentUser);
        model.addAttribute("employee", employee);
        return "admin/system_admin_dashboard";
    }

    /*
     * Display profile update page
     */
    @GetMapping("/updateProfile")
    public String showUpdateProfile(HttpSession session, Model model, HttpServletResponse response) {
        preventDashboardCaching(response);
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        populateUserAttributes(model, currentUser);
        if (hasRole(currentUser, "DOCTOR")) {
            Doctor doctor = doctorDAO.getDoctorProfile(currentUser.getuserID());
            Employee employee = employeeDAO.getEmployeeByuserID(currentUser.getuserID());
            model.addAttribute("doctor", doctor);
            model.addAttribute("employee", employee);
        } else if (isEmployeeRole(currentUser.getRole())) {
            Employee employee = employeeDAO.getEmployeeByuserID(currentUser.getuserID());
            model.addAttribute("employee", employee);
        }
        return "updateProfilePage";
    }

    /*
     * Update patient, doctor, or general employee profile
     */
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam(required = false) String bloodGroup,
                                @RequestParam(required = false) String medicalHistory,
                                @RequestParam(required = false) String specialization,
                                @RequestParam(required = false, defaultValue = "0") int licenseID,
                                HttpSession session) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (hasRole(currentUser, "PATIENT")) {
            return updatePatientProfile(currentUser, bloodGroup, medicalHistory, session);
        }
        if (hasRole(currentUser, "DOCTOR")) {
            return updateDoctorProfile(currentUser, specialization, licenseID, session);
        }
        if (isEmployeeRole(currentUser.getRole())) {
            return redirectToDashboard(currentUser, null);
        }
        return redirectToDashboard(currentUser, "unsupportedProfileUpdate");
    }

    @PostMapping("/doctor/lab-requests/create")
    public String createLabRequest(@RequestParam("patientId") int patientId,
                                   @RequestParam("appointmentId") int appointmentId,
                                   @RequestParam("testType") String testType,
                                   @RequestParam("priority") String priority,
                                   @RequestParam("clinicalNotes") String clinicalNotes,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        User doctor = (User) session.getAttribute("currentUser");
        if (doctor == null || !"DOCTOR".equalsIgnoreCase(doctor.getRole())) {
            return "redirect:/login";
        }

        boolean created = labReportService.createDiagnosticRequest(
                patientId,
                doctor.getuserID(),
                appointmentId,
                testType,
                priority,
                clinicalNotes
        );

        if (created) {
            redirectAttributes.addFlashAttribute("msg", "Lab test order submitted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to submit lab test order.");
        }

        return "redirect:/doctorDashboard";
    }


    /*
     * Patient profile update helper
     */
    private String updatePatientProfile(User currentUser, String bloodGroup, String medicalHistory, HttpSession session) {
        if (bloodGroup == null || bloodGroup.isBlank()) {
            return "redirect:/patientDashboard?error=invalidBloodGroup";
        }
        String cleanedBloodGroup = bloodGroup.trim().toUpperCase();
        String cleanedMedicalHistory = medicalHistory == null ? "" : medicalHistory.trim();

        // Fixed: Passing exactly 3 arguments (userID, bloodGroup, medicalHistory)
        boolean success = patientDAO.updateProfile(currentUser.getuserID(), cleanedBloodGroup, cleanedMedicalHistory);

        if (!success) {
            return "redirect:/patientDashboard?error=profileUpdateFailed";
        }
        if (currentUser instanceof Patient patient) {
            patient.setBloodGroup(cleanedBloodGroup);
            patient.setMedicalHistory(cleanedMedicalHistory);
            updateSessionUser(session, patient);
        }
        return "redirect:/patientDashboard?updated=true";
    }

    /*
     * Doctor profile update helper
     */
    private String updateDoctorProfile(User currentUser, String specialization, int licenseID, HttpSession session) {
        if (specialization == null || specialization.isBlank()) {
            return "redirect:/doctorDashboard?error=invalidSpecialization";
        }
        if (licenseID <= 0) {
            return "redirect:/doctorDashboard?error=invalidLicense";
        }
        String cleanedSpecialization = specialization.trim();
        boolean success = doctorDAO.updateProfile(currentUser.getuserID(), cleanedSpecialization, licenseID);
        if (!success) {
            return "redirect:/doctorDashboard?error=profileUpdateFailed";
        }
        if (currentUser instanceof Doctor doctor) {
            doctor.setSpecialization(cleanedSpecialization);
            doctor.setLicenseID(licenseID);
            updateSessionUser(session, doctor);
        }
        return "redirect:/doctorDashboard?updated=true";
    }

    /*
     * Populate model with user attributes under both "user" and "currentUser" for JSP compatibility
     */
    private void populateUserAttributes(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);
    }

    /*
     * Retrieve logged-in user from the HTTP session safely checking both keys
     */
    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser instanceof User) {
            return (User) currentUser;
        }
        return null;
    }

    /*
     * Helper to update user object across all session keys
     */
    private void updateSessionUser(HttpSession session, User updatedUser) {
        if (session != null && updatedUser != null) {
            session.setAttribute("user", updatedUser);
            session.setAttribute("currentUser", updatedUser);
        }
    }

    /*
     * Check the user's role safely
     */
    private boolean hasRole(User user, String requiredRole) {
        return user != null && user.getRole() != null && requiredRole.equalsIgnoreCase(user.getRole());
    }

    /*
     * Identify roles that require an employee record
     */
    private boolean isEmployeeRole(String role) {
        if (role == null) {
            return false;
        }
        return "DOCTOR".equalsIgnoreCase(role) || "PHARMACIST".equalsIgnoreCase(role)
                || "LAB_TECHNICIAN".equalsIgnoreCase(role) || "HOSPITAL_ADMIN".equalsIgnoreCase(role)
                || "SYSTEM_ADMIN".equalsIgnoreCase(role);
    }

    /*
     * Redirect a user to the correct dashboard
     */
    private String redirectToDashboard(User user, String error) {
        String errorParameter = error == null || error.isBlank() ? "" : "?error=" + error;
        if (hasRole(user, "PATIENT")) {
            return "redirect:/patientDashboard" + errorParameter;
        }
        if (hasRole(user, "DOCTOR")) {
            return "redirect:/doctorDashboard" + errorParameter;
        }
        if (hasRole(user, "PHARMACIST")) {
            return "redirect:/pharmacistDashboard" + errorParameter;
        }
        if (hasRole(user, "LAB_TECHNICIAN")) {
            return "redirect://labDashboard" + errorParameter;
        }
        if (hasRole(user, "HOSPITAL_ADMIN")) {
            return "redirect:/adminDashboard" + errorParameter;
        }
        if (hasRole(user, "SYSTEM_ADMIN")) {
            return "redirect:/systemAdminDashboard" + errorParameter;
        }
        return "redirect:/login?error=invalidRole";
    }

    /*
     * Prevent dashboard access through the browser cache after logout
     */
    private void preventDashboardCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}