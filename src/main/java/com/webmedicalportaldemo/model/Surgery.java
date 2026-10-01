package com.webmedicalportaldemo.model;

import com.webmedicalportaldemo.model.strategy.SurgeryFeeStrategy;

public class Surgery extends Appointment {

    private String theaterID;
    private String addCharge;

    public Surgery() {
        super();
        this.addCharge = "NONE";
        setFeeStrategy(new SurgeryFeeStrategy());
    }

    public Surgery(
            int doctorID,
            int patientID,
            String date,
            String time,
            String theaterID) {

        super(doctorID, patientID, date, time);

        this.theaterID = theaterID;
        this.addCharge = "NONE";
        setFeeStrategy(new SurgeryFeeStrategy());
    }

    public Surgery(
            int doctorID,
            int patientID,
            String date,
            String time,
            String theaterID,
            String addCharge) {

        super(doctorID, patientID, date, time);

        this.theaterID = theaterID;
        setAddCharge(addCharge);
        setFeeStrategy(new SurgeryFeeStrategy());
    }

    // calculateFee() is no longer overridden here - it is inherited from
    // Appointment, which now delegates to the FeeCalculationStrategy
    // set above (SurgeryFeeStrategy). This is the only behavioral change:
    // WHERE the calculation logic lives, not WHAT it calculates.

    public String getTheaterID() {
        return theaterID;
    }

    public void setTheaterID(String theaterID) {
        this.theaterID = theaterID;
    }

    public String getAddCharge() {
        return addCharge;
    }

    public void setAddCharge(String addCharge) {

        if (addCharge == null ||
                addCharge.isBlank()) {

            this.addCharge = "NONE";
            return;
        }

        String normalizedCharge =
                addCharge.trim().toUpperCase();

        switch (normalizedCharge) {

            case "ANESTHESIA":
            case "FACILITY":
            case "EQUIPMENT":
            case "OTHER":
            case "NONE":
                this.addCharge = normalizedCharge;
                break;

            default:
                this.addCharge = "NONE";
                break;
        }
    }

    @Override
    public String toString() {

        return "Surgery{" +
                "doctorID=" + getDoctorID() +
                ", patientID=" + getPatientID() +
                ", theaterID='" + theaterID + '\'' +
                ", addCharge='" + addCharge + '\'' +
                ", totalFee=" + calculateFee() +
                '}';
    }
}