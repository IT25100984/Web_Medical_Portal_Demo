package com.webmedicalportaldemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class HealthRecordRequestDTO {

    @NotNull(message = "A patient must be selected.")
    @Positive(message = "The selected patient ID is invalid.")
    private Integer patientID;

    @Positive(message = "The appointment ID must be valid.")
    private Integer appointmentID;

    @NotBlank(message = "Record type is required.")
    private String recordType;

    @Size(
            max = 5000,
            message = "Diagnosis must not exceed 5000 characters."
    )
    private String diagnosis;

    @Size(
            max = 5000,
            message = "Symptoms must not exceed 5000 characters."
    )
    private String symptoms;

    @Size(
            max = 5000,
            message = "Treatment plan must not exceed 5000 characters."
    )
    private String treatmentPlan;

    @Size(
            max = 5000,
            message = "Clinical notes must not exceed 5000 characters."
    )
    private String clinicalNotes;

    @Size(
            max = 2000,
            message = "Allergy information must not exceed 2000 characters."
    )
    private String allergies;

    @Size(
            max = 3000,
            message = "Medication information must not exceed 3000 characters."
    )
    private String medications;

    @Size(
            max = 3000,
            message = "Follow-up instructions must not exceed 3000 characters."
    )
    private String followUpInstructions;

    public HealthRecordRequestDTO() {
    }

    public Integer getPatientID() {
        return patientID;
    }

    public void setPatientID(Integer patientID) {
        this.patientID = patientID;
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
}