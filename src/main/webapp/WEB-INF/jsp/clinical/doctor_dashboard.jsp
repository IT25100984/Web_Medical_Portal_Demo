<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Doctor Dashboard | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .appointment-container {
            max-height: 520px;
            overflow-y: auto;
        }
        .appointment-container .table thead th {
            position: sticky;
            top: 0;
            z-index: 10;
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
        }
        .doctor-header {
            background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 25%, #94a3b8 50%, #cbd5e1 75%, #f1f5f9 100%);
        }
        .doctor-details {
            background-color: rgba(255, 255, 255, 0.40);
            border: 1px solid rgba(255, 255, 255, 0.60);
            border-radius: 0.75rem;
        }
        .appointment-actions {
            min-width: 330px;
        }
        .price-value {
            font-size: 1.1rem;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<main class="container pb-5">
    <c:if test="${param.msg eq 'availabilityUpdated'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            <strong>Availability updated.</strong> Your weekly work schedule was saved.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'availabilityPartial'}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            Some availability entries could not be saved.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'acceptSuccess'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            The appointment was confirmed successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'completeSuccess'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            The appointment was marked as completed.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'rescheduled'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-calendar-check-fill me-2" aria-hidden="true"></i>
            The rescheduling proposal was submitted.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'error' || not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            The requested operation could not be completed.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <section class="doctor-header p-4 p-lg-5 mb-4 rounded-3 shadow text-dark">
        <div class="row align-items-center g-4">
            <div class="col-lg-8">
                <h1 class="display-6 fw-bold mb-2">
                    <i class="bi bi-heart-pulse-fill me-2" aria-hidden="true"></i>Doctor Portal
                </h1>
                <p class="fs-4 mb-3">Welcome, Dr. <c:out value="${currentUser.fullName}" /></p>
                <div class="doctor-details p-3">
                    <div class="row g-3">
                        <div class="col-md-3">
                            <small class="text-muted d-block">Employee ID</small>
                            <strong><c:out value="${employee.employeeID}" /></strong>
                        </div>
                        <div class="col-md-3">
                            <small class="text-muted d-block">Specialization</small>
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty doctor.specialization}"><c:out value="${doctor.specialization}" /></c:when>
                                    <c:otherwise>General Practitioner</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                        <div class="col-md-3">
                            <small class="text-muted d-block">License ID</small>
                            <strong>
                                <c:choose>
                                    <c:when test="${doctor.licenseID gt 0}"><c:out value="${doctor.licenseID}" /></c:when>
                                    <c:otherwise>Not available</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                        <div class="col-md-3">
                            <small class="text-muted d-block">Department</small>
                            <strong><c:out value="${employee.department}" /></strong>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="d-grid gap-2">
                    <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#doctorProfileModal">
                        <i class="bi bi-pencil-square me-1" aria-hidden="true"></i>Update Profile
                    </button>
                    <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#setWorkHoursModal">
                        <i class="bi bi-calendar-check me-1" aria-hidden="true"></i>Set Work Hours
                    </button>
                    <c:url var="ehrViewerUrl" value="/clinical/ehr" />
                    <a href="${ehrViewerUrl}" class="btn btn-outline-dark">
                        <i class="bi bi-folder2-open me-1" aria-hidden="true"></i>Open EHR Viewer
                    </a>
                </div>
            </div>
        </div>
    </section>
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <div class="row g-3 align-items-center">
                <div class="col-md-6">
                    <label for="dateFilter" class="form-label fw-semibold">Filter by Date</label>
                    <div class="input-group">
                        <span class="input-group-text bg-white">
                            <i class="bi bi-calendar-search" aria-hidden="true"></i>
                        </span>
                        <input type="date" id="dateFilter" class="form-control">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-check form-switch mt-md-4">
                        <input class="form-check-input" type="checkbox" id="toggleCancelled" checked>
                        <label class="form-check-label fw-bold" for="toggleCancelled">Show cancelled appointments</label>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="card shadow-sm border-0">
        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <h2 class="h5 mb-0">
                <i class="bi bi-calendar-week me-2" aria-hidden="true"></i>Your Schedule
            </h2>
            <span class="badge bg-primary">
                <c:choose>
                    <c:when test="${not empty myAppts}"><c:out value="${myAppts.size()}" /> appointments</c:when>
                    <c:otherwise>0 appointments</c:otherwise>
                </c:choose>
            </span>
        </div>
        <div class="card-body p-0 appointment-container">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="apptTable">
                    <thead>
                    <tr>
                        <th scope="col">Date and Time</th>
                        <th scope="col">Patient</th>
                        <th scope="col">Type and Fee</th>
                        <th scope="col">Status</th>
                        <th scope="col">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty myAppts}">
                            <c:forEach var="appt" items="${myAppts}">
                                <tr class="appointment-row" data-status="${appt.status}">
                                    <td><c:out value="${appt.dateTime}" /></td>
                                    <td>
                                        <span class="fw-semibold"><c:out value="${appt.oppositePartyName}" /></span>
                                        <small class="text-muted d-block">Patient ID: #<c:out value="${appt.patientID}" /></small>
                                    </td>
                                    <td>
                                        <span class="badge bg-secondary"><c:out value="${appt.appointmentType}" /></span>
                                        <div class="price-value text-success fw-bold mt-1">
                                            LKR
                                            <c:choose>
                                                <c:when test="${not empty appt.totalFee}"><c:out value="${appt.totalFee}" /></c:when>
                                                <c:otherwise>0.00</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${appt.status eq 'CONFIRMED'}"><span class="badge bg-success">CONFIRMED</span></c:when>
                                            <c:when test="${appt.status eq 'PENDING'}"><span class="badge bg-primary">PENDING</span></c:when>
                                            <c:when test="${appt.status eq 'CANCELLED'}"><span class="badge bg-danger">CANCELLED</span></c:when>
                                            <c:when test="${appt.status eq 'RESCHEDULED'}"><span class="badge bg-info text-dark">RESCHEDULED</span></c:when>
                                            <c:when test="${appt.status eq 'COMPLETED'}"><span class="badge bg-dark">COMPLETED</span></c:when>
                                            <c:otherwise><span class="badge bg-warning text-dark"><c:out value="${appt.status}" /></span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="appointment-actions">
                                        <div class="d-flex flex-wrap gap-2">
                                            <c:url var="patientEhrUrl" value="/clinical/ehr">
                                                <c:param name="patientID" value="${appt.patientID}" />
                                            </c:url>
                                            <a href="${patientEhrUrl}" class="btn btn-sm btn-outline-primary">
                                                <i class="bi bi-folder2-open me-1" aria-hidden="true"></i>EHR
                                            </a>
                                            <c:if test="${appt.status eq 'CONFIRMED' || appt.status eq 'COMPLETED'}">
                                                <c:url var="createEhrUrl" value="/clinical/ehr/create">
                                                    <c:param name="patientID" value="${appt.patientID}" />
                                                    <c:param name="appointmentID" value="${appt.appointmentID}" />
                                                </c:url>
                                                <a href="${createEhrUrl}" class="btn btn-sm btn-outline-success">
                                                    <i class="bi bi-file-earmark-plus me-1" aria-hidden="true"></i>New Record
                                                </a>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${appt.status eq 'CANCELLED'}">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary" disabled>Cancelled</button>
                                                </c:when>
                                                <c:when test="${appt.status eq 'COMPLETED'}">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary" disabled>Completed</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${appt.status ne 'CONFIRMED' && currentUser.userID ne appt.lastModifiedBy}">
                                                        <c:url var="acceptUrl" value="/updateAppointment">
                                                            <c:param name="id" value="${appt.appointmentID}" />
                                                            <c:param name="action" value="accept" />
                                                        </c:url>
                                                        <a href="${acceptUrl}" class="btn btn-sm btn-success">Accept</a>
                                                    </c:if>
                                                    <c:if test="${appt.status eq 'CONFIRMED'}">
                                                        <c:url var="completeUrl" value="/updateAppointment">
                                                            <c:param name="id" value="${appt.appointmentID}" />
                                                            <c:param name="action" value="complete" />
                                                        </c:url>
                                                        <a href="${completeUrl}" class="btn btn-sm btn-primary">Complete</a>
                                                    </c:if>
                                                    <c:if test="${not appt.rescheduled}">
                                                        <button type="button" class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#rescheduleModal${appt.appointmentID}">Reschedule</button>
                                                    </c:if>
                                                    <button type="button" class="btn btn-sm btn-outline-danger cancel-appointment-button" data-appointment-id="${appt.appointmentID}">Cancel</button>
                                                </c:otherwise>
                                            </c:choose>
                                            <button type="button" class="btn btn-info btn-sm text-white" data-bs-toggle="modal" data-bs-target="#aboutModal${appt.appointmentID}">About</button>
                                        </div>
                                        <div class="modal fade" id="aboutModal${appt.appointmentID}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered">
                                                <div class="modal-content border-0 shadow">
                                                    <div class="modal-header bg-info text-white">
                                                        <h3 class="h5 modal-title">
                                                            <i class="bi bi-receipt-cutoff me-2" aria-hidden="true"></i>Appointment Summary
                                                        </h3>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body p-4 text-start">
                                                        <div class="row g-3">
                                                            <div class="col-6">
                                                                <small class="text-muted fw-bold text-uppercase d-block">Type</small>
                                                                <span class="fw-bold text-primary"><c:out value="${appt.appointmentType}" /></span>
                                                            </div>
                                                            <div class="col-6 text-end">
                                                                <small class="text-muted fw-bold text-uppercase d-block">Total Cost</small>
                                                                <span class="fw-bold text-success">
                                                                        LKR
                                                                        <c:choose>
                                                                            <c:when test="${not empty appt.totalFee}"><c:out value="${appt.totalFee}" /></c:when>
                                                                            <c:otherwise>0.00</c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                            </div>
                                                            <div class="col-6">
                                                                <small class="text-muted fw-bold text-uppercase d-block">Additional Requirement</small>
                                                                <span>
                                                                        <c:choose>
                                                                            <c:when test="${not empty appt.additionalCharge}"><c:out value="${appt.additionalCharge}" /></c:when>
                                                                            <c:otherwise>None</c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                            </div>
                                                            <div class="col-6 text-end">
                                                                <small class="text-muted fw-bold text-uppercase d-block">Schedule</small>
                                                                <span><c:out value="${appt.dateTime}" /></span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer bg-light border-0">
                                                        <button type="button" class="btn btn-secondary btn-sm px-4" data-bs-dismiss="modal">Close</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal fade" id="rescheduleModal${appt.appointmentID}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h3 class="h5 modal-title">Reschedule Appointment</h3>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <c:url var="updateAppointmentUrl" value="/updateAppointment" />
                                                    <form action="${updateAppointmentUrl}" method="post">
                                                        <c:if test="${not empty _csrf}">
                                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                                        </c:if>
                                                        <div class="modal-body text-dark text-start">
                                                            <input type="hidden" name="id" value="${appt.appointmentID}">
                                                            <input type="hidden" name="action" value="rescheduled">
                                                            <div class="mb-3">
                                                                <label for="newDate${appt.appointmentID}" class="form-label fw-bold">Select New Date</label>
                                                                <input type="date" id="newDate${appt.appointmentID}" name="newDate" class="form-control future-date-input" required>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="newTime${appt.appointmentID}" class="form-label fw-bold">Select New Time</label>
                                                                <select id="newTime${appt.appointmentID}" name="newTime" class="form-select" required>
                                                                    <option value="" disabled selected>Choose a time...</option>
                                                                    <c:forEach var="hour" begin="8" end="17">
                                                                        <c:set var="displayTime" value="${hour lt 10 ? '0' : ''}${hour}:00" />
                                                                        <option value="${displayTime}"><c:out value="${displayTime}" /></option>
                                                                    </c:forEach>
                                                                </select>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                            <button type="submit" class="btn btn-primary">Send Proposal</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="bi bi-calendar-x fs-2 d-block mb-2" aria-hidden="true"></i>No appointments found.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</main>
<div class="modal fade" id="doctorProfileModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="h5 modal-title">Update Professional Information</h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <c:url var="updateProfileUrl" value="/updateProfile" />
            <form action="${updateProfileUrl}" method="post">
                <c:if test="${not empty _csrf}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </c:if>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="specialization" class="form-label fw-bold">Medical Specialization</label>
                        <select id="specialization" name="specialization" class="form-select" required>
                            <option value="" disabled ${empty doctor.specialization ? 'selected' : ''}>Choose specialization...</option>
                            <c:forTokens items="General Practitioner,Cardiology,Dermatology,Pediatric,Neurology,Orthopedic" delims="," var="specializationOption">
                                <option value="${specializationOption}" ${doctor.specialization eq specializationOption ? 'selected' : ''}><c:out value="${specializationOption}" /></option>
                            </c:forTokens>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="licenseID" class="form-label fw-bold">Medical License ID</label>
                        <input type="number" id="licenseID" name="licenseID" class="form-control" min="1" value="${doctor.licenseID}" required>
                        <div class="form-text">Enter your verified numeric medical registration ID.</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="setWorkHoursModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="h5 modal-title">Set Weekly Work Hours</h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <c:url var="updateAvailabilityUrl" value="/updateAvailability" />
            <form action="${updateAvailabilityUrl}" method="post">
                <c:if test="${not empty _csrf}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </c:if>
                <div class="modal-body text-start">
                    <p class="text-muted small">Select available days and one consistent time range.</p>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Available Days</label>
                        <div class="d-flex flex-wrap gap-2">
                            <c:forEach var="day" items="${['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']}" varStatus="dayStatus">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="checkbox" name="workDays" value="${dayStatus.index + 1}" id="day${dayStatus.index + 1}">
                                    <label class="form-check-label" for="day${dayStatus.index + 1}"><c:out value="${day}" /></label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-6 mb-3">
                            <label for="startTime" class="form-label fw-bold">Start Time</label>
                            <select id="startTime" name="startTime" class="form-select" required>
                                <c:forEach var="hour" begin="0" end="23">
                                    <c:set var="formattedHour" value="${hour lt 10 ? '0' : ''}${hour}:00" />
                                    <option value="${formattedHour}" ${currentStart eq formattedHour ? 'selected' : ''}><c:out value="${formattedHour}" /></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6 mb-3">
                            <label for="endTime" class="form-label fw-bold">End Time</label>
                            <select id="endTime" name="endTime" class="form-select" required>
                                <c:forEach var="hour" begin="1" end="24">
                                    <c:set var="formattedEndHour" value="${hour eq 24 ? '23:59' : (hour lt 10 ? '0' : '')}${hour eq 24 ? '' : hour}${hour eq 24 ? '' : ':00'}" />
                                    <option value="${formattedEndHour}" ${currentEnd eq formattedEndHour ? 'selected' : ''}><c:out value="${formattedEndHour}" /></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Save Schedule</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- Bootstrap 5 JS Bundle (loaded before custom inline scripts) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<c:url var="updateAppointmentBaseUrl" value="/updateAppointment" />
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const dateInput = document.getElementById("dateFilter");
        const cancelledToggle = document.getElementById("toggleCancelled");
        const appointmentRows = document.querySelectorAll(".appointment-row");
        const futureDateInputs = document.querySelectorAll(".future-date-input");
        const savedState = localStorage.getItem("doctor_showCancelled");
        const today = new Date().toISOString().split("T")[0];

        if (savedState !== null) {
            cancelledToggle.checked = savedState === "true";
        }

        futureDateInputs.forEach(function (input) {
            input.min = today;
        });

        function filterTable() {
            const filterDate = dateInput.value;
            const showCancelled = cancelledToggle.checked;
            localStorage.setItem("doctor_showCancelled", String(showCancelled));

            appointmentRows.forEach(function (row) {
                const dateText = row.cells[0].textContent;
                const status = (row.dataset.status || "").toUpperCase();
                const matchesDate = filterDate === "" || dateText.includes(filterDate);
                const matchesStatus = showCancelled || status !== "CANCELLED";

                row.style.display = matchesDate && matchesStatus ? "" : "none";
            });
        }

        if (dateInput) dateInput.addEventListener("input", filterTable);
        if (cancelledToggle) cancelledToggle.addEventListener("change", filterTable);

        // Confirmation dialog & routing for appointment cancellation
        document.querySelectorAll(".cancel-appointment-button").forEach(function (button) {
            button.addEventListener("click", function () {
                const appointmentID = button.dataset.appointmentID;
                if (window.confirm("Are you sure you want to cancel this appointment?")) {
                    window.location.href = "${updateAppointmentBaseUrl}?id=" + encodeURIComponent(appointmentID) + "&action=cancel";
                }
            });
        });
    });
</script>
</body>
</html>