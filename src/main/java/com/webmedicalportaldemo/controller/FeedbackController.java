package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.DoctorDAO;
import com.webmedicalportaldemo.dao.FeedbackDAO;
import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.model.Feedback;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.FeedbackFileService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FeedbackController {

    private final FeedbackDAO feedbackDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;
    private final FeedbackFileService feedbackFileService;

    public FeedbackController(FeedbackDAO feedbackDAO,
                              DoctorDAO doctorDAO,
                              PatientDAO patientDAO,
                              FeedbackFileService feedbackFileService) {

        this.feedbackDAO = feedbackDAO;
        this.doctorDAO = doctorDAO;
        this.patientDAO = patientDAO;
        this.feedbackFileService = feedbackFileService;
    }

    /**
     * Displays feedback pages based on the logged-in user's role.
     */
    @GetMapping("/feedback")
    public String feedbackPage(
            @RequestParam(value = "doctorID", required = false) Integer doctorID,
            @RequestParam(value = "appointmentID", required = false) Integer appointmentID,
            HttpSession session, Model model) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        /*
         * Patient feedback form
         */
        if (hasRole(currentUser, "PATIENT")) {
            model.addAttribute("selectedDoctorID", doctorID);
            model.addAttribute("selectedAppointmentID", appointmentID);
            model.addAttribute("doctors", doctorDAO.getAllDoctors());
            model.addAttribute("myFeedback", feedbackDAO.getFeedbackByPatient(currentUser.getuserID()));
            return "shared/feedback_form";
        }

        /*
         * Doctor feedback view
         */
        if (hasRole(currentUser, "DOCTOR")) {
            List<Feedback> reviews = feedbackDAO.getFeedbackForDoctor(currentUser.getuserID());
            double averageRating = feedbackDAO.getAverageRating(currentUser.getuserID());

            model.addAttribute("reviews", reviews);
            model.addAttribute("avgRating", averageRating);
            return "clinical/doctor_feedback";
        }

        /*
         * Hospital administrator feedback view
         */
        if (hasRole(currentUser, "HOSPITAL_ADMIN")) {
            model.addAttribute("allFeedback", feedbackDAO.getAllFeedback());
            return "admin/admin_feedback_modal";
        }

        return redirectToDashboard(currentUser);
    }

    /**
     * Allows a patient to submit feedback.
     */
    @PostMapping("/submitFeedback")
    public String submitFeedback(
            @RequestParam(value = "doctorID", required = false) Integer doctorID,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "appointmentID", required = false) Integer appointmentID,
            HttpSession session, Model model) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!hasRole(currentUser, "PATIENT")) {
            return redirectToDashboard(currentUser);
        }

        /* 1. Resolve patient_id strictly */
        Integer patientID = patientDAO.getPatientIDByuserID(currentUser.getuserID());
        if (patientID == null || patientID <= 0) {
            return showPatientFeedbackError(currentUser, doctorID, appointmentID,
                    "Patient profile not found. Please contact support.", model
            );
        }

        /* 2. Validate rating */
        if (rating == null || rating < 1 || rating > 5) {
            return showPatientFeedbackError(currentUser, doctorID, appointmentID,
                    "Rating must be between 1 and 5.", model
            );
        }

        String cleanedComment = comment == null ? "" : comment.trim();
        if (cleanedComment.length() > 2000) {
            return showPatientFeedbackError(currentUser, doctorID, appointmentID,
                    "The feedback comment must not exceed 2000 characters.", model
            );
        }

        /* 3. Pass NULL (not 0) for optional foreign keys */
        Integer safeDoctorID = (doctorID != null && doctorID > 0) ? doctorID : null;
        Integer safeAppointmentID = (appointmentID != null && appointmentID > 0) ? appointmentID : null;

        Integer newFeedbackId = feedbackDAO.submitFeedback(
                patientID,
                safeDoctorID,
                safeAppointmentID,
                rating,
                cleanedComment
        );

        if (newFeedbackId == null || newFeedbackId <= 0) {
            return showPatientFeedbackError(currentUser, doctorID, appointmentID,
                    "The database could not save the feedback.", model
            );
        }

        /* 4. Secondary file synchronization */
        try {
            String patientName = currentUser.getFullName();
            String doctorName = (safeDoctorID != null) ? feedbackDAO.getUserFullName(safeDoctorID) : "General Platform";
            feedbackFileService.writeFeedbackToFile(newFeedbackId, patientName, doctorName, rating, cleanedComment);
        } catch (Exception e) {
            System.err.println("Feedback ID " + newFeedbackId + " saved to DB, but file sync failed: " + e.getMessage());
        }

        return "redirect:/patientDashboard?msg=review_success";
    }

    /**
     * Allows hospital administrator to delete feedback.
     */
    @PostMapping("/deleteFeedback")
    public String deleteFeedback(
            @RequestParam("feedbackId") Integer feedbackId, HttpSession session) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!hasRole(currentUser, "HOSPITAL_ADMIN")) {
            return redirectToDashboard(currentUser);
        }
        if (feedbackId == null || feedbackId <= 0) {
            return "redirect:/adminDashboard?msg=invalid_feedback_id";
        }

        try {
            boolean deletedFromDatabase = feedbackDAO.deleteFeedbackById(feedbackId);
            if (!deletedFromDatabase) {
                return "redirect:/adminDashboard?msg=delete_failed";
            }

            try {
                feedbackFileService.deleteFeedbackFromFile(feedbackId);
            } catch (Exception fileException) {
                System.err.println("Feedback ID " + feedbackId + " deleted from DB but file removal failed: " + fileException.getMessage());
            }

            return "redirect:/adminDashboard?msg=delete_success";
        } catch (Exception e) {
            return "redirect:/adminDashboard?msg=delete_failed";
        }
    }

    private String showPatientFeedbackError(User currentUser, Integer doctorID,
                                            Integer appointmentID, String errorMessage, Model model) {

        model.addAttribute("error", errorMessage);
        model.addAttribute("selectedDoctorID", doctorID);
        model.addAttribute("selectedAppointmentID", appointmentID);
        model.addAttribute("doctors", doctorDAO.getAllDoctors());
        model.addAttribute("myFeedback", feedbackDAO.getFeedbackByPatient(currentUser.getuserID()));
        return "shared/feedback_form";
    }

    private User getCurrentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("user");
        return (sessionUser instanceof User) ? (User) sessionUser : null;
    }

    private boolean hasRole(User user, String requiredRole) {
        return user != null && user.getRole() != null && requiredRole.equalsIgnoreCase(user.getRole());
    }

    private String redirectToDashboard(User user) {
        if (hasRole(user, "PATIENT")) return "redirect:/patientDashboard";
        if (hasRole(user, "DOCTOR")) return "redirect:/doctorDashboard";
        if (hasRole(user, "PHARMACIST")) return "redirect:/pharmacistDashboard";
        if (hasRole(user, "HOSPITAL_ADMIN")) return "redirect:/adminDashboard";
        return "redirect:/login";
    }
}