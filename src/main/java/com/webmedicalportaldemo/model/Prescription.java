package com.webmedicalportaldemo.model;

public class Prescription extends User {

    private int prescriptionID;
    private int patientID;
    private Integer doctorID;
    private String medicineName;
    private int quantity;
    private double medicinePrice;
    private String status;
    private String orderDate;
    private String orderTime;
    private String oppositePartyName;

    // Default constructor
    public Prescription() {}

    // Constructor for order/file parsing (fixed parameter assignment)
    public Prescription(int prescriptionID, int patientID, int doctorID, String orderDate, String orderTime, String medicineName, int quantity, double medicinePrice, String status) {
        this.prescriptionID = prescriptionID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.medicinePrice = medicinePrice;
        this.status = (status != null && !status.isBlank()) ? status : "PENDING";
    }

    // Constructor for creating new prescriptions
    public Prescription(int patientID,
                        int doctorID,
                        String medicineName,
                        int quantity,
                        double medicinePrice) {

        this.patientID = patientID;
        this.doctorID = doctorID;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.medicinePrice = medicinePrice;
        this.status = "PENDING";
    }

    // Constructor for loading from database
    public Prescription(int prescriptionID,
                        int patientID,
                        int doctorID,
                        String medicineName,
                        int quantity,
                        double medicinePrice,
                        String status,
                        String orderDate,
                        String orderTime) {

        this.prescriptionID = prescriptionID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.medicinePrice = medicinePrice;
        this.status = status;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    // Business Logic

    public double calculateFee() {
        return quantity * medicinePrice;
    }

    public double getTotalFee() {
        return calculateFee();
    }

    // Getters and Setters

    // Added to resolve Jakarta EL ${prescription.prescriptionID} lookup
    public int getPrescriptionID() {
        return prescriptionID;
    }

    public void setPrescriptionID(int prescriptionID) {
        this.prescriptionID = prescriptionID;
    }

    // Kept as alias for legacy order compatibility
    public int getOrderID() {
        return prescriptionID;
    }

    public void setOrderID(int prescriptionID) {
        this.prescriptionID = prescriptionID;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOppositePartyName() {
        return oppositePartyName;
    }

    public void setOppositePartyName(String oppositePartyName) {
        this.oppositePartyName = oppositePartyName;
    }

    @Override
    public String toFileString() {
        return "Prescription{" +
                "prescriptionID=" + prescriptionID +
                ", patientID=" + patientID +
                ", doctorID=" + doctorID +
                ", medicineName='" + medicineName + '\'' +
                ", quantity=" + quantity +
                ", medicinePrice=" + medicinePrice +
                ", status='" + status + '\'' +
                '}';
    }
}