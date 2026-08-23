package com.webmedicalportaldemo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PrescriptionRequestDTO {

    /*
     * Required when a doctor creates a prescription for a patient.
     *
     * Do not use users.user_id here if prescriptions.patient_id
     * references patients.patient_id.
     */
    @Positive(message = "A valid patient must be selected.")
    private Integer patientID;

    /*
     * Optional for a patient-created pharmacy order.
     *
     * When a doctor creates the prescription, the doctor ID
     * should preferably be obtained from the logged-in session
     * rather than accepted from the browser.
     */
    @Positive(message = "The doctor ID must be valid.")
    private Integer doctorID;

    @NotBlank(message = "Medicine name is required.")
    @Size(
            max = 150,
            message = "Medicine name must not exceed 150 characters."
    )
    private String medicineName;

    @Min(
            value = 1,
            message = "Medicine quantity must be at least 1."
    )
    private int quantity;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Medicine price cannot be negative."
    )
    private double medicinePrice;

    @Size(
            max = 100,
            message = "Dosage must not exceed 100 characters."
    )
    private String dosage;

    @Size(
            max = 100,
            message = "Frequency must not exceed 100 characters."
    )
    private String frequency;

    @Size(
            max = 100,
            message = "Duration must not exceed 100 characters."
    )
    private String duration;

    @Size(
            max = 1000,
            message = "Instructions must not exceed 1000 characters."
    )
    private String instructions;

    public PrescriptionRequestDTO() {
    }

    public PrescriptionRequestDTO(
            Integer patientID,
            Integer doctorID,
            String medicineName,
            int quantity,
            double medicinePrice,
            String dosage,
            String frequency,
            String duration,
            String instructions) {

        this.patientID = patientID;
        this.doctorID = doctorID;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.medicinePrice = medicinePrice;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    public Integer getPatientID() {
        return patientID;
    }

    public void setPatientID(Integer patientID) {
        this.patientID = patientID;
    }

    public Integer getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(Integer doctorID) {
        this.doctorID = doctorID;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getMedicinePrice() {
        return medicinePrice;
    }

    public void setMedicinePrice(double medicinePrice) {
        this.medicinePrice = medicinePrice;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}