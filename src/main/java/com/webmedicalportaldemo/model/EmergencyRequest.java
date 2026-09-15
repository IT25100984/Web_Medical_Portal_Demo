package com.webmedicalportaldemo.model;

import java.sql.Timestamp;

public class EmergencyRequest {
    private int requestId;
    private String patientName;
    private String contactNumber;
    private String location;
    private String description;
    private String status; // PENDING, ASSIGNED, RESOLVED
    private Integer assignedDoctorId;
    private String assignedDoctorName; // For display purposes
    private Timestamp createdAt;
    private String priorityLevel;
    private String emergencyContact;
    private boolean requiresAmbulance;

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAssignedDoctorId() { return assignedDoctorId; }
    public void setAssignedDoctorId(Integer assignedDoctorId) { this.assignedDoctorId = assignedDoctorId; }

    public String getAssignedDoctorName() { return assignedDoctorName; }
    public void setAssignedDoctorName(String assignedDoctorName) { this.assignedDoctorName = assignedDoctorName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPriorityLevel() {
        return priorityLevel;
    }
    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public boolean isRequiresAmbulance() {
        return requiresAmbulance;
    }
    public void setRequiresAmbulance(boolean requiresAmbulance) {
        this.requiresAmbulance = requiresAmbulance;
    }
}