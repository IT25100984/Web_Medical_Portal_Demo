package com.webmedicalportaldemo.model;

public class Employee {
    private int userID;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String department;
    private boolean active = true;
    private boolean isRegistered;

    public Employee() {}

    public Employee(String employeeId, String firstName, String lastName, String email, String role, String department, boolean isRegistered) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.department = department;
        this.isRegistered = isRegistered;
    }

    // User ID Getters and Setters (supporting both casing styles)
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    // Employee ID Getters and Setters (supporting both casing styles)
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeID() { return employeeId; }
    public void setEmployeeID(String employeeId) { this.employeeId = employeeId; }

    // Names
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }

    // Account Credentials & Details
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    // Account Status
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isRegistered() { return isRegistered; }
    public void setRegistered(boolean isRegistered) { this.isRegistered = isRegistered; }
}