<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Health Record Details | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css
    <style>
        .ehr-header {
            background: linear-gradient(135deg, #0d6efd, #0dcaf0);
        }
        .record-section {
            border-left: 4px solid #0d6efd;
        }
        .record-label {
            color: #6c757d;
            font-size: 0.85rem;
            font-weight: 600;
            letter-spacing: 0.03rem;
            text-transform: uppercase;
        }
        .record-content {
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }
        .metadata-card {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<main class="container pb-5">
    <c:if test="${param.msg eq 'created'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            The health record was created successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'updated'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            The health record was updated successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.error eq 'accessDenied'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-shield-exclamation me-2" aria-hidden="true"></i>
            Access to edit this health record was denied.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <section class="ehr-header text-white rounded-3 shadow p-4 mb-4">
        <div class="row align-items-center g-3">
            <div class="col-lg-8">
                <h1 class="h2 fw-bold mb-2">
                    <i class="bi bi-file-earmark-medical-fill me-2" aria-hidden="true"></i>Electronic Health Record
                </h1>
                <p class="mb-0">Clinical record #<c:out value="${healthRecord.healthRecordID}" /></p>
            </div>
            <div class="col-lg-4 text-lg-end">
                <span class="badge bg-light text-primary fs-6">
                    <c:out value="${healthRecord.recordType}" />
                </span>
            </div>
        </div>
    </section>
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-dark text-white">
            <h2 class="h5 mb-0">
                <i class="bi bi-info-circle-fill me-2" aria-hidden="true"></i>Record Information
            </h2>
        </div>
        <div class="card-body metadata-card">
            <div class="row g-3">
                <div class="col-md-4">
                    <div class="record-label">Health Record ID</div>
                    <div class="fw-semibold">#<c:out value="${healthRecord.healthRecordID}" /></div>
                </div>
                <div class="col-md-4">
                    <div class="record-label">Patient ID</div>
                    <div class="fw-semibold">#<c:out value="${healthRecord.patientID}" /></div>
                </div>
                <div class="col-md-4">
                    <div class="record-label">Doctor ID</div>
                    <div class="fw-semibold">
                        <c:choose>
                            <c:when test="${not empty healthRecord.doctorID}">#<c:out value="${healthRecord.doctorID}" /></c:when>
                            <c:otherwise>Not assigned</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="record-label">Appointment ID</div>
                    <div class="fw-semibold">
                        <c:choose>
                            <c:when test="${not empty healthRecord.appointmentID}">#<c:out value="${healthRecord.appointmentID}" /></c:when>
                            <c:otherwise>Not associated</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="record-label">Created At</div>
                    <div class="fw-semibold">
                        <c:choose>
                            <c:when test="${not empty healthRecord.createdAt}"><c:out value="${healthRecord.createdAt}" /></c:when>
                            <c:otherwise>Not available</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="record-label">Last Updated</div>
                    <div class="fw-semibold">
                        <c:choose>
                            <c:when test="${not empty healthRecord.updatedAt}"><c:out value="${healthRecord.updatedAt}" /></c:when>
                            <c:otherwise>Not available</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <div class="row g-4">
        <div class="col-lg-6">
            <section class="card record-section border-0 shadow-sm h-100">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-activity me-2" aria-hidden="true"></i>Symptoms
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.symptoms}"><c:out value="${healthRecord.symptoms}" /></c:when>
                            <c:otherwise>No symptoms were recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
        <div class="col-lg-6">
            <section class="card record-section border-0 shadow-sm h-100">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-clipboard2-pulse me-2" aria-hidden="true"></i>Diagnosis
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.diagnosis}"><c:out value="${healthRecord.diagnosis}" /></c:when>
                            <c:otherwise>No diagnosis was recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
        <div class="col-lg-6">
            <section class="card record-section border-0 shadow-sm h-100">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-journal-medical me-2" aria-hidden="true"></i>Treatment Plan
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.treatmentPlan}"><c:out value="${healthRecord.treatmentPlan}" /></c:when>
                            <c:otherwise>No treatment plan was recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
        <div class="col-lg-6">
            <section class="card record-section border-0 shadow-sm h-100">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-card-text me-2" aria-hidden="true"></i>Clinical Notes
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.clinicalNotes}"><c:out value="${healthRecord.clinicalNotes}" /></c:when>
                            <c:otherwise>No clinical notes were recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
        <div class="col-lg-6">
            <section class="card record-section border-0 shadow-sm h-100">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-exclamation-diamond me-2" aria-hidden="true"></i>Allergies
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.allergies}"><c:out value="${healthRecord.allergies}" /></c:when>
                            <c:otherwise>No allergy information was recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
        <div class="col-lg-6">
            <section class="card record-section border-0 shadow-sm h-100">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-capsule-pill me-2" aria-hidden="true"></i>Medications
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.medications}"><c:out value="${healthRecord.medications}" /></c:when>
                            <c:otherwise>No medication information was recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
        <div class="col-12">
            <section class="card record-section border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="h5 text-primary">
                        <i class="bi bi-calendar2-check me-2" aria-hidden="true"></i>Follow-Up Instructions
                    </h2>
                    <div class="record-content text-secondary">
                        <c:choose>
                            <c:when test="${not empty healthRecord.followUpInstructions}"><c:out value="${healthRecord.followUpInstructions}" /></c:when>
                            <c:otherwise>No follow-up instructions were recorded.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </div>
    </div>
    <section class="d-flex flex-wrap justify-content-between gap-2 mt-4">
        <c:url var="healthRecordsUrl" value="/clinical/ehr">
            <c:if test="${currentUser.role eq 'DOCTOR'}">
                <c:param name="patientID" value="${healthRecord.patientID}" />
            </c:if>
        </c:url>
        ${healthRecordsUrl}
        <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>Back to Health Records
        </a>
        <c:if test="${canEdit}">
            <c:url var="editHealthRecordUrl" value="/clinical/ehr/${healthRecord.healthRecordID}/edit" />
            ${editHealthRecordUrl}
            <i class="bi bi-pencil-square me-1" aria-hidden="true"></i>Edit Health Record
            </a>
        </c:if>
    </section>
    <div class="alert alert-warning mt-4 mb-0" role="note">
        <i class="bi bi-shield-lock-fill me-2" aria-hidden="true"></i>
        This health record contains confidential clinical information. Access and changes should be limited to authorized users.
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>