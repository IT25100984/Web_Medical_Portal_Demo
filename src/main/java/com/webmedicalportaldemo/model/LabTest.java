package com.webmedicalportaldemo.model;

import java.sql.Timestamp;

public class LabTest {
    private int requestID;
    private int patientID;
    private int doctorID;
    private String testName;
    private String category;
    private String status;
    private String sampleStatus;
    private String resultsSummary;
    private String filePath;
    private Timestamp requestedDate;
    private Timestamp completedDate;

    // Optional joined fields for UI display
    private String patientName;
    private String doctorName;

    public LabTest() {
    }

    public LabTest(int requestID, int patientID, int doctorID, String testName, String category,
                   String status, String sampleStatus, String resultsSummary, String filePath,
                   Timestamp requestedDate, Timestamp completedDate) {
        this.requestID = requestID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.testName = testName;
        this.category = category;
        this.status = status;
        this.sampleStatus = sampleStatus;
        this.resultsSummary = resultsSummary;
        this.filePath = filePath;
        this.requestedDate = requestedDate;
        this.completedDate = completedDate;
    }

    // Getters and Setters
    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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