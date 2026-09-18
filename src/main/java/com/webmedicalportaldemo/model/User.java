package com.webmedicalportaldemo.model;

import java.time.LocalDateTime;

public class User implements UserInteraction {

    private int userID;

    private String firstName;
    private String lastName;
    private String email;

    private String password;
    private String role;

    private boolean active;

    private LocalDateTime createdAt;

    // Empty constructor
    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // For existing users loaded from DB
    public User(int userID,
                String firstName,
                String lastName,
                String email,
                String password,
                String role) {

        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;

        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // For newly registering users
    public User(String firstName,
                String lastName,
                String email,
                String password,
                String role) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;

        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public void updateUser(int userID,
                           String firstName,
                           String lastName,
                           String email,
                           String password,
                           String role) {

        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getInteractionType() {
        return "STANDARD_USER_SESSION";
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.createdAt;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void displayDashboard() {
        System.out.println("User Class Dashboard");
    }

    @Override
    public String toFileString() {
        return "User{" +
                "userID=" + userID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}