package com.webmedicalportaldemo.model;

public class Prescription extends User{

    private int orderID;
    private int patientID;
    private int doctorID;
    private String medicineName;
    private int quantity;
    private double medicinePrice;
    private String status;
    private String orderDate;
    private String orderTime;
    private String oppositePartyName;

    public Prescription(){}
    // Default constructor
    public Prescription(int orderId, int patientId, int doctorID, String date, String time, String medName, int qty, double price, String status) {
        this.status = "PENDING";
    }

    // Constructor for new orders
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
    public Prescription(int orderID,
                        int patientID,
                        int doctorID,
                        String medicineName,
                        int quantity,
                        double medicinePrice,
                        String status,
                        String orderDate,
                        String orderTime) {

        this.orderID = orderID;
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

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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
                "orderID=" + orderID +
                ", patientID=" + patientID +
                ", doctorID=" + doctorID +
                ", medicineName='" + medicineName + '\'' +
                ", quantity=" + quantity +
                ", medicinePrice=" + medicinePrice +
                ", status='" + status + '\'' +
                '}';
    }
}