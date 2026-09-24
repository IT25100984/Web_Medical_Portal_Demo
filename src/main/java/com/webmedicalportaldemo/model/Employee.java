package com.webmedicalportaldemo.model;

public class Employee extends User {

    private String employeeId;
    private String department;
    private boolean isRegistered;

    public Employee() {
        super();
    }

    public Employee(String employeeId, String firstName, String lastName, String email, String role, String department, boolean isRegistered) {
        super();
        this.employeeId = employeeId;
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setRole(role);
        this.department = department;
        this.isRegistered = isRegistered;
    }

    // Employee ID Getters and Setters (supporting both casing styles)
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeID() { return employeeId; }
    public void setEmployeeID(String employeeId) { this.employeeId = employeeId; }

    // Department
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    // Registration Status
    public boolean isRegistered() { return isRegistered; }
    public void setRegistered(boolean isRegistered) { this.isRegistered = isRegistered; }
}