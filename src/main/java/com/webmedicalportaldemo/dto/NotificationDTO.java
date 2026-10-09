package com.webmedicalportaldemo.dto;

public record NotificationDTO(int notificationID, int appointmentID, String reminderType, String message) {
}