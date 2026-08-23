package com.webmedicalportaldemo.model;

public class Doctor extends User {

    private String specialization;
    private int licenseID;

    // Default Constructor
    public Doctor() {
        super();
        this.setRole("DOCTOR");
    }

    // Constructor for new Doctor registration
    public Doctor(String firstName, String lastName, String email, String password_hash,
                  String specialization, int licenseID) {

        super(firstName, lastName, email, password_hash, "DOCTOR");

        this.specialization = specialization;
        this.licenseID = licenseID;
    }

    // Constructor for existing Doctors loaded from DB
    public Doctor(int userID, String firstName, String lastName, String email,
                  String password_hash, String specialization, int licenseID) {

        super(userID, firstName, lastName, email, password_hash, "DOCTOR");

        this.specialization = specialization;
        this.licenseID = licenseID;
    }

    // Constructor for Patient Search Results
    public Doctor(int userID, String firstName, String lastName, String specialization, int licenseID) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);

        this.specialization = specialization;
        this.licenseID = licenseID;

        this.setRole("DOCTOR");
    }

    // Constructor for Admin Search Results
    public Doctor(int userID, String firstName, String lastName, String specialization) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);

        this.specialization = specialization;

        this.setRole("DOCTOR");
    }

    // Update current doctor object
    public void updateDoctor(int userID, String firstName, String lastName, String email,
                             String password_hash, String specialization, int licenseID) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPassword(password_hash);

        this.specialization = specialization;
        this.licenseID = licenseID;

        this.setRole("DOCTOR");
    }

    // Getters & Setters

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(int licenseID) {
        this.licenseID = licenseID;
    }

    @Override
    public void displayDashboard() {
        System.out.println("Displaying Doctor Portal for: " + getFirstName() + " " + getLastName());
    }

    @Override
    public String toFileString() {
        return "Doctor{" +
                "userID=" + getUserID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", licenseID=" + licenseID +
                '}';
    }
}