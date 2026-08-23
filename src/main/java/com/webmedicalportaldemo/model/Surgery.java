package com.webmedicalportaldemo.model;

public class Surgery extends Appointment {

    private static final double BASE_FEE = 5000.00;
    private static final double ANESTHESIA_FEE = 2500.00;
    private static final double FACILITY_FEE = 1500.00;
    private static final double EQUIPMENT_FEE = 3000.00;
    private static final double OTHER_FEE = 500.00;

    private String theaterID;
    private String addCharge;

    public Surgery() {
        super();
        this.addCharge = "NONE";
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
    }

    @Override
    public double calculateFee() {

        double extraCost;

        switch (addCharge) {

            case "ANESTHESIA":
                extraCost = ANESTHESIA_FEE;
                break;

            case "FACILITY":
                extraCost = FACILITY_FEE;
                break;

            case "EQUIPMENT":
                extraCost = EQUIPMENT_FEE;
                break;

            case "OTHER":
                extraCost = OTHER_FEE;
                break;

            case "NONE":
            default:
                extraCost = 0.00;
                break;
        }

        return BASE_FEE + extraCost;
    }

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