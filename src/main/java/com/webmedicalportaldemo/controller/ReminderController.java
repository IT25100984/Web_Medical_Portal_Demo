package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.NotificationDAO;
import com.webmedicalportaldemo.dto.ReminderSummaryDTO;
import com.webmedicalportaldemo.model.User;
import com.webmedicalportaldemo.service.AppointmentReminderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final AppointmentReminderService reminderService;
    private final NotificationDAO notificationDAO;

    public ReminderController(AppointmentReminderService reminderService, NotificationDAO notificationDAO) {
        this.reminderService = reminderService;
        this.notificationDAO = notificationDAO;
    }

    /** Summary + unread reminders for the logged-in doctor or patient. */
    @GetMapping
    public ResponseEntity<ReminderSummaryDTO> getReminders(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        boolean isDoctor = hasRole(user, "DOCTOR");
        if (!isDoctor && !hasRole(user, "PATIENT")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(reminderService.buildSummary(user.getUserID(), isDoctor));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable("id") int notificationID, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return notificationDAO.markRead(notificationID, user.getUserID())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        notificationDAO.markAllRead(user.getUserID());
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object sessionUser = session.getAttribute("user");
        return sessionUser instanceof User ? (User) sessionUser : null;
    }

    private boolean hasRole(User user, String requiredRole) {
        return user.getRole() != null && requiredRole.equalsIgnoreCase(user.getRole());
    }
}