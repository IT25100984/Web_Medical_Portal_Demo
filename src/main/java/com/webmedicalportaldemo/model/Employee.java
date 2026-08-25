package com.webmedicalportaldemo.model;

public class Employee extends User {

    private String employeeID;
    private String department;

    // Default Constructor
    public Employee() {
        super();
    }

    // Constructor for new employee registration
    public Employee(String firstName, String lastName, String email, String password,
                    String role, String employeeID, String department) {

        super(firstName, lastName, email, password, role);

        this.employeeID = employeeID;
        this.department = department;
    }

    // Constructor for existing employees
    public Employee(int userID, String firstName, String lastName, String email,
                    String password, String role, String employeeID, String department) {

        super(userID, firstName, lastName, email, password, role);

        this.employeeID = employeeID;
        this.department = department;
    }

    // Constructor for Search Results
    public Employee(int userID, String firstName, String lastName,
                    String role, String employeeID) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setRole(role);

        this.employeeID = employeeID;
    }

    // Update Employee Information
    public void updateEmployee(int userID, String firstName, String lastName, String email,
                               String password, String role, String employeeID, String department) {

        this.setUserID(userID);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPassword(password);
        this.setRole(role);

        this.employeeID = employeeID;
        this.department = department;
    }

    // Getters and Setters

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public void displayDashboard() {
        System.out.println(
                "Displaying Employee Dashboard for: "
                        + getFullName()
        );
    }

    @Override
    public String toString() {
        return "Employee{" +
                "userID=" + getUserID() +
                ", employeeID='" + employeeID + '\'' +
                ", name='" + getFullName() + '\'' +
                ", role='" + getRole() + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}