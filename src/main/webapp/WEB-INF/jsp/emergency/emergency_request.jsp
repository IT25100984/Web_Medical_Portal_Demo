<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Emergency Fast-Track Intake - WMP</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../shared/header.jsp" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow border-danger">
                <div class="card-header bg-danger text-white">
                    <h4 class="mb-0">🚨 Fast-Track Emergency Request</h4>
                </div>
                <div class="card-body">
                    <form id="emergencyForm" action="${pageContext.request.contextPath}/emergency/submit" method="post" novalidate>
                        <div class="row mb-3">
                            <!-- PATIENT NAME FIELD -->
                            <div class="col-md-6">
                                <label for="patientName" class="form-label fw-bold">Patient Name</label>
                                <input type="text"
                                       id="patientName"
                                       name="patientName"
                                       class="form-control"
                                       required
                                       placeholder="John Doe or 'Unknown'"
                                       pattern="[a-zA-Z\s'-]+"
                                       title="Name must only contain letters, spaces, hyphens, and apostrophes.">
                                <div class="invalid-feedback">
                                    Name cannot contain numbers or special characters.
                                </div>
                            </div>

                            <!-- SRI LANKAN PHONE NUMBER FIELD -->
                            <div class="col-md-6">
                                <label for="contactNumber" class="form-label fw-bold">Emergency Contact</label>
                                <input type="tel"
                                       id="contactNumber"
                                       name="contactNumber"
                                       class="form-control"
                                       required
                                       placeholder="e.g. 0771234567 or +94771234567"
                                       pattern="^(?:\+94|0)[0-9]{9}$"
                                       title="Please enter a valid Sri Lankan phone number (e.g. 0771234567 or +94771234567).">
                                <div class="invalid-feedback">
                                    Please enter a valid Sri Lankan number (e.g., 0771234567 or +94771234567).
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="location" class="form-label fw-bold">Current Location</label>
                            <input type="text" id="location" name="location" class="form-control" required placeholder="Ward, Room, or Address">
                            <div class="invalid-feedback">
                                Please specify the current location.
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="priorityLevel" class="form-label fw-bold text-danger">Priority Level</label>
                                <select id="priorityLevel" name="priorityLevel" class="form-select border-danger">
                                    <option value="CRITICAL">Critical (Life-Threatening)</option>
                                    <option value="URGENT" selected>Urgent (Immediate Attention)</option>
                                    <option value="STANDARD">Standard (Needs Care)</option>
                                </select>
                            </div>
                            <div class="col-md-6 d-flex align-items-center mt-4">
                                <div class="form-check form-switch fs-5">
                                    <input class="form-check-input" type="checkbox" name="requiresAmbulance" value="true" id="ambulanceSwitch">
                                    <label class="form-check-label text-danger fw-bold" for="ambulanceSwitch">🚑 Dispatch Ambulance</label>
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label fw-bold">Clinical Description / Symptoms</label>
                            <textarea id="description" name="description" class="form-control" rows="3" required placeholder="Briefly describe the emergency..."></textarea>
                            <div class="invalid-feedback">
                                Please provide a brief clinical description.
                            </div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-danger btn-lg">Trigger Emergency Alert</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("emergencyForm");
        const patientName = document.getElementById("patientName");
        const contactNumber = document.getElementById("contactNumber");

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
            }
        });
    });
</script>

<jsp:include page="/WEB-INF/jsp/ai/ai_assistant_chat.jsp" />
</body>
</html>