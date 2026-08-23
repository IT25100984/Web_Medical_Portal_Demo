package com.webmedicalportaldemo.model;

public class HospitalAdmin extends Employee {

    private static final String HOSPITAL_ADMIN_ROLE = "HOSPITAL_ADMIN";

    /**
     * Default constructor for DAO mapping and object creation.
     */
    public HospitalAdmin() {
       super();
        setRole(HOSPITAL_ADMIN_ROLE);
        setActive(true);
    }

    /**
     * Constroctor for registering a new hospital administrator.
     * The userID is excluded because MySQL generates it.
     */
    public HospitalAdmin(
            String firstName,
            String lastName,
            String email,
            String passwordHash,
            String employeeID,
            String department) {

        super(
                firstName,
                lastName,
                email,
                passwordHash,
                HOSPITAL_ADMIN_ROLE,
                employeeID,
                department
        );

        setActive(true);
    }

    /**
        Constructor for loading on existing hospital administrator
        from the database.
     */
          public HospitalAdmin(
          int userID,
          String firstName,
          String lastName,
          String email,
          String passwordHash,
          String employeeID,
          String department,
          boolean active) {

          super(
                  userID,
                  firstName,
                  lastName,
                  email,
                  passwordHash,
                  HOSPITAL_ADMIN_ROLE,
                  employeeID,
                  department
          );
        setActive(active);
    }

    /**
     * Lightweight constructor for administator lists.
     *
     * Sensitiveinformation such as the password hash and email
     * is intentionally excluded.
     */
    public HospitalAdmin(
            int userID,
            String firstName,
            String lastName,
            String employeeID) {

        super();

        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmployeeID(employeeID);
        setRole(HOSPITAL_ADMIN_ROLE);
        setActive(true);
    }

    /**Updates the hospital administrator's profile information.
     **
     * The employee ID and role are not supplied by the form because
     * staff members should not modify those values themselves.
     */
    public void updateHospitalAdmin(
            int userID,
            String firstName,
            String lastName,
            String email,
            String passwordHash,
            String department) {

        setUserID(userID);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setDepartment(department);
        setRole(HOSPITAL_ADMIN_ROLE);

        /*
         * Preserve the existing password when no replacement
         * password is supplied.
         */
        if (passwordHash != null && !passwordHash.isBlank()) {
            setPassword(passwordHash);
        }
    }

    /**
     * Identifies the type of administrator interaction.
     */

    @Override
    public String getInteractionType() {
        return "HOSPITAL_ADMIN_CONTROL_SESSION";
    }

    /**
     *  Provides a readable summary of the administrator.
     */
    @Override
    public String getDisplaySummary() {

        return "Hospital Administrator: "
                + getFullName()
                + " (User ID: #"
                + getUserID()
                + ", Employee ID: "
                + getEmployeeID()
                + ") has opened the hospital administration console.";
    }

    /**
     * Displays the hospital administratorsdashboard message.
     */
    @Override
    public void displayDashboard() {
        System.out.println("Displaying Hospital Administrator Portal for: "+ getFullName());
    }

    /**
     **Placeholder for hospital report generation.
     */
    public void generateReport() {

        System.out.println(
                "Generating hospital administration reports.."
        );
    }

    @Override
    public String toString() {
        return "HospitalAdmin{" +
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