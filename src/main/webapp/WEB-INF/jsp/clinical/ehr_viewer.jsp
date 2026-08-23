<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Electronic Health Records | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css
    <style>
        .ehr-viewer-header {
            background: linear-gradient(135deg, #0d6efd, #0dcaf0);
        }
        .record-card {
            height: 100%;
            border: 0;
            border-left: 4px solid #0d6efd;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .record-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.10);
        }
        .record-summary {
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
            white-space: pre-wrap;
        }
        .metadata-label {
            color: #6c757d;
            font-size: 0.78rem;
            font-weight: 600;
            letter-spacing: 0.03rem;
            text-transform: uppercase;
        }
        .record-type-badge {
            font-size: 0.8rem;
        }
        .empty-state-icon {
            font-size: 4rem;
            color: #adb5bd;
        }
        .hidden-record {
            display: none !important;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<main class="container pb-5">
    <section class="ehr-viewer-header text-white rounded-3 shadow p-4 mb-4">
        <div class="row align-items-center g-3">
            <div class="col-lg-8">
                <h1 class="h2 fw-bold mb-2">
                    <i class="bi bi-folder2-open me-2" aria-hidden="true"></i>Electronic Health Records
                </h1>
                <p class="mb-0">
                    <c:choose>
                        <c:when test="${currentUser.role eq 'PATIENT'}">Review your clinical records, diagnoses, treatment plans, and follow-up instructions.</c:when>
                        <c:when test="${currentUser.role eq 'DOCTOR' && not empty selectedPatientID}">Review clinical records for patient #<c:out value="${selectedPatientID}" />.</c:when>
                        <c:otherwise>Select a patient before viewing clinical records.</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="col-lg-4 text-lg-end">
                <c:if test="${currentUser.role eq 'DOCTOR' && not empty selectedPatientID}">
                    <c:url var="createHealthRecordUrl" value="/clinical/ehr/create">
                        <c:param name="patientID" value="${selectedPatientID}" />
                    </c:url>
                    ${createHealthRecordUrl}
                    <i class="bi bi-file-earmark-plus-fill me-1" aria-hidden="true"></i>Create Health Record
                    </a>
                </c:if>
            </div>
        </div>
    </section>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            <c:out value="${errorMessage}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.error eq 'recordNotFound'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-file-earmark-x-fill me-2" aria-hidden="true"></i>The requested health record could not be found.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.error eq 'accessDenied'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-shield-exclamation me-2" aria-hidden="true"></i>You are not authorized to view the selected health record.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.error eq 'patientProfileNotFound'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-person-x-fill me-2" aria-hidden="true"></i>The patient profile could not be found.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${currentUser.role eq 'DOCTOR' && empty selectedPatientID}">
        <section class="card border-0 shadow-sm mb-4">
            <div class="card-header bg-dark text-white">
                <h2 class="h5 mb-0">
                    <i class="bi bi-person-search me-2" aria-hidden="true"></i>Select Patient
                </h2>
            </div>
            <div class="card-body">
                <c:url var="ehrSearchUrl" value="/clinical/ehr" />
                    ${ehrSearchUrl}
                <div class="col-md-8">
                    <label for="patientID" class="form-label">Patient ID</label>
                    <input type="number" id="patientID" name="patientID" class="form-control" min="1" placeholder="Enter patients.patient_id" required>
                    <div class="form-text">Enter the patient's ID to load the associated health records.</div>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-search me-1" aria-hidden="true"></i>View Records
                    </button>
                </div>
                </form>
            </div>
        </section>
    </c:if>
    <c:if test="${not empty selectedPatientID || currentUser.role eq 'PATIENT'}">
        <section class="card border-0 shadow-sm mb-4">
            <div class="card-body">
                <div class="row g-3 align-items-end">
                    <div class="col-lg-7">
                        <label for="recordSearch" class="form-label fw-semibold">Search Health Records</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white">
                                <i class="bi bi-search" aria-hidden="true"></i>
                            </span>
                            <input type="search" id="recordSearch" class="form-control" placeholder="Search by diagnosis, symptoms, treatment, or record ID" autocomplete="off">
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <label for="recordTypeFilter" class="form-label fw-semibold">Record Type</label>
                        <select id="recordTypeFilter" class="form-select">
                            <option value="">All record types</option>
                            <option value="CONSULTATION">Consultation</option>
                            <option value="DIAGNOSIS">Diagnosis</option>
                            <option value="TREATMENT">Treatment</option>
                            <option value="FOLLOW_UP">Follow-Up</option>
                            <option value="EMERGENCY">Emergency</option>
                            <option value="SURGERY">Surgery</option>
                            <option value="LAB_RESULT">Laboratory Result</option>
                            <option value="GENERAL_NOTE">General Note</option>
                        </select>
                    </div>
                </div>
            </div>
        </section>
        <section class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
            <div>
                <h2 class="h4 mb-1">Clinical Record History</h2>
                <p class="text-muted mb-0">
                    <c:choose>
                        <c:when test="${not empty healthRecords}"><c:out value="${healthRecords.size()}" /> record(s) available</c:when>
                        <c:otherwise>No records available</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <c:if test="${currentUser.role eq 'DOCTOR' && not empty selectedPatientID}">
                <c:url var="createRecordUrl" value="/clinical/ehr/create">
                    <c:param name="patientID" value="${selectedPatientID}" />
                </c:url>
                ${createRecordUrl}
                <i class="bi bi-plus-circle-fill me-1" aria-hidden="true"></i>New Record
                </a>
            </c:if>
        </section>
        <c:choose>
            <c:when test="${not empty healthRecords}">
                <div id="healthRecordGrid" class="row g-4">
                    <c:forEach var="record" items="${healthRecords}">
                        <div class="col-md-6 col-xl-4 health-record-item" data-record-type="${record.recordType}">
                            <article class="card record-card shadow-sm">
                                <div class="card-body d-flex flex-column">
                                    <div class="d-flex justify-content-between align-items-start gap-2 mb-3">
                                        <div>
                                            <div class="metadata-label">Health Record</div>
                                            <h3 class="h5 mb-0">#<c:out value="${record.healthRecordID}" /></h3>
                                        </div>
                                        <c:choose>
                                            <c:when test="${record.recordType eq 'EMERGENCY'}"><span class="badge bg-danger record-type-badge">Emergency</span></c:when>
                                            <c:when test="${record.recordType eq 'SURGERY'}"><span class="badge bg-dark record-type-badge">Surgery</span></c:when>
                                            <c:when test="${record.recordType eq 'LAB_RESULT'}"><span class="badge bg-info text-dark record-type-badge">Lab Result</span></c:when>
                                            <c:when test="${record.recordType eq 'FOLLOW_UP'}"><span class="badge bg-success record-type-badge">Follow-Up</span></c:when>
                                            <c:otherwise><span class="badge bg-primary record-type-badge"><c:out value="${record.recordType}" /></span></c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="row g-2 mb-3">
                                        <div class="col-6">
                                            <div class="metadata-label">Doctor ID</div>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${not empty record.doctorID}">#<c:out value="${record.doctorID}" /></c:when>
                                                    <c:otherwise>Not assigned</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <div class="metadata-label">Appointment</div>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${not empty record.appointmentID}">#<c:out value="${record.appointmentID}" /></c:when>
                                                    <c:otherwise>Not linked</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <div class="metadata-label mb-1">Diagnosis</div>
                                        <div class="record-summary text-secondary">
                                            <c:choose>
                                                <c:when test="${not empty record.diagnosis}"><c:out value="${record.diagnosis}" /></c:when>
                                                <c:otherwise>No diagnosis was recorded.</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <div class="metadata-label mb-1">Treatment Plan</div>
                                        <div class="record-summary text-secondary">
                                            <c:choose>
                                                <c:when test="${not empty record.treatmentPlan}"><c:out value="${record.treatmentPlan}" /></c:when>
                                                <c:otherwise>No treatment plan was recorded.</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="text-muted small mb-3">
                                        <i class="bi bi-clock-history me-1" aria-hidden="true"></i>
                                        <c:choose>
                                            <c:when test="${not empty record.updatedAt}">Updated: <c:out value="${record.updatedAt}" /></c:when>
                                            <c:when test="${not empty record.createdAt}">Created: <c:out value="${record.createdAt}" /></c:when>
                                            <c:otherwise>Date unavailable</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:url var="recordDetailsUrl" value="/clinical/ehr/${record.healthRecordID}" />
                                        ${recordDetailsUrl}
                                    <i class="bi bi-eye-fill me-1" aria-hidden="true"></i>View Details
                                    </a>
                                </div>
                            </article>
                        </div>
                    </c:forEach>
                </div>
                <div id="emptyFilterResults" class="card border-0 shadow-sm d-none">
                    <div class="card-body text-center py-5">
                        <i class="bi bi-search empty-state-icon d-block mb-3" aria-hidden="true"></i>
                        <h3 class="h5">No Matching Records</h3>
                        <p class="text-muted mb-0">No health records match the current search and record-type filters.</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <section class="card border-0 shadow-sm">
                    <div class="card-body text-center py-5">
                        <i class="bi bi-folder2-open empty-state-icon d-block mb-3" aria-hidden="true"></i>
                        <h2 class="h5">No Health Records Available</h2>
                        <p class="text-muted">
                            <c:choose>
                                <c:when test="${currentUser.role eq 'DOCTOR'}">No clinical records have been created for this patient.</c:when>
                                <c:otherwise>No clinical records are currently available for your patient profile.</c:otherwise>
                            </c:choose>
                        </p>
                        <c:if test="${currentUser.role eq 'DOCTOR' && not empty selectedPatientID}">
                            <c:url var="firstRecordUrl" value="/clinical/ehr/create">
                                <c:param name="patientID" value="${selectedPatientID}" />
                            </c:url>
                            ${firstRecordUrl}
                            <i class="bi bi-plus-circle-fill me-1" aria-hidden="true"></i>Create First Record
                            </a>
                        </c:if>
                    </div>
                </section>
            </c:otherwise>
        </c:choose>
    </c:if>
    <section class="d-flex flex-wrap justify-content-between gap-2 mt-4">
        <c:choose>
            <c:when test="${currentUser.role eq 'DOCTOR'}">
                <c:url var="dashboardUrl" value="/doctorDashboard" />
            </c:when>
            <c:otherwise>
                <c:url var="dashboardUrl" value="/patientDashboard" />
            </c:otherwise>
        </c:choose>
        ${dashboardUrl}
        <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>Return to Dashboard
        </a>
    </section>
    <div class="alert alert-warning mt-4 mb-0" role="note">
        <i class="bi bi-shield-lock-fill me-2" aria-hidden="true"></i>Electronic health records contain confidential clinical information and must only be accessed for authorized healthcare purposes.
    </div>
</main>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("recordSearch");
        const recordTypeFilter = document.getElementById("recordTypeFilter");
        const recordItems = document.querySelectorAll(".health-record-item");
        const emptyFilterResults = document.getElementById("emptyFilterResults");
        function filterHealthRecords() {
            if (!searchInput || !recordTypeFilter) {
                return;
            }
            const searchQuery = searchInput.value.trim().toLowerCase();
            const selectedRecordType = recordTypeFilter.value.trim().toUpperCase();
            let visibleRecordCount = 0;
            recordItems.forEach(function (recordItem) {
                const recordText = recordItem.textContent.toLowerCase();
                const recordType = (recordItem.dataset.recordType || "").toUpperCase();
                const matchesSearch = recordText.includes(searchQuery);
                const matchesType = selectedRecordType === "" || recordType === selectedRecordType;
                const shouldDisplay = matchesSearch && matchesType;
                recordItem.classList.toggle("hidden-record", !shouldDisplay);
                if (shouldDisplay) {
                    visibleRecordCount++;
                }
            });
            if (emptyFilterResults) {
                emptyFilterResults.classList.toggle("d-none", visibleRecordCount > 0);
            }
        }
        if (searchInput) {
            searchInput.addEventListener("input", filterHealthRecords);
        }
        if (recordTypeFilter) {
            recordTypeFilter.addEventListener("change", filterHealthRecords);
        }
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>