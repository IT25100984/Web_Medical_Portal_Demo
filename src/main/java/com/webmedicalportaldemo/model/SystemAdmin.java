package com.webmedicalportaldemo.model;

public class SystemAdmin extends Employee {
    private static final String SYSTEM_ADMIN_ROLE = "SYSTEM_ADMIN";
    public SystemAdmin() {
        super();
        setRole(SYSTEM_ADMIN_ROLE);
        setActive(true);
    }
    public SystemAdmin(String firstName, String lastName, String email, String passwordHash, String employeeID, String department) {
        super(firstName, lastName, email, passwordHash, SYSTEM_ADMIN_ROLE, employeeID, department);
        setActive(true);
    }
    public SystemAdmin(int userID, String firstName, String lastName, String email, String passwordHash, String employeeID, String department) {
        super(userID, firstName, lastName, email, passwordHash, SYSTEM_ADMIN_ROLE, employeeID, department);
    }
    public SystemAdmin(int userID, String firstName, String lastName, String employeeID) {
        super();
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmployeeID(employeeID);
        setRole(SYSTEM_ADMIN_ROLE);
        setActive(true);
    }
    public void updateSystemAdmin(int userID, String firstName, String lastName, String email, String passwordHash, String department) {
        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(passwordHash);
        setDepartment(department);
        setRole(SYSTEM_ADMIN_ROLE);
    }
    @Override
    public String getInteractionType() {
        return "SYSTEM_ADMIN_CONTROL_SESSION";
    }
    @Override
    public String getDisplaySummary() {
        return "System Administrator: " + getFullName() + " (User ID: #" + getUserID() + ", Employee ID: " + getEmployeeID() + ") initialized the system administration console.";
    }
    @Override
    public void displayDashboard() {
        System.out.println("Displaying System Administrator Portal for: " + getFullName());
    }
    public void manageUsers() {
        System.out.println("Opening system user management...");
    }
    public void manageRoles() {
        System.out.println("Opening role and permission management...");
    }
    public void reviewAuditLogs() {
        System.out.println("Opening system audit logs...");
    }
    public void manageSystemSettings() {
        System.out.println("Opening system configuration settings...");
    }
    public void performSystemBackup() {
        System.out.println("Starting system backup operation...");
    }
    @Override
    public String toString() {
        return "SystemAdmin{" +
                "userID=" + getUserID() +
                ", employeeID='" + getEmployeeID() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", department='" + getDepartment() + '\'' +
                ", active=" + isActive() +
                '}';
    }
}