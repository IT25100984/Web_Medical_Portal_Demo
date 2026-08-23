package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.DoctorDAO;
import com.webmedicalportaldemo.dao.FeedbackDAO;
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
    private final FeedbackFileService feedbackFileService;

    public FeedbackController(FeedbackDAO feedbackDAO,
                              DoctorDAO doctorDAO,
                              FeedbackFileService feedbackFileService) {

        this.feedbackDAO = feedbackDAO;
        this.doctorDAO = doctorDAO;
        this.feedbackFileService = feedbackFileService;
    }

    /**
     * Displays feedback pages based on the logged-in user's role.
     */
    @GetMapping("/feedback")
    public String feedbackPage(
            @RequestParam(value = "doctorId", required = false) Integer doctorId,
            @RequestParam(value = "appointmentId", required = false) Integer appointmentId,
            HttpSession session, Model model) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        /*
         * Patient feedback form
         */
        if (hasRole(currentUser, "PATIENT")) {
            model.addAttribute("selectedDoctorId", doctorId);
            model.addAttribute("selectedAppointmentId", appointmentId);
            model.addAttribute("doctors", doctorDAO.getAllDoctors());
            model.addAttribute("myFeedback", feedbackDAO.getFeedbackByPatient(currentUser.getUserID()));
            return "shared/feedback_form";
        }
        /*
         * Doctor feedback view
         */
        if (hasRole(currentUser, "DOCTOR")) {
            /*
             * This assumes FeedbackDAO resolves the user's ID
             * to the corresponding doctors.doctor_id.
             *
             * See the database ID note below.
             */
            List<Feedback> reviews = feedbackDAO.getFeedbackForDoctor(currentUser.getUserID());

            double averageRating = feedbackDAO.getAverageRating(currentUser.getUserID());

            model.addAttribute("reviews", reviews);
            model.addAttribute("avgRating", averageRating);
            /*
             * Use this path if the JSP is stored at:
             *
             * WEB-INF/jsp/clinical/doctor_feedback.jsp
             */
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
            @RequestParam(value = "doctorId", required = false) Integer doctorId,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "appointmentId", required = false) Integer appointmentId,
            HttpSession session, Model model) {

        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        /*
         * Only patients should submit feedback.
         */
        if (!hasRole(currentUser, "PATIENT")) {
            return redirectToDashboard(currentUser);
        }
        /*
         * Validate all required identifiers.
         *
         * If every feedback record must relate to an appointment,
         * appointmentId must also be required.
         */
        if (doctorId == null || doctorId <= 0 ||
                appointmentId == null || appointmentId <= 0) {
            return showPatientFeedbackError(currentUser, doctorId, appointmentId,
                    "A valid doctor and appointment are required.", model
            );
        }

        /*
         * The database constraint requires a value from 1 to 5.
         */
        if (rating == null || rating < 1 || rating > 5) {
            return showPatientFeedbackError(currentUser, doctorId,
                    appointmentId, "Rating must be between 1 and 5.", model
            );
        }

        String cleanedComment = comment == null ? "" : comment.trim();
        /*
         * Prevent unnecessarily large feedback comments.
         * This value may be changed to match the JSP and database.
         */
        if (cleanedComment.length() > 2000) {
            return showPatientFeedbackError(currentUser, doctorId, appointmentId,
                    "The feedback comment must not exceed 2000 characters.", model
            );
        }
        /*
         * Important:
         *
         * currentUser.getUserID() is users.user_id.
         *
         * If feedback.patient_id references patients.patient_id,
         * FeedbackDAO must convert users.user_id into patient_id
         * before inserting the feedback record.
         */
        Integer newFeedbackId = feedbackDAO.submitFeedback(currentUser.getUserID(),
                doctorId, appointmentId, rating, cleanedComment);

        if (newFeedbackId == null || newFeedbackId <= 0) {
            return showPatientFeedbackError(currentUser, doctorId, appointmentId,
                    "The database could not save the feedback.", model
            );
        }

        /*
         * Temporary file synchronization.
         *
         * The database insert remains successful even if writing
         * to the secondary text file fails.
         */
        try {
            String patientName = currentUser.getFullName();
            String doctorName = feedbackDAO.getUserFullName(doctorId);
            feedbackFileService.writeFeedbackToFile(newFeedbackId, patientName,
                    doctorName, rating, cleanedComment
            );
            System.out.println("Feedback saved to MySQL and feedback.txt. "
                            + "Feedback ID: " + newFeedbackId
            );

        } catch (Exception e) {

            System.err.println("Feedback ID " + newFeedbackId + " was saved to MySQL, but file "
                    + "synchronization failed: " + e.getMessage()
            );
        }
        return "redirect:/patientDashboard" + "?msg=review_success";
    }
    /**
     * Allows only the hospital administrator to delete feedback.
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
            return "redirect:/adminDashboard" + "?msg=invalid_feedback_id";
        }
        try {
            boolean deletedFromDatabase = feedbackDAO.deleteFeedbackById(feedbackId);
            if (!deletedFromDatabase) {return "redirect:/adminDashboard" + "?msg=delete_failed";}
            /*
             * Delete the corresponding file record only after
             * successfully deleting the database record.
             */
            try {
                feedbackFileService.deleteFeedbackFromFile(feedbackId);
            } catch (Exception fileException) {
                System.err.println("Feedback ID " + feedbackId
                                + " was deleted from MySQL, " + "but could not be removed from "
                                + "feedback.txt: " + fileException.getMessage()
                );
                return "redirect:/adminDashboard" + "?msg=db_deleted_file_failed";
            }
            return "redirect:/adminDashboard" + "?msg=delete_success";
        } catch (Exception e) {
            System.err.println("Feedback deletion failed for ID " + feedbackId
                            + ": " + e.getMessage());
            return "redirect:/adminDashboard" + "?msg=delete_failed";
        }
    }
    /**
     * Rebuilds the patient feedback form after validation fails.
     */
    private String showPatientFeedbackError(User currentUser, Integer doctorId,
            Integer appointmentId, String errorMessage, Model model) {

        model.addAttribute("error", errorMessage);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedAppointmentId", appointmentId);
        model.addAttribute("doctors", doctorDAO.getAllDoctors());
        model.addAttribute("myFeedback", feedbackDAO.getFeedbackByPatient(currentUser.getUserID()));
        return "shared/feedback_form";
    }
    /**
     * Safely retrieves the logged-in user.
     */
    private User getCurrentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("user");

        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }
        return null;
    }
    /**
     * Performs a null-safe role comparison.
     */
    private boolean hasRole(User user, String requiredRole) {
        return user != null && user.getRole() != null
                && requiredRole.equalsIgnoreCase(user.getRole()
        );
    }
    /**
     * Redirects each role to the correct dashboard.
     */
    private String redirectToDashboard(User user) {
        if (hasRole(user, "PATIENT")) {
            return "redirect:/patientDashboard";
        }
        if (hasRole(user, "DOCTOR")) {
            return "redirect:/doctorDashboard";
        }
        if (hasRole(user, "PHARMACIST")) {
            return "redirect:/pharmacistDashboard";
        }
        if (hasRole(user, "HOSPITAL_ADMIN")) {
            return "redirect:/adminDashboard";
        }
        return "redirect:/login";
    }
}