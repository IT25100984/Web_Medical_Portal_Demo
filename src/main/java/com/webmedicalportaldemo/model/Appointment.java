package com.webmedicalportaldemo.model;

import com.webmedicalportaldemo.model.strategy.FeeCalculationStrategy;

public abstract class Appointment {
    private int appointmentID;
    private int doctorID;
    private int patientID;
    private String date;
    private String time;
    private String status;
    private int lastModifiedBy;
    protected FeeCalculationStrategy feeStrategy;

    public Appointment(int doctorID, int patientID, String date, String time) {
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.date = date;
        this.time = time;
        this.status = "PENDING";
    }

    public Appointment() {}

    // Standard Getters and Setters (Encapsulation)
    public int getAppointmentID() { return appointmentID; }
    public void setAppointmentID(int id) { this.appointmentID = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getDoctorID() { return doctorID; }
    public void setDoctorID(int doctorID) { this.doctorID = doctorID; }

    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(int lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public void setFeeStrategy(FeeCalculationStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

    public double calculateFee() {
        return feeStrategy.calculateFee(this);
    }

}