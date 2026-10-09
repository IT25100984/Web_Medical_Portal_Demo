<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register | Web Medical Portal - WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body class="auth-page">
<%@ include file="shared/header.jsp" %>
<main class="container py-4 py-md-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card auth-card border-0 shadow">
                <div class="auth-card-header">
                    <div class="auth-logo"><i class="bi bi-person-plus-fill" aria-hidden="true"></i></div>
                    <h1 class="h4 fw-bold mb-1">Create an Account</h1>
                    <p class="mb-0 small opacity-75">Register as a patient or hospital staff member</p>
                </div>
                <div class="card-body p-4">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${errorMessage}" />
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${error}" />
                        </div>
                    </c:if>
                    <c:if test="${param.msg eq 'error'}">
                        <div class="alert alert-danger" role="alert">
                            Registration could not be completed. Please verify the entered details.
                        </div>
                    </c:if>

                    <c:url var="registerUrl" value="/register" />
                    <form:form action="${registerUrl}" method="post" modelAttribute="user" id="registrationForm">

                        <c:if test="${not empty _csrf}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        </c:if>

                        <div class="mb-3">
                            <label for="registrationType" class="form-label fw-semibold">Account Type</label>
                            <form:select path="registrationType" id="registrationType" cssClass="form-select" required="required">
                                <form:option value="">Select account type</form:option>
                                <form:option value="PATIENT">Patient</form:option>
                                <form:option value="STAFF">Hospital Staff</form:option>
                            </form:select>
                            <form:errors path="registrationType" cssClass="text-danger small d-block mt-1" />
                            <div class="form-text">
                                Hospital staff must use an employee ID issued by the hospital administrator.
                            </div>
                        </div>

                        <div id="employeeFields" class="auth-section p-3 mb-3 d-none">
                            <h2 class="h6 text-primary fw-bold mb-3">
                                <i class="bi bi-person-badge me-1" aria-hidden="true"></i> Staff Verification
                            </h2>
                            <div class="mb-0">
                                <label for="employeeID" class="form-label">Employee ID</label>
                                <form:input path="employeeID" id="employeeID" cssClass="form-control" placeholder="Example: DOC001" maxlength="20" autocomplete="off" />
                                <form:errors path="employeeID" cssClass="text-danger small d-block mt-1" />
                                <div class="form-text">
                                    The employee ID, first name, last name, and email address must match the hospital employee registry.
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="firstName" class="form-label fw-semibold">First Name</label>
                                <form:input path="firstName" id="firstName" cssClass="form-control" placeholder="Enter first name" maxlength="50" autocomplete="given-name" required="required" />
                                <form:errors path="firstName" cssClass="text-danger small d-block mt-1" />
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="lastName" class="form-label fw-semibold">Last Name</label>
                                <form:input path="lastName" id="lastName" cssClass="form-control" placeholder="Enter last name" maxlength="50" autocomplete="family-name" required="required" />
                                <form:errors path="lastName" cssClass="text-danger small d-block mt-1" />
                            </div>
                        </div>

                        <!-- EMAIL FIELD WITH UNUSUAL DOMAIN WARNING & SPRING BINDING -->
                        <div class="mb-3">
                            <label for="email" class="form-label fw-semibold">Email Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-envelope" aria-hidden="true"></i></span>
                                <form:input path="email"
                                            type="email"
                                            id="email"
                                            cssClass="form-control"
                                            required="required"
                                            placeholder="Enter email address (e.g. name@example.com)"
                                            autocomplete="email" />
                            </div>
                            <form:errors path="email" cssClass="text-danger small d-block mt-1" />

                            <div id="staffEmailHelp" class="form-text d-none">
                                Must match the email address registered with hospital administration.
                            </div>
                            <div id="emailWarning" class="text-warning small mt-1 d-none">
                                <i class="bi bi-exclamation-triangle-fill me-1" aria-hidden="true"></i>
                                This domain contains numbers or unusual formatting. Please verify there are no typos (e.g., "21gmail" instead of "gmail").
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label fw-semibold">Password</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock" aria-hidden="true"></i></span>
                                <form:password path="password" id="password" cssClass="form-control" placeholder="Create a password" minlength="6" autocomplete="new-password" required="required" />
                                <button type="button" class="btn btn-outline-secondary" id="togglePassword" aria-label="Show or hide password" aria-pressed="false">
                                    <i class="bi bi-eye" aria-hidden="true"></i>
                                </button>
                            </div>
                            <form:errors path="password" cssClass="text-danger small d-block mt-1" />
                            <div class="form-text">Use at least 6 characters.</div>
                        </div>

                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label fw-semibold">Confirm Password</label>
                            <div class="input-group has-validation">
                                <span class="input-group-text"><i class="bi bi-lock-fill" aria-hidden="true"></i></span>
                                <input type="password" id="confirmPassword" class="form-control" placeholder="Enter the password again" minlength="6" autocomplete="new-password" required>
                                <div id="passwordMismatch" class="invalid-feedback">
                                    The passwords do not match.
                                </div>
                            </div>
                        </div>

                        <div id="patientFields" class="auth-section p-3 mb-3 d-none">
                            <h2 class="h6 text-primary fw-bold mb-3">
                                <i class="bi bi-clipboard2-pulse me-1" aria-hidden="true"></i> Patient Information
                            </h2>
                            <div class="mb-3">
                                <label for="bloodGroup" class="form-label">Blood Group</label>
                                <form:select path="bloodGroup" id="bloodGroup" cssClass="form-select">
                                    <form:option value="">Select blood group</form:option>
                                    <form:option value="A+">A+</form:option>
                                    <form:option value="A-">A-</form:option>
                                    <form:option value="B+">B+</form:option>
                                    <form:option value="B-">B-</form:option>
                                    <form:option value="AB+">AB+</form:option>
                                    <form:option value="AB-">AB-</form:option>
                                    <form:option value="O+">O+</form:option>
                                    <form:option value="O-">O-</form:option>
                                    <form:option value="UNKNOWN">Unknown</form:option>
                                </form:select>
                                <form:errors path="bloodGroup" cssClass="text-danger small d-block mt-1" />
                            </div>
                            <div class="mb-0">
                                <label for="medicalHistory" class="form-label">Medical History</label>
                                <form:textarea path="medicalHistory" id="medicalHistory" cssClass="form-control" rows="4" maxlength="2000" placeholder="Enter allergies, existing conditions, or relevant medical history" />
                                <form:errors path="medicalHistory" cssClass="text-danger small d-block mt-1" />
                                <div class="form-text">
                                    This field may be left empty and updated later.
                                </div>
                            </div>
                        </div>

                        <div id="registrationHelp" class="alert alert-info d-none" role="alert"></div>
                        <button type="submit" id="registerButton" class="btn btn-primary w-100">Create Account</button>
                    </form:form>
                </div>
                <div class="card-footer bg-white text-center py-3">
                    <c:url var="loginUrl" value="/login" />
                    <small>
                        Already have an account?
                        <a href="${loginUrl}" class="text-primary text-decoration-none fw-semibold">Login here</a>
                    </small>
                </div>
            </div>
        </div>
    </div>
</main>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const registrationForm = document.getElementById("registrationForm");
        const registrationType = document.getElementById("registrationType");
        const employeeFields = document.getElementById("employeeFields");
        const employeeID = document.getElementById("employeeID");
        const patientFields = document.getElementById("patientFields");
        const bloodGroup = document.getElementById("bloodGroup");
        const medicalHistory = document.getElementById("medicalHistory");
        const emailInput = document.getElementById("email");
        const staffEmailHelp = document.getElementById("staffEmailHelp");
        const emailWarning = document.getElementById("emailWarning");
        const registrationHelp = document.getElementById("registrationHelp");
        const password = document.getElementById("password");
        const confirmPassword = document.getElementById("confirmPassword");
        const togglePassword = document.getElementById("togglePassword");
        const registerButton = document.getElementById("registerButton");

        function updateRegistrationFields(clearHiddenValues) {
            const selectedType = registrationType.value;
            const isPatient = selectedType === "PATIENT";
            const isStaff = selectedType === "STAFF";
            patientFields.classList.toggle("d-none", !isPatient);
            employeeFields.classList.toggle("d-none", !isStaff);

            if (staffEmailHelp) {
                staffEmailHelp.classList.toggle("d-none", !isStaff);
            }

            employeeID.required = isStaff;
            bloodGroup.required = isPatient;
            employeeID.disabled = !isStaff;
            bloodGroup.disabled = !isPatient;
            medicalHistory.disabled = !isPatient;

            if (clearHiddenValues && !isStaff) {
                employeeID.value = "";
            }
            if (clearHiddenValues && !isPatient) {
                bloodGroup.value = "";
                medicalHistory.value = "";
            }
            if (isPatient) {
                registrationHelp.textContent = "Patients do not need an employee ID.";
                registrationHelp.classList.remove("d-none");
            } else if (isStaff) {
                registrationHelp.textContent = "Your employee ID, first name, last name, and email address must match the employee record created by the hospital administrator. Your staff role and department will be assigned automatically.";
                registrationHelp.classList.remove("d-none");
            } else {
                registrationHelp.textContent = "";
                registrationHelp.classList.add("d-none");
            }
        }

        function validateEmailDomain() {
            if (!emailInput || !emailWarning) return;
            const email = emailInput.value.trim();

            // Legal email format (allows numbers in domain)
            const baseRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            // Strict email format (domain section has no numbers)
            const strictRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z.-]+\.[a-zA-Z]{2,}$/;

            if (email.length > 0 && baseRegex.test(email) && !strictRegex.test(email)) {
                // Highlight input box in orange and show warning note
                emailInput.style.borderColor = "#ff9800";
                emailInput.style.boxShadow = "0 0 5px rgba(255, 152, 0, 0.4)";
                emailWarning.classList.remove("d-none");
            } else {
                // Reset styles
                emailInput.style.borderColor = "";
                emailInput.style.boxShadow = "";
                emailWarning.classList.add("d-none");
            }
        }

        function validatePasswords() {
            const passwordsMatch = password.value === confirmPassword.value;
            const shouldShowError = confirmPassword.value.length > 0 && !passwordsMatch;
            confirmPassword.classList.toggle("is-invalid", shouldShowError);
            confirmPassword.setCustomValidity(passwordsMatch ? "" : "Passwords do not match.");
            return passwordsMatch;
        }

        registrationType.addEventListener("change", function () {
            updateRegistrationFields(true);
        });

        if (emailInput) {
            emailInput.addEventListener("input", validateEmailDomain);
        }

        confirmPassword.addEventListener("input", validatePasswords);
        password.addEventListener("input", function () {
            if (confirmPassword.value.length > 0) {
                validatePasswords();
            }
        });

        togglePassword.addEventListener("click", function () {
            const showPassword = password.type === "password";
            password.type = showPassword ? "text" : "password";
            confirmPassword.type = showPassword ? "text" : "password";
            togglePassword.querySelector("i").className = showPassword ? "bi bi-eye-slash" : "bi bi-eye";
            togglePassword.setAttribute("aria-pressed", String(showPassword));
        });

        if (registrationForm) {
            registrationForm.addEventListener("submit", function (event) {
                updateRegistrationFields(false);
                if (!validatePasswords()) {
                    event.preventDefault();
                    confirmPassword.focus();
                    return;
                }
                if (registrationType.value === "STAFF" && employeeID.value.trim() === "") {
                    event.preventDefault();
                    employeeID.focus();
                    return;
                }
                if (!registrationForm.checkValidity()) {
                    event.preventDefault();
                    registrationForm.classList.add("was-validated");
                    return;
                }
                registerButton.disabled = true;
                registerButton.textContent = "Creating Account...";
            });
        }

        updateRegistrationFields(false);
        validateEmailDomain();
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
