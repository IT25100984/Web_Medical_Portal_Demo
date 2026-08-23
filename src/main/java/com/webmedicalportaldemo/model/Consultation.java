package com.webmedicalportaldemo.model;
public class Consultation extends Appointment {
    private static final double CONSULTATION_FEE = 1500.00;
    private String roomNumber;
    public Consultation() {
        super();
        this.roomNumber = "UNASSIGNED";
    }
    public Consultation(int doctorID, int patientID, String date, String time, String roomNumber) {
        super(doctorID, patientID, date, time);
        setRoomNumber(roomNumber);
    }
    @Override
    public double calculateFee() {
        return CONSULTATION_FEE;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            this.roomNumber = "UNASSIGNED";
        } else {
            this.roomNumber = roomNumber.trim();
        }
    }
    @Override
    public String toString() {
        return "Consultation{" +
                "doctorID=" + getDoctorID() +
                ", patientID=" + getPatientID() +
                ", roomNumber='" + roomNumber + '\'' +
                ", totalFee=" + calculateFee() +
                '}';
    }
}