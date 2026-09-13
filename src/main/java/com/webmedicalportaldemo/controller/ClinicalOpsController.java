package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.AppointmentDAO;
import com.webmedicalportaldemo.dao.DoctorDAO;
import com.webmedicalportaldemo.dao.HealthRecordDAO;
import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.dto.HealthRecordRequestDTO;
import com.webmedicalportaldemo.model.HealthRecord;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.EHRService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class ClinicalOpsController {
    private final EHRService ehrService;
    private final HealthRecordDAO healthRecordDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;
    private final AppointmentDAO appointmentDAO;

    public ClinicalOpsController(EHRService ehrService, HealthRecordDAO healthRecordDAO, PatientDAO patientDAO, DoctorDAO doctorDAO, AppointmentDAO appointmentDAO) {
        this.ehrService = ehrService;
        this.healthRecordDAO = healthRecordDAO;
        this.patientDAO = patientDAO;
        this.doctorDAO = doctorDAO;
        this.appointmentDAO = appointmentDAO;
    }

    /**
     * Displays health records for the logged-in patient or a patient selected by a doctor.
     */
    @GetMapping("/clinical/ehr")
    public String showHealthRecords(@RequestParam(value = "patientID", required = false) Integer patientID, HttpSession session, Model model, HttpServletResponse response) {
        preventCaching(response);
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        List<HealthRecord> healthRecords;
        if (hasRole(currentUser, "PATIENT")) {
            Integer currentPatientID = patientDAO.getPatientIDByuserID(currentUser.getuserID());
            if (currentPatientID == null || currentPatientID <= 0) {
                return "redirect:/patientDashboard?error=patientProfileNotFound";
            }
            healthRecords = ehrService.getLoggedInPatientHealthRecords(currentUser.getuserID());
            model.addAttribute("selectedPatientID", currentPatientID);
        } else if (hasRole(currentUser, "DOCTOR")) {
            Integer doctorID = doctorDAO.getDoctorIDByuserID(currentUser.getuserID());
            if (doctorID == null || doctorID <= 0) {
                return "redirect:/doctorDashboard?error=doctorProfileNotFound";
            }
            if (patientID == null || patientID <= 0) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("healthRecords", Collections.emptyList());
                model.addAttribute("errorMessage", "Select a valid patient to view health records.");
                return "clinical/ehr_viewer";
            }
            healthRecords = ehrService.getPatientHealthRecords(patientID);
            model.addAttribute("selectedPatientID", patientID);
        } else {
            return redirectByRole(currentUser);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("healthRecords", healthRecords);
        return "clinical/ehr_viewer";
    }

    @GetMapping("/clinical/ehr/detail")
    public String viewEHRDetail(@RequestParam(value = "id", required = false) Integer recordId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (recordId == null || recordId == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Health Record ID.");
            return "redirect:/doctor/dashboard";
        }

        HealthRecord record = healthRecordDAO.getHealthRecordById(recordId);
        model.addAttribute("record", record);
        return "clinical/ehr_details";
    }

    /**
     * Displays the health-record creation form for doctors.
     */
    @GetMapping("/clinical/ehr/create")
    public String showCreateHealthRecordForm(@RequestParam(value = "patientID", required = false) Integer patientID, @RequestParam(value = "appointmentID", required = false) Integer appointmentID, HttpSession session, Model model, HttpServletResponse response) {
        preventCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "DOCTOR")) {
            return currentUser == null ? "redirect:/login" : redirectByRole(currentUser);
        }
        Integer doctorID = doctorDAO.getDoctorIDByuserID(currentUser.getuserID());
        if (doctorID == null || doctorID <= 0) {
            return "redirect:/doctorDashboard?error=doctorProfileNotFound";
        }
        HealthRecordRequestDTO request = new HealthRecordRequestDTO();
        if (patientID != null && patientID > 0) {
            request.setPatientID(patientID);
        }
        if (appointmentID != null && appointmentID > 0) {
            request.setAppointmentID(appointmentID);
        }
        request.setRecordType("CONSULTATION");

        populateFormDropdowns(model);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("healthRecordRequest", request);
        model.addAttribute("formMode", "create");
        return "clinical/ehr_form";
    }

    @PostMapping("/clinical/ehr/save")
    public String saveEHR(@RequestParam("patientId") int patientId,
                          @ModelAttribute("healthRecord") HealthRecord record,
                          RedirectAttributes redirectAttributes) {
        try {
            // Assign patientId explicitly before saving
            record.setPatientID(patientId);
            healthRecordDAO.saveRecord(record);

            // Pass the valid patientId in redirect
            return "redirect:/clinical/ehr/view?patientId=" + patientId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "The requested operation could not be completed.");
            return "redirect:/doctor/dashboard";
        }
    }

    /**
     * Creates a health record using the logged-in doctor's identity.
     */
    @PostMapping("/clinical/ehr/create")
    public String createHealthRecord(@Valid @ModelAttribute("healthRecordRequest") HealthRecordRequestDTO request, BindingResult bindingResult, HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "DOCTOR")) {
            return currentUser == null ? "redirect:/login" : redirectByRole(currentUser);
        }
        Integer doctorID = doctorDAO.getDoctorIDByuserID(currentUser.getuserID());
        if (doctorID == null || doctorID <= 0) {
            return "redirect:/doctorDashboard?error=doctorProfileNotFound";
        }
        if (bindingResult.hasErrors()) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("formMode", "create");
            return "clinical/ehr_form";
        }
        if (request.getPatientID() == null || request.getPatientID() <= 0) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "A valid patient must be selected.");
            return "clinical/ehr_form";
        }
        if (!isSupportedRecordType(request.getRecordType())) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "The selected health-record type is invalid.");
            return "clinical/ehr_form";
        }
        request.setRecordType(request.getRecordType().trim().toUpperCase(Locale.ROOT));
        int generatedRecordID = ehrService.createHealthRecord(request, currentUser.getuserID());
        if (generatedRecordID <= 0) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "The health record could not be created.");
            return "clinical/ehr_form";
        }
        return "redirect:/clinical/ehr/" + generatedRecordID + "?msg=created";
    }

    /**
     * Displays one health record after checking access permissions.
     */
    @GetMapping("/clinical/ehr/{healthRecordID}")
    public String showHealthRecordDetails(@PathVariable int healthRecordID, HttpSession session, Model model, HttpServletResponse response) {
        preventCaching(response);
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (healthRecordID <= 0) {
            return redirectByRoleWithError(currentUser, "invalidRecordID");
        }
        HealthRecord healthRecord = ehrService.getHealthRecordById(healthRecordID);
        if (healthRecord == null) {
            return redirectByRoleWithError(currentUser, "recordNotFound");
        }
        if (!canViewHealthRecord(currentUser, healthRecord)) {
            return redirectByRoleWithError(currentUser, "accessDenied");
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("healthRecord", healthRecord);
        model.addAttribute("canEdit", canEditHealthRecord(currentUser, healthRecord));
        return "clinical/ehr_details";
    }

    /**
     * Displays the health-record editing form for the doctor associated with the record.
     */
    @GetMapping("/clinical/ehr/{healthRecordID}/edit")
    public String showEditHealthRecordForm(@PathVariable int healthRecordID, HttpSession session, Model model, HttpServletResponse response) {
        preventCaching(response);
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "DOCTOR")) {
            return currentUser == null ? "redirect:/login" : redirectByRole(currentUser);
        }
        if (healthRecordID <= 0) {
            return "redirect:/doctorDashboard?error=invalidRecordID";
        }
        HealthRecord healthRecord = ehrService.getHealthRecordById(healthRecordID);
        if (healthRecord == null) {
            return "redirect:/doctorDashboard?error=recordNotFound";
        }
        if (!canEditHealthRecord(currentUser, healthRecord)) {
            return "redirect:/clinical/ehr/" + healthRecordID + "?error=accessDenied";
        }
        HealthRecordRequestDTO request = convertToRequestDTO(healthRecord);
        populateFormDropdowns(model);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("healthRecord", healthRecord);
        model.addAttribute("healthRecordRequest", request);
        model.addAttribute("formMode", "edit");
        return "clinical/ehr_form";
    }

    /**
     * Updates a health record after validating doctor ownership.
     */
    @PostMapping("/clinical/ehr/{healthRecordID}/edit")
    public String updateHealthRecord(@PathVariable int healthRecordID, @Valid @ModelAttribute("healthRecordRequest") HealthRecordRequestDTO request, BindingResult bindingResult, HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (!hasRole(currentUser, "DOCTOR")) {
            return currentUser == null ? "redirect:/login" : redirectByRole(currentUser);
        }
        if (healthRecordID <= 0) {
            return "redirect:/doctorDashboard?error=invalidRecordID";
        }
        HealthRecord existingRecord = ehrService.getHealthRecordById(healthRecordID);
        if (existingRecord == null) {
            return "redirect:/doctorDashboard?error=recordNotFound";
        }
        if (!canEditHealthRecord(currentUser, existingRecord)) {
            return "redirect:/clinical/ehr/" + healthRecordID + "?error=accessDenied";
        }
        if (bindingResult.hasErrors()) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("healthRecord", existingRecord);
            model.addAttribute("formMode", "edit");
            return "clinical/ehr_form";
        }
        if (!isSupportedRecordType(request.getRecordType())) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("healthRecord", existingRecord);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "The selected health-record type is invalid.");
            return "clinical/ehr_form";
        }
        request.setPatientID(existingRecord.getPatientID());
        request.setAppointmentID(existingRecord.getAppointmentID());
        request.setRecordType(request.getRecordType().trim().toUpperCase(Locale.ROOT));
        boolean updated = ehrService.updateHealthRecord(healthRecordID, request, currentUser.getuserID());
        if (!updated) {
            populateFormDropdowns(model);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("healthRecord", existingRecord);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "The health record could not be updated.");
            return "clinical/ehr_form";
        }
        return "redirect:/clinical/ehr/" + healthRecordID + "?msg=updated";
    }

    /**
     * Populates patient and appointment lists required for JSP dropdowns.
     */
    private void populateFormDropdowns(Model model) {
        model.addAttribute("patientList", patientDAO.getAllPatients());
        model.addAttribute("appointmentList", appointmentDAO.getAllAppointments());
    }

    /**
     * Checks whether the current user may view a health record.
     */
    private boolean canViewHealthRecord(User currentUser, HealthRecord healthRecord) {
        if (currentUser == null || healthRecord == null) {
            return false;
        }
        if (hasRole(currentUser, "PATIENT")) {
            Integer patientID = patientDAO.getPatientIDByuserID(currentUser.getuserID());
            return patientID != null && patientID == healthRecord.getPatientID();
        }
        if (hasRole(currentUser, "DOCTOR")) {
            Integer doctorID = doctorDAO.getDoctorIDByuserID(currentUser.getuserID());
            return doctorID != null
                    && doctorID > 0
                    && appointmentDAO.doctorHasAccessToPatient(
                    doctorID,
                    healthRecord.getPatientID()
            );
        }
        return false;
    }

    /**
     * Checks whether the logged-in doctor created and may edit the record.
     */
    private boolean canEditHealthRecord(User currentUser, HealthRecord healthRecord) {
        if (!hasRole(currentUser, "DOCTOR") || healthRecord == null) {
            return false;
        }
        Integer doctorID = doctorDAO.getDoctorIDByuserID(currentUser.getuserID());
        return doctorID != null && healthRecord.getDoctorID() != null && doctorID.equals(healthRecord.getDoctorID());
    }

    /**
     * Converts an existing record into an editable request DTO.
     */
    private HealthRecordRequestDTO convertToRequestDTO(HealthRecord healthRecord) {
        HealthRecordRequestDTO request = new HealthRecordRequestDTO();
        request.setPatientID(healthRecord.getPatientID());
        request.setAppointmentID(healthRecord.getAppointmentID());
        request.setRecordType(healthRecord.getRecordType());
        request.setDiagnosis(healthRecord.getDiagnosis());
        request.setSymptoms(healthRecord.getSymptoms());
        request.setTreatmentPlan(healthRecord.getTreatmentPlan());
        request.setClinicalNotes(healthRecord.getClinicalNotes());
        request.setAllergies(healthRecord.getAllergies());
        request.setMedications(healthRecord.getMedications());
        request.setFollowUpInstructions(healthRecord.getFollowUpInstructions());
        return request;
    }

    /**
     * Validates health-record types against the MySQL ENUM values.
     */
    private boolean isSupportedRecordType(String recordType) {
        if (recordType == null || recordType.isBlank()) {
            return false;
        }
        return switch (recordType.trim().toUpperCase(Locale.ROOT)) {
            case "CONSULTATION", "DIAGNOSIS", "TREATMENT", "FOLLOW_UP", "EMERGENCY", "SURGERY", "LAB_RESULT", "GENERAL_NOTE" -> true;
            default -> false;
        };
    }

    /**
     * Retrieves the logged-in user safely.
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
     * Redirects a user to the appropriate dashboard.
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
     * Redirects a user to the appropriate dashboard with an error parameter.
     */
    private String redirectByRoleWithError(User user, String error) {
        String redirect = redirectByRole(user);
        if (error == null || error.isBlank()) {
            return redirect;
        }
        return redirect.contains("?") ? redirect + "&error=" + error : redirect + "?error=" + error;
    }

    /**
     * Prevents clinical pages from being stored in the browser cache.
     */
    private void preventCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}