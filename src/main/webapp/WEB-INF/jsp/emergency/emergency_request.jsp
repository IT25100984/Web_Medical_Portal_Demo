<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Emergency Fast-Track Intake - WMP</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body class="auth-page">
<jsp:include page="../shared/header.jsp" />

<main class="container py-4 py-md-5">
    <div class="row justify-content-center">
        <div class="col-md-9 col-lg-7">
            <div class="card auth-card border-0 shadow">
                <div class="auth-card-header is-danger">
                    <div class="auth-logo pulse"><i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i></div>
                    <h1 class="h4 fw-bold mb-1">Fast-Track Emergency Request</h1>
                    <p class="small mb-0 opacity-75">Fill in what you can. The care team is alerted as soon as you submit.</p>
                </div>
                <div class="card-body p-4">
                    <form id="emergencyForm" action="${pageContext.request.contextPath}/emergency/submit" method="post" novalidate>
                        <c:if test="${not empty _csrf}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        </c:if>

                        <div class="row mb-3">
                            <!-- PATIENT NAME FIELD -->
                            <div class="col-md-6 mb-3 mb-md-0">
                                <label for="patientName" class="form-label fw-semibold">Patient Name</label>
                                <div class="input-group has-validation">
                                    <span class="input-group-text"><i class="bi bi-person" aria-hidden="true"></i></span>
                                    <input type="text"
                                           id="patientName"
                                           name="patientName"
                                           class="form-control"
                                           required
                                           autocomplete="name"
                                           placeholder="John Doe or 'Unknown'"
                                           pattern="[a-zA-Z\s'\-]+"
                                           title="Name must only contain letters, spaces, hyphens, and apostrophes.">
                                    <div class="invalid-feedback">
                                        Name cannot contain numbers or special characters.
                                    </div>
                                </div>
                            </div>

                            <!-- SRI LANKAN PHONE NUMBER FIELD -->
                            <div class="col-md-6">
                                <label for="contactNumber" class="form-label fw-semibold">Emergency Contact</label>
                                <div class="input-group has-validation">
                                    <span class="input-group-text"><i class="bi bi-telephone" aria-hidden="true"></i></span>
                                    <input type="tel"
                                           id="contactNumber"
                                           name="contactNumber"
                                           class="form-control"
                                           required
                                           inputmode="tel"
                                           autocomplete="tel"
                                           placeholder="0771234567 or +94771234567"
                                           pattern="^(?:\+94|0)[0-9]{9}$"
                                           title="Please enter a valid Sri Lankan phone number (e.g. 0771234567 or +94771234567).">
                                    <div class="invalid-feedback">
                                        Please enter a valid Sri Lankan number (e.g., 0771234567 or +94771234567).
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="location" class="form-label fw-semibold">Current Location</label>
                            <div class="input-group has-validation">
                                <span class="input-group-text"><i class="bi bi-geo-alt" aria-hidden="true"></i></span>
                                <input type="text" id="location" name="location" class="form-control" required placeholder="Ward, Room, or Address">
                                <div class="invalid-feedback">
                                    Please specify the current location.
                                </div>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6 mb-3 mb-md-0">
                                <label for="priorityLevel" class="form-label fw-semibold text-danger">Priority Level</label>
                                <select id="priorityLevel" name="priorityLevel" class="form-select border-danger">
                                    <option value="CRITICAL">Critical (Life-Threatening)</option>
                                    <option value="URGENT" selected>Urgent (Immediate Attention)</option>
                                    <option value="STANDARD">Standard (Needs Care)</option>
                                </select>
                            </div>
                            <div class="col-md-6 d-flex align-items-end">
                                <div class="form-check form-switch fs-5 mb-1">
                                    <input class="form-check-input" type="checkbox" name="requiresAmbulance" value="true" id="ambulanceSwitch">
                                    <label class="form-check-label text-danger fw-bold" for="ambulanceSwitch">
                                        <i class="bi bi-truck me-1" aria-hidden="true"></i>Dispatch Ambulance
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label fw-semibold">Clinical Description / Symptoms</label>
                            <textarea id="description" name="description" class="form-control" rows="3" required placeholder="Briefly describe the emergency..."></textarea>
                            <div class="invalid-feedback">
                                Please provide a brief clinical description.
                            </div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" id="emergencyButton" class="btn btn-danger btn-lg">
                                <i class="bi bi-broadcast me-1" aria-hidden="true"></i> Trigger Emergency Alert
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("emergencyForm");
        const patientName = document.getElementById("patientName");
        const contactNumber = document.getElementById("contactNumber");
        const emergencyButton = document.getElementById("emergencyButton");

        // Patterns
        const nameRegex = /^[a-zA-Z\s'-]+$/;
        // Sri Lankan Phone pattern: standard 10-digit mobile/landline (07X/01X...) or +94 format
        const slPhoneRegex = /^(?:\+94|0)[0-9]{9}$/;

        // Live validation for Patient Name (disallows numbers in real-time)
        patientName.addEventListener("input", function () {
            const val = patientName.value.trim();
            if (val.length > 0 && !nameRegex.test(val)) {
                patientName.classList.add("is-invalid");
                patientName.classList.remove("is-valid");
            } else if (val.length > 0) {
                patientName.classList.remove("is-invalid");
                patientName.classList.add("is-valid");
            } else {
                patientName.classList.remove("is-invalid", "is-valid");
            }
        });

        // Live validation for Emergency Contact
        contactNumber.addEventListener("input", function () {
            // Strip any accidental spaces typed by the user
            const cleanPhone = contactNumber.value.replace(/\s+/g, "");

            if (cleanPhone.length > 0 && !slPhoneRegex.test(cleanPhone)) {
                contactNumber.classList.add("is-invalid");
                contactNumber.classList.remove("is-valid");
            } else if (cleanPhone.length > 0) {
                contactNumber.classList.remove("is-invalid");
                contactNumber.classList.add("is-valid");
            } else {
                contactNumber.classList.remove("is-invalid", "is-valid");
            }
        });

        // Form Submit Handler
        form.addEventListener("submit", function (event) {
            const isNameValid = nameRegex.test(patientName.value.trim());
            const cleanPhone = contactNumber.value.replace(/\s+/g, "");
            const isPhoneValid = slPhoneRegex.test(cleanPhone);

            if (!form.checkValidity() || !isNameValid || !isPhoneValid) {
                event.preventDefault();
                event.stopPropagation();

                if (!isNameValid) patientName.classList.add("is-invalid");
                if (!isPhoneValid) contactNumber.classList.add("is-invalid");

                form.classList.add("was-validated");
                return;
            }

            // Send the cleaned number (no spaces) and prevent accidental double alerts
            contactNumber.value = cleanPhone;
            emergencyButton.disabled = true;
            emergencyButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>Sending alert...';
        });
    });
</script>

<jsp:include page="/WEB-INF/jsp/ai/ai_assistant_chat.jsp" />
</body>
</html>
