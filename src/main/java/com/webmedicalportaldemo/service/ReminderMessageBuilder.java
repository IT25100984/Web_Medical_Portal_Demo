package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.dto.AppointmentReminderRow;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Builds the text shown in reminders (named to avoid clashing with any existing NotificationBuilder). */
@Component
public class ReminderMessageBuilder {

    public enum ReminderType {
        DAY_BEFORE("24H"),
        HOUR_BEFORE("1H");

        private final String code;

        ReminderType(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    public String build(AppointmentReminderRow row, boolean recipientIsDoctor, ReminderType type) {
        String who = counterpartName(row, recipientIsDoctor);
        String what = formatType(row.appointmentType());
        String time = row.startsAt().toLocalTime().format(TIME);
        String with = recipientIsDoctor ? "with patient " + who : "with " + who;

        return switch (type) {
            case HOUR_BEFORE -> "Starting within the hour: " + what + " " + with + " at " + time + ".";
            case DAY_BEFORE -> "Upcoming: " + what + " " + with + " on " + row.startsAt().toLocalDate() + " at " + time + ".";
        };
    }

    /** The other party's display name from the recipient's point of view. */
    public String counterpartName(AppointmentReminderRow row, boolean recipientIsDoctor) {
        return recipientIsDoctor ? row.patientName() : "Dr. " + row.doctorName();
    }

    public String formatType(String appointmentType) {
        if (appointmentType == null || appointmentType.isBlank()) {
            return "appointment";
        }
        return appointmentType.trim().toLowerCase(Locale.ROOT).replace('_', ' ') + " appointment";
    }
}