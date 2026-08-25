package com.webmedicalportaldemo.model;

public class Patient extends User {

    private String medicalHistory;
    private String bloodGroup;

    public Patient() {
        super();
    }

    // Used when creating a new patient during registration
    public Patient(String firstName, String lastName,
                   String email, String password,
                   String bloodGroup, String medicalHistory) {

        super();
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPassword(password);
        this.setRole("PATIENT");

        this.bloodGroup = bloodGroup;
        this.medicalHistory = medicalHistory;
    }

    // Used when loading a complete patient from the database
    public Patient(int userID, String firstName, String lastName,
                   String email, String password,
                   String bloodGroup, String medicalHistory) {

        super(userID, firstName, lastName, email, password, "PATIENT");

        this.bloodGroup = bloodGroup;
        this.medicalHistory = medicalHistory;
    }

    // Used for search results and admin listings
    public Patient(int userID, String firstName, String lastName,
                   String bloodGroup) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.bloodGroup = bloodGroup;
    }

    // Update current patient object
    public void updatePatient(int userID, String firstName, String lastName, String email,
                              String password, String bloodGroup, String medicalHistory) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPassword(password);

        this.bloodGroup = bloodGroup;
        this.medicalHistory = medicalHistory;
    }

    // Getters and Setters

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @Override
    public void displayDashboard() {
        System.out.println(
                "Displaying Patient Portal: View your appointments and history."
        );
    }

    @Override
    public String toFileString() {
        return "Patient{" +
                "userID=" + getUserID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                '}';
    }
}