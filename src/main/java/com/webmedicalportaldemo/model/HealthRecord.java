package com.webmedicalportaldemo.model;
import java.time.LocalDateTime;

public class HealthRecord {
    private int healthRecordID;
    private int patientID;
    private Integer doctorID;
    private Integer appointmentID;
    private String recordType;
    private String diagnosis;
    private String symptoms;
    private String treatmentPlan;
    private String clinicalNotes;
    private String allergies;
    private String medications;
    private String followUpInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HealthRecord() {
        this.recordType = "CONSULTATION";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public HealthRecord(int patientID, Integer doctorID, Integer appointmentID, String recordType, String diagnosis, String symptoms, String treatmentPlan, String clinicalNotes, String allergies, String medications, String followUpInstructions) {
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.appointmentID = appointmentID;
        this.recordType = recordType == null || recordType.isBlank() ? "CONSULTATION" : recordType;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatmentPlan = treatmentPlan;
        this.clinicalNotes = clinicalNotes;
        this.allergies = allergies;
        this.medications = medications;
        this.followUpInstructions = followUpInstructions;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public HealthRecord(int healthRecordID, int patientID, Integer doctorID, Integer appointmentID, String recordType, String diagnosis, String symptoms, String treatmentPlan, String clinicalNotes, String allergies, String medications, String followUpInstructions, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.healthRecordID = healthRecordID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.appointmentID = appointmentID;
        this.recordType = recordType;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatmentPlan = treatmentPlan;
        this.clinicalNotes = clinicalNotes;
        this.allergies = allergies;
        this.medications = medications;
        this.followUpInstructions = followUpInstructions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateHealthRecord(String recordType, String diagnosis, String symptoms, String treatmentPlan, String clinicalNotes, String allergies, String medications, String followUpInstructions) {
        if (recordType != null && !recordType.isBlank()) {
            this.recordType = recordType;
        }
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatmentPlan = treatmentPlan;
        this.clinicalNotes = clinicalNotes;
        this.allergies = allergies;
        this.medications = medications;
        this.followUpInstructions = followUpInstructions;
        this.updatedAt = LocalDateTime.now();
    }

    public int getHealthRecordID() {
        return healthRecordID;
    }
    public void setHealthRecordID(int healthRecordID) {
        this.healthRecordID = healthRecordID;
    }
    public int getPatientID() {
        return patientID;
    }
    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
    public Integer getDoctorID() {
        return doctorID;
    }
    public void setDoctorID(Integer doctorID) {
        this.doctorID = doctorID;
    }
    public Integer getAppointmentID() {
        return appointmentID;
    }
    public void setAppointmentID(Integer appointmentID) {
        this.appointmentID = appointmentID;
    }
    public String getRecordType() {
        return recordType;
    }
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    public String getSymptoms() {
        return symptoms;
    }
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    public String getTreatmentPlan() {
        return treatmentPlan;
    }
    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }
    public String getClinicalNotes() {
        return clinicalNotes;
    }
    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }
    public String getAllergies() {
        return allergies;
    }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    public String getMedications() {
        return medications;
    }
    public void setMedications(String medications) {
        this.medications = medications;
    }
    public String getFollowUpInstructions() {
        return followUpInstructions;
    }
    public void setFollowUpInstructions(String followUpInstructions) {
        this.followUpInstructions = followUpInstructions;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "HealthRecord{" +
                "healthRecordID=" + healthRecordID +
                ", patientID=" + patientID +
                ", doctorID=" + doctorID +
                ", appointmentID=" + appointmentID +
                ", recordType='" + recordType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}