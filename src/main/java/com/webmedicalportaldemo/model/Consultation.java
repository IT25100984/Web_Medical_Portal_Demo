package com.webmedicalportaldemo.model;

import com.webmedicalportaldemo.model.strategy.ConsultationFeeStrategy;

public class Consultation extends Appointment {

    private String roomNumber;

    public Consultation() {
        super();
        this.roomNumber = "UNASSIGNED";
        setFeeStrategy(new ConsultationFeeStrategy());
    }

    public Consultation(int doctorID, int patientID, String date, String time, String roomNumber) {
        super(doctorID, patientID, date, time);
        setRoomNumber(roomNumber);
        setFeeStrategy(new ConsultationFeeStrategy());
    }

    // calculateFee() is no longer overridden here - it is inherited from
    // Appointment, which delegates to the FeeCalculationStrategy set above.

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