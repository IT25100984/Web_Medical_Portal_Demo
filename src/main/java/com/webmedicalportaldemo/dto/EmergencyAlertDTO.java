package com.webmedicalportaldemo.dto;

import java.sql.Timestamp;

public class EmergencyAlertDTO {
    private int requestId;
    private String patientName;
    private String priorityLevel; // CRITICAL, URGENT, STANDARD
    private String location;
    private boolean requiresAmbulance;
    private String status;
    private String timeElapsed; // E.g., "5 mins ago"

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isRequiresAmbulance() { return requiresAmbulance; }
    public void setRequiresAmbulance(boolean requiresAmbulance) { this.requiresAmbulance = requiresAmbulance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimeElapsed() { return timeElapsed; }
    public void setTimeElapsed(String timeElapsed) { this.timeElapsed = timeElapsed; }
}