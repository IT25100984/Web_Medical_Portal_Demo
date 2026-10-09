package com.webmedicalportaldemo.dto;

import java.util.List;

/** JSON payload consumed by shared/reminder_banner.jsp. */
public record ReminderSummaryDTO(
        List<NotificationDTO> notifications,
        NextAppointment next,          // null when nothing is upcoming
        int upcomingCount,
        int todayCount) {

    public record NextAppointment(String date, String time, String type, String withName, long minutesAway) {
    }
}