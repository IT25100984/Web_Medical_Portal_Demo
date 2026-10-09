package com.webmedicalportaldemo.dto;

import java.time.LocalDateTime;

/**
 * Lightweight read model for reminders. Carries users.user_id for both parties
 * (AppointmentDTO only has doctor/patient entity IDs) and a real LocalDateTime.
 */
public record AppointmentReminderRow(
        int appointmentID,
        int doctorUserID,
        int patientUserID,
        String doctorName,
        String patientName,
        String appointmentType,
        LocalDateTime startsAt) {
}