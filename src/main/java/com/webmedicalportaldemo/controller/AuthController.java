package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.EmployeeDAO;
import com.webmedicalportaldemo.dao.PatientDAO;
import com.webmedicalportaldemo.dao.UserDAO;

import com.webmedicalportaldemo.dto.RegistrationRequestDTO;

import com.webmedicalportaldemo.model.Employee;
import com.webmedicalportaldemo.model.Patient;
import com.webmedicalportaldemo.model.User;

import com.webmedicalportaldemo.service.DoctorFileService;
import com.webmedicalportaldemo.service.PatientFileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;

@Controller
public class AuthController {
    private final UserDAO userDAO;
    private final PatientDAO patientDAO;
    private final EmployeeDAO employeeDAO;
    private final PatientFileService patientFileService;
    private final DoctorFileService doctorFileService;
    public AuthController(UserDAO userDAO, PatientDAO patientDAO, EmployeeDAO employeeDAO, PatientFileService patientFileService, DoctorFileService doctorFileService) {
        this.userDAO = userDAO;
        this.patientDAO = patientDAO;
        this.employeeDAO = employeeDAO;
        this.patientFileService = patientFileService;
        this.doctorFileService = doctorFileService;
    }
    /**
     * Displays the login page.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    /**
     * Authenticates a user using email and password.
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String email, @RequestParam("password_hash") String password, HttpSession session, HttpServletRequest request) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return "redirect:/login?error=missingCredentials";
        }
        String cleanedEmail = email.trim().toLowerCase(Locale.ROOT);
        User user = userDAO.login(cleanedEmail, password);
        if (user == null) {
            return "redirect:/login?error=invalidCredentials";
        }
        if (!user.isActive()) {
            return "redirect:/login?error=accountDisabled";
        }
        request.changeSessionId();
        session.setAttribute("user", user);
        return redirectByRole(user.getRole());
    }
    /**
     * Invalidates the current session and logs the user out.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?status=loggedOut";
    }
    /**
     * Displays the shared patient and staff registration page.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registrationRequest")) {
            model.addAttribute("registrationRequest", new RegistrationRequestDTO());
        }
        return "register";
    }
    /**
     * Validates and routes patient or staff registration.
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationRequest") RegistrationRequestDTO request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.getFieldError() != null) {
                model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            }
            return "register";
        }
        String registrationType = request.getRegistrationType();
        if ("PATIENT".equalsIgnoreCase(registrationType)) {
            return registerPatient(request, model);
        }
        if ("STAFF".equalsIgnoreCase(registrationType)) {
            return registerStaff(request, model);
        }
        model.addAttribute("errorMessage", "Please select a valid account type.");
        return "register";
    }
    /**
     * Registers a patient without requiring an employee ID.
     */
    private String registerPatient(RegistrationRequestDTO request, Model model) {
        String cleanedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userDAO.findByEmail(cleanedEmail) != null) {
            model.addAttribute("errorMessage", "An account already exists with that email address.");
            return "register";
        }
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName().trim());
        patient.setLastName(request.getLastName().trim());
        patient.setEmail(cleanedEmail);
        patient.setPassword(request.getPassword_hash());
        patient.setRole("PATIENT");
        patient.setActive(true);
        patient.setBloodGroup(normalizeBloodGroup(request.getBloodGroup()));
        patient.setMedicalHistory(request.getMedicalHistory() == null ? "" : request.getMedicalHistory().trim());
        boolean registered = patientDAO.registerPatient(patient);
        if (!registered) {
            model.addAttribute("errorMessage", "Patient registration could not be completed.");
            return "register";
        }
        try {
            patientFileService.logToFile(patient);
        } catch (Exception exception) {
            System.err.println("Patient user ID " + patient.getUserID() + " was saved to MySQL, but patients.txt could not be updated: " + exception.getMessage());
        }
        return "redirect:/login?status=registered";
    }
    /**
     * Registers staff using a pre-approved employee registry record.
     */
    private String registerStaff(RegistrationRequestDTO request, Model model) {
        String employeeID = request.getEmployeeID();
        if (employeeID == null || employeeID.isBlank()) {
            model.addAttribute("errorMessage", "Employee ID is required for staff registration.");
            return "register";
        }
        String cleanedEmployeeID = employeeID.trim().toUpperCase(Locale.ROOT);
        String cleanedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        Employee registryEmployee = employeeDAO.findEmployeeByID(cleanedEmployeeID);
        if (registryEmployee == null) {
            model.addAttribute("errorMessage", "The employee ID is not present in the hospital employee registry.");
            return "register";
        }
        if (employeeDAO.isRegistered(cleanedEmployeeID)) {
            model.addAttribute("errorMessage", "An account has already been registered for this employee ID.");
            return "register";
        }
        if (!employeeDetailsMatch(registryEmployee, request.getFirstName(), request.getLastName(), cleanedEmail)) {
            model.addAttribute("errorMessage", "The entered details do not match the hospital employee registry.");
            return "register";
        }
        if (userDAO.findByEmail(cleanedEmail) != null) {
            model.addAttribute("errorMessage", "An account already exists with that email address.");
            return "register";
        }
        Employee employee = new Employee();
        employee.setEmployeeID(registryEmployee.getEmployeeID());
        employee.setFirstName(registryEmployee.getFirstName());
        employee.setLastName(registryEmployee.getLastName());
        employee.setEmail(registryEmployee.getEmail().trim().toLowerCase(Locale.ROOT));
        employee.setPassword(request.getPassword_hash());
        employee.setRole(registryEmployee.getRole());
        employee.setDepartment(registryEmployee.getDepartment());
        employee.setActive(true);
        int generatedUserID = userDAO.saveUser(employee);
        if (generatedUserID <= 0) {
            model.addAttribute("errorMessage", "The staff user account could not be created.");
            return "register";
        }
        employee.setUserID(generatedUserID);
        boolean employeeSaved = employeeDAO.registerEmployee(employee);
        if (!employeeSaved) {
            userDAO.deleteUserById(generatedUserID);
            model.addAttribute("errorMessage", "The employee profile could not be created.");
            return "register";
        }
        boolean registryUpdated = employeeDAO.markRegistered(cleanedEmployeeID);
        if (!registryUpdated) {
            System.err.println("Staff account user ID " + generatedUserID + " was created, but employee_registry was not marked as registered.");
        }
        if ("DOCTOR".equalsIgnoreCase(employee.getRole())) {
            try {
                doctorFileService.logToFile(employee);
            } catch (Exception exception) {
                System.err.println("Doctor user ID " + generatedUserID + " was saved to MySQL, but doctors.txt could not be updated: " + exception.getMessage());
            }
        }
        return "redirect:/login?status=registered";
    }
    /**
     * Deletes the current patient's profile.
     */
    @PostMapping("/profile/delete")
    public String deleteProfile(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!hasRole(currentUser, "PATIENT")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hospital staff accounts cannot be deleted through the profile page.");
            return redirectByRole(currentUser.getRole());
        }
        int userID = currentUser.getUserID();
        boolean deleted = userDAO.deleteUserById(userID);
        if (!deleted) {
            redirectAttributes.addFlashAttribute("errorMessage", "The account could not be deleted.");
            return "redirect:/patientDashboard?error=deleteFailed";
        }
        try {
            patientFileService.deleteById(userID);
        } catch (Exception exception) {
            System.err.println("Patient user ID " + userID + " was deleted from MySQL, but patients.txt could not be updated: " + exception.getMessage());
        }
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Your account has been closed.");
        return "redirect:/login?msg=deleted";
    }
    /**
     * Checks that submitted staff details match employee_registry.
     */
    private boolean employeeDetailsMatch(Employee registryEmployee, String firstName, String lastName, String email) {
        if (registryEmployee.getFirstName() == null || registryEmployee.getLastName() == null || registryEmployee.getEmail() == null) {
            return false;
        }
        return registryEmployee.getFirstName().trim().equalsIgnoreCase(firstName.trim()) && registryEmployee.getLastName().trim().equalsIgnoreCase(lastName.trim()) && registryEmployee.getEmail().trim().equalsIgnoreCase(email.trim());
    }
    /**
     * Normalizes an optional patient blood group.
     */
    private String normalizeBloodGroup(String bloodGroup) {
        if (bloodGroup == null || bloodGroup.isBlank()) {
            return null;
        }
        return bloodGroup.trim().toUpperCase(Locale.ROOT);
    }
    /**
     * Safely retrieves the logged-in user.
     */
    private User getCurrentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }
        return null;
    }
    /**
     * Performs a null-safe role check.
     */
    private boolean hasRole(User user, String requiredRole) {
        return user != null && user.getRole() != null && requiredRole.equalsIgnoreCase(user.getRole());
    }
    /**
     * Redirects a logged-in user to the correct dashboard.
     */
    private String redirectByRole(String role) {
        if (role == null) {
            return "redirect:/login?error=invalidRole";
        }
        return switch (role.toUpperCase(Locale.ROOT)) {
            case "PATIENT" -> "redirect:/patientDashboard";
            case "DOCTOR" -> "redirect:/doctorDashboard";
            case "PHARMACIST" -> "redirect:/pharmacistDashboard";
            case "LAB_TECHNICIAN" -> "redirect:/labDashboard";
            case "HOSPITAL_ADMIN" -> "redirect:/adminDashboard";
            case "SYSTEM_ADMIN" -> "redirect:/systemAdminDashboard";
            default -> "redirect:/login?error=invalidRole";
        };
    }
}