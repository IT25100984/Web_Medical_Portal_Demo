package com.webmedicalportaldemo.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LabReport {
    private int requestId;
    private int patientId;
    private int doctorId;
    private int appointmentId;
    private String testName;
    private String category;
    private String priority;
    private String clinicalNotes;
    private String status;
    private String sampleStatus;
    private String resultsSummary;
    private String filePath;
    private Timestamp requestedDate;
    private Timestamp completedDate;

    // Optional joined fields for UI display
    private String patientName;
    private String doctorName;

    public LabReport() {
    }

    public LabReport(int requestId, int patientId, int doctorId, int appointmentId, String testName,
                     String category, String priority, String clinicalNotes, String status,
                     String sampleStatus, String resultsSummary, String filePath,
                     Timestamp requestedDate, Timestamp completedDate) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.testName = testName;
        this.category = category;
        this.priority = priority;
        this.clinicalNotes = clinicalNotes;
        this.status = status;
        this.sampleStatus = sampleStatus;
        this.resultsSummary = resultsSummary;
        this.filePath = filePath;
        this.requestedDate = requestedDate;
        this.completedDate = completedDate;
    }

    // --- Getters & Setters ---

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    // Alias methods for testType <-> testName mapping
    public String getTestType() {
        return testName;
    }

    public void setTestType(String testType) {
        this.testName = testType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSampleStatus() {
        return sampleStatus;
    }

    public void setSampleStatus(String sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    public String getResultsSummary() {
        return resultsSummary;
    }

    public void setResultsSummary(String resultsSummary) {
        this.resultsSummary = resultsSummary;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Timestamp requestedDate) {
        this.requestedDate = requestedDate;
    }

    // LocalDateTime overload for service layer compatibility
    public void setRequestedAt(LocalDateTime requestedAt) {
        if (requestedAt != null) {
            this.requestedDate = Timestamp.valueOf(requestedAt);
        }
    }

    public Timestamp getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Timestamp completedDate) {
        this.completedDate = completedDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}