package com.webmedicalportaldemo.controller;

import com.webmedicalportaldemo.dao.DoctorDAO;
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
    private final DoctorDAO doctorDAO;
    private final PatientFileService patientFileService;
    private final DoctorFileService doctorFileService;

    public AuthController(UserDAO userDAO, PatientDAO patientDAO, EmployeeDAO employeeDAO, DoctorDAO doctorDAO,
                          PatientFileService patientFileService, DoctorFileService doctorFileService) {
        this.userDAO = userDAO;
        this.patientDAO = patientDAO;
        this.employeeDAO = employeeDAO;
        this.doctorDAO = doctorDAO;
        this.patientFileService = patientFileService;
        this.doctorFileService = doctorFileService;
    }

    /**
     * Displays the login page or redirects logged-in users to their role dashboard.
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(name = "error", required = false) String error, HttpSession session) {
        User currentUser = getCurrentUser(session);

        // Only auto-redirect to dashboard if there is no error parameter present
        if (currentUser != null && (error == null || error.isBlank())) {
            return redirectByRole(currentUser.getRole());
        }
        return "login";
    }

    /**
     * Authenticates a user using email and password.
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        HttpServletRequest request) {
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

        // Populate full Patient or User object into session attributes for JSP view resolution
        User sessionUser;
        if ("PATIENT".equalsIgnoreCase(user.getRole())) {
            Patient patient = patientDAO.findByUserId(user.getUserID());
            sessionUser = (patient != null) ? patient : user;
        } else {
            sessionUser = user;
        }

        session.setAttribute("user", sessionUser);
        session.setAttribute("currentUser", sessionUser);

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
    public String showRegisterPage(HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (currentUser != null) {
            return redirectByRole(currentUser.getRole());
        }

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new RegistrationRequestDTO());
        }
        return "register";
    }

    /**
     * Validates and routes patient or staff registration.
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegistrationRequestDTO request,
                               BindingResult bindingResult,
                               Model model) {
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
        patient.setPassword(request.getPassword());
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
        String employeeId = request.getEmployeeID();
        String cleanedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        // 1. Verify employee exists in registry
        Employee registryEmployee = employeeDAO.getEmployeeById(employeeId);
        if (registryEmployee == null) {
            model.addAttribute("errorMessage", "Employee ID not found in registry.");
            return "register";
        }

        // 2. Ensure employee is not already registered
        if (registryEmployee.isRegistered()) {
            model.addAttribute("errorMessage", "This employee ID has already been registered.");
            return "register";
        }

        // 3. Ensure email is not already tied to an existing user account
        if (userDAO.findByEmail(cleanedEmail) != null) {
            model.addAttribute("errorMessage", "An account with this email already exists.");
            return "register";
        }

        // 4. Create and populate User entity for the users table
        User newUser = new User();
        newUser.setFirstName(registryEmployee.getFirstName());
        newUser.setLastName(registryEmployee.getLastName());
        newUser.setEmail(cleanedEmail);
        newUser.setPassword(request.getPassword()); // Apply password hashing if active (e.g., passwordEncoder.encode(...))
        newUser.setRole(registryEmployee.getRole()); // Inherit role from registry (DOCTOR, PHARMACIST, LAB_TECHNICIAN)
        newUser.setActive(true);

        // 5. Save user record and retrieve generated user_id
        int generatedUserID = userDAO.saveUser(newUser);

        if (generatedUserID <= 0) {
            model.addAttribute("errorMessage", "The system encountered an error creating your account.");
            return "register";
        }

        // 6. Link newly created user_id to employee_registry and set is_registered = 1
        boolean linked = employeeDAO.linkUserToEmployee(registryEmployee.getEmployeeId(), generatedUserID);

        if (!linked) {
            model.addAttribute("errorMessage", "Account created, but failed to link employee profile.");
            return "register";
        }

        return "redirect:/login?msg=registration_success";
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

    private boolean employeeDetailsMatch(Employee registryEmployee, String firstName, String lastName, String email) {
        if (registryEmployee.getFirstName() == null || registryEmployee.getLastName() == null || registryEmployee.getEmail() == null) {
            return false;
        }
        return registryEmployee.getFirstName().trim().equalsIgnoreCase(firstName.trim())
                && registryEmployee.getLastName().trim().equalsIgnoreCase(lastName.trim())
                && registryEmployee.getEmail().trim().equalsIgnoreCase(email.trim());
    }

    private String normalizeBloodGroup(String bloodGroup) {
        if (bloodGroup == null || bloodGroup.isBlank()) {
            return null;
        }
        return bloodGroup.trim().toUpperCase(Locale.ROOT);
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser instanceof User) {
            return (User) currentUser;
        }
        return null;
    }

    private boolean hasRole(User user, String requiredRole) {
        return user != null && user.getRole() != null && requiredRole.equalsIgnoreCase(user.getRole());
    }

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