package com.webmedicalportaldemo.model;
public class Doctor extends User {
    private static final String DOCTOR_ROLE = "DOCTOR";
    private int doctorID;
    private String specialization;
    private String licenseID;
    public Doctor() {
        super();
        setRole(DOCTOR_ROLE);
        setActive(true);
    }
    public Doctor(String firstName, String lastName, String email, String password, String specialization, String licenseID) {
        super(firstName, lastName, email, password, DOCTOR_ROLE);
        this.specialization = specialization;
        this.licenseID = licenseID;
        setActive(true);
    }
    public Doctor(int userID, String firstName, String lastName, String email, String password, String specialization, String licenseID) {
        super(userID, firstName, lastName, email, password, DOCTOR_ROLE);
        this.specialization = specialization;
        this.licenseID = licenseID;
    }
    public Doctor(int doctorID, int userID, String firstName, String lastName, String email, String password, String specialization, String licenseID) {
        super(userID, firstName, lastName, email, password, DOCTOR_ROLE);
        this.doctorID = doctorID;
        this.specialization = specialization;
        this.licenseID = licenseID;
    }
    public Doctor(int userID, String firstName, String lastName, String specialization, String licenseID) {
        super();
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(DOCTOR_ROLE);
        setActive(true);
        this.specialization = specialization;
        this.licenseID = licenseID;
    }
    public Doctor(int doctorID, int userID, String firstName, String lastName, String specialization, String licenseID) {
        super();
        this.doctorID = doctorID;
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(DOCTOR_ROLE);
        setActive(true);
        this.specialization = specialization;
        this.licenseID = licenseID;
    }
    public Doctor(int userID, String firstName, String lastName, String specialization) {
        super();
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(DOCTOR_ROLE);
        setActive(true);
        this.specialization = specialization;
    }
    public void updateDoctor(int userID, String firstName, String lastName, String email, String password, String specialization, String licenseID) {
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setRole(DOCTOR_ROLE);
        this.specialization = specialization;
        this.licenseID = licenseID;
    }
    public void updateDoctor(int doctorID, int userID, String firstName, String lastName, String email, String password, String specialization, String licenseID) {
        this.doctorID = doctorID;
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setRole(DOCTOR_ROLE);
        this.specialization = specialization;
        this.licenseID = licenseID;
    }
    public int getDoctorID() {
        return doctorID;
    }
    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public String getLicenseID() {
        return licenseID;
    }
    public void setLicenseID(String licenseID) {
        this.licenseID = licenseID;
    }
    @Override
    public void displayDashboard() {
        System.out.println("Displaying Doctor Portal for: " + getFirstName() + " " + getLastName());
    }
    @Override
    public String toFileString() {
        return "Doctor{" +
                "doctorID=" + doctorID +
                ", userID=" + getUserID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", licenseID='" + licenseID + '\'' +
                '}';
    }
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorID=" + doctorID +
                ", userID=" + getUserID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", licenseID='" + licenseID + '\'' +
                ", role='" + getRole() + '\'' +
                ", active=" + isActive() +
                '}';
    }
}