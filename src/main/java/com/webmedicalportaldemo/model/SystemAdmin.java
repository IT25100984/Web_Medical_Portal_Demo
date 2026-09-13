package com.webmedicalportaldemo.model;

public class SystemAdmin extends Employee {

    private static final String SYSTEM_ADMIN_ROLE = "SYSTEM_ADMIN";

    public SystemAdmin() {
        super();
        setRole(SYSTEM_ADMIN_ROLE);
        setActive(true);
    }

    public SystemAdmin(String firstName, String lastName, String email, String password, String employeeId, String department) {
        super();
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setRole(SYSTEM_ADMIN_ROLE);
        setEmployeeId(employeeId);
        setDepartment(department);
        setActive(true);
    }

    public SystemAdmin(int userID, String firstName, String lastName, String email, String password, String employeeId, String department) {
        super();
        setuserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setRole(SYSTEM_ADMIN_ROLE);
        setEmployeeId(employeeId);
        setDepartment(department);
        setActive(true);
    }

    public SystemAdmin(int userID, String firstName, String lastName, String employeeId) {
        super();
        setuserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmployeeId(employeeId);
        setRole(SYSTEM_ADMIN_ROLE);
        setActive(true);
    }

    public void updateSystemAdmin(int userID, String firstName, String lastName, String email, String password, String department) {
        setuserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setDepartment(department);
        setRole(SYSTEM_ADMIN_ROLE);

        if (password != null && !password.isBlank()) {
            setPassword(password);
        }
    }

    public String getInteractionType() {
        return "SYSTEM_ADMIN_CONTROL_SESSION";
    }

    public String getDisplaySummary() {
        return "System Administrator: " + getFullName() + " (User ID: #" + getuserID() + ", Employee ID: " + getEmployeeId() + ") initialized the system administration console.";
    }

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
                "userID=" + getuserID() +
                ", employeeId='" + getEmployeeId() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", department='" + getDepartment() + '\'' +
                ", active=" + isActive() +
                '}';
    }
}