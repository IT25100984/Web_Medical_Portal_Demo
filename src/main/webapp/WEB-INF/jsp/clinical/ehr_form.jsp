<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${formMode eq 'edit'}">Edit Health Record</c:when><c:otherwise>Create Health Record</c:otherwise></c:choose> | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css
    <style>
        .ehr-form-header {
            background: linear-gradient(135deg, #0d6efd, #0dcaf0);
        }
        .form-section {
            border-left: 4px solid #0d6efd;
        }
        .required-label::after {
            content: " *";
            color: #dc3545;
        }
        textarea {
            resize: vertical;
        }
        .readonly-field {
            background-color: #e9ecef;
            cursor: not-allowed;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<c:choose>
    <c:when test="${formMode eq 'edit'}">
        <c:url var="formAction" value="/clinical/ehr/${healthRecord.healthRecordID}/edit" />
    </c:when>
    <c:otherwise>
        <c:url var="formAction" value="/clinical/ehr/create" />
    </c:otherwise>
</c:choose>
<main class="container pb-5">
    <section class="ehr-form-header text-white rounded-3 shadow p-4 mb-4">
        <div class="row align-items-center g-3">
            <div class="col-lg-8">
                <h1 class="h2 fw-bold mb-2">
                    <i class="bi bi-file-earmark-medical-fill me-2" aria-hidden="true"></i>
                    <c:choose>
                        <c:when test="${formMode eq 'edit'}">Edit Electronic Health Record</c:when>
                        <c:otherwise>Create Electronic Health Record</c:otherwise>
                    </c:choose>
                </h1>
                <p class="mb-0">
                    <c:choose>
                        <c:when test="${formMode eq 'edit'}">Update the clinical information stored in health record #<c:out value="${healthRecord.healthRecordID}" />.</c:when>
                        <c:otherwise>Document the patient's clinical assessment, diagnosis, and treatment information.</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="col-lg-4 text-lg-end">
                <span class="badge bg-light text-primary fs-6">
                    <c:choose>
                        <c:when test="${formMode eq 'edit'}">Edit Mode</c:when>
                        <c:otherwise>New Record</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </section>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            <c:out value="${errorMessage}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    ${formAction}
    <c:if test="${not empty _csrf}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    </c:if>
    <section class="card form-section border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0 text-primary">
                <i class="bi bi-person-vcard-fill me-2" aria-hidden="true"></i>Record Identification
            </h2>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label for="patientID" class="form-label required-label">Patient ID</label>
                    <form:input path="patientID" id="patientID" type="number" min="1" cssClass="form-control" cssErrorClass="form-control is-invalid" readonly="${formMode eq 'edit'}" required="required" />
                    <form:errors path="patientID" cssClass="invalid-feedback d-block" />
                    <div class="form-text">
                        <c:choose>
                            <c:when test="${formMode eq 'edit'}">The patient cannot be changed while editing a record.</c:when>
                            <c:otherwise>Enter the patient's patients.patient_id value.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-md-4">
                    <label for="appointmentID" class="form-label">Appointment ID</label>
                    <form:input path="appointmentID" id="appointmentID" type="number" min="1" cssClass="form-control" cssErrorClass="form-control is-invalid" readonly="${formMode eq 'edit'}" />
                    <form:errors path="appointmentID" cssClass="invalid-feedback d-block" />
                    <div class="form-text">Optional when the record is not linked to an appointment.</div>
                </div>
                <div class="col-md-4">
                    <label for="recordType" class="form-label required-label">Record Type</label>
                    <form:select path="recordType" id="recordType" cssClass="form-select" cssErrorClass="form-select is-invalid" required="required">
                        <form:option value="">Select record type</form:option>
                        <form:option value="CONSULTATION">Consultation</form:option>
                        <form:option value="DIAGNOSIS">Diagnosis</form:option>
                        <form:option value="TREATMENT">Treatment</form:option>
                        <form:option value="FOLLOW_UP">Follow-Up</form:option>
                        <form:option value="EMERGENCY">Emergency</form:option>
                        <form:option value="SURGERY">Surgery</form:option>
                        <form:option value="LAB_RESULT">Laboratory Result</form:option>
                        <form:option value="GENERAL_NOTE">General Note</form:option>
                    </form:select>
                    <form:errors path="recordType" cssClass="invalid-feedback d-block" />
                </div>
            </div>
        </div>
    </section>
    <section class="card form-section border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0 text-primary">
                <i class="bi bi-clipboard2-pulse-fill me-2" aria-hidden="true"></i>Clinical Assessment
            </h2>
        </div>
        <div class="card-body">
            <div class="mb-3">
                <label for="symptoms" class="form-label">Symptoms</label>
                <form:textarea path="symptoms" id="symptoms" rows="4" maxlength="5000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Describe the symptoms reported or observed during the assessment." />
                <form:errors path="symptoms" cssClass="invalid-feedback d-block" />
                <div class="form-text"><span id="symptomsCount">0</span>/5000 characters</div>
            </div>
            <div class="mb-0">
                <label for="diagnosis" class="form-label">Diagnosis</label>
                <form:textarea path="diagnosis" id="diagnosis" rows="4" maxlength="5000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Enter the clinical diagnosis or assessment outcome." />
                <form:errors path="diagnosis" cssClass="invalid-feedback d-block" />
                <div class="form-text"><span id="diagnosisCount">0</span>/5000 characters</div>
            </div>
        </div>
    </section>
    <section class="card form-section border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0 text-primary">
                <i class="bi bi-journal-medical me-2" aria-hidden="true"></i>Treatment and Clinical Notes
            </h2>
        </div>
        <div class="card-body">
            <div class="mb-3">
                <label for="treatmentPlan" class="form-label">Treatment Plan</label>
                <form:textarea path="treatmentPlan" id="treatmentPlan" rows="5" maxlength="5000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Describe the recommended treatment, monitoring, referrals, or procedures." />
                <form:errors path="treatmentPlan" cssClass="invalid-feedback d-block" />
                <div class="form-text"><span id="treatmentPlanCount">0</span>/5000 characters</div>
            </div>
            <div class="mb-0">
                <label for="clinicalNotes" class="form-label">Clinical Notes</label>
                <form:textarea path="clinicalNotes" id="clinicalNotes" rows="5" maxlength="5000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Enter relevant observations and additional clinical information." />
                <form:errors path="clinicalNotes" cssClass="invalid-feedback d-block" />
                <div class="form-text"><span id="clinicalNotesCount">0</span>/5000 characters</div>
            </div>
        </div>
    </section>
    <section class="card form-section border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0 text-primary">
                <i class="bi bi-capsule-pill me-2" aria-hidden="true"></i>Medication and Safety Information
            </h2>
        </div>
        <div class="card-body">
            <div class="mb-3">
                <label for="allergies" class="form-label">Allergies</label>
                <form:textarea path="allergies" id="allergies" rows="3" maxlength="2000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Enter known medication, food, or environmental allergies." />
                <form:errors path="allergies" cssClass="invalid-feedback d-block" />
                <div class="form-text"><span id="allergiesCount">0</span>/2000 characters</div>
            </div>
            <div class="mb-0">
                <label for="medications" class="form-label">Medications</label>
                <form:textarea path="medications" id="medications" rows="4" maxlength="3000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Enter medication names, dosage, frequency, and relevant instructions." />
                <form:errors path="medications" cssClass="invalid-feedback d-block" />
                <div class="form-text"><span id="medicationsCount">0</span>/3000 characters</div>
            </div>
        </div>
    </section>
    <section class="card form-section border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0 text-primary">
                <i class="bi bi-calendar2-check-fill me-2" aria-hidden="true"></i>Follow-Up
            </h2>
        </div>
        <div class="card-body">
            <label for="followUpInstructions" class="form-label">Follow-Up Instructions</label>
            <form:textarea path="followUpInstructions" id="followUpInstructions" rows="4" maxlength="3000" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Enter follow-up dates, warning signs, monitoring instructions, or referral details." />
            <form:errors path="followUpInstructions" cssClass="invalid-feedback d-block" />
            <div class="form-text"><span id="followUpInstructionsCount">0</span>/3000 characters</div>
        </div>
    </section>
    <div class="alert alert-warning" role="note">
        <i class="bi bi-shield-lock-fill me-2" aria-hidden="true"></i>Confirm that all clinical information is accurate before saving. Health records contain confidential patient information.
    </div>
    <div class="d-flex flex-wrap justify-content-between gap-2">
        <c:choose>
            <c:when test="${formMode eq 'edit'}">
                <c:url var="cancelUrl" value="/clinical/ehr/${healthRecord.healthRecordID}" />
            </c:when>
            <c:when test="${not empty healthRecordRequest.patientID}">
                <c:url var="cancelUrl" value="/clinical/ehr">
                    <c:param name="patientID" value="${healthRecordRequest.patientID}" />
                </c:url>
            </c:when>
            <c:otherwise>
                <c:url var="cancelUrl" value="/doctorDashboard" />
            </c:otherwise>
        </c:choose>
        ${cancelUrl}
        <i class="bi bi-x-circle me-1" aria-hidden="true"></i>Cancel
        </a>
        <button type="submit" id="saveHealthRecordButton" class="btn btn-primary">
            <i class="bi bi-save-fill me-1" aria-hidden="true"></i>
            <c:choose>
                <c:when test="${formMode eq 'edit'}">Update Health Record</c:when>
                <c:otherwise>Create Health Record</c:otherwise>
            </c:choose>
        </button>
    </div>
    </form:form>
</main>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const healthRecordForm = document.getElementById("healthRecordForm");
        const saveButton = document.getElementById("saveHealthRecordButton");
        const trackedFields = [
            { fieldID: "symptoms", counterID: "symptomsCount" },
            { fieldID: "diagnosis", counterID: "diagnosisCount" },
            { fieldID: "treatmentPlan", counterID: "treatmentPlanCount" },
            { fieldID: "clinicalNotes", counterID: "clinicalNotesCount" },
            { fieldID: "allergies", counterID: "allergiesCount" },
            { fieldID: "medications", counterID: "medicationsCount" },
            { fieldID: "followUpInstructions", counterID: "followUpInstructionsCount" }
        ];
        trackedFields.forEach(function (item) {
            const field = document.getElementById(item.fieldID);
            const counter = document.getElementById(item.counterID);
            if (field && counter) {
                const updateCounter = function () {
                    counter.textContent = field.value.length;
                };
                field.addEventListener("input", updateCounter);
                updateCounter();
            }
        });
        healthRecordForm.addEventListener("submit", function (event) {
            if (!healthRecordForm.checkValidity()) {
                event.preventDefault();
                healthRecordForm.classList.add("was-validated");
                return;
            }
            saveButton.disabled = true;
            saveButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>Saving...';
        });
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>