<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reschedule Appointment | Hospital Administration</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>

<main class="container py-5" style="max-width: 650px;">

    <c:if test="${param.error eq 'reschedule_failed'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0 mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <strong>Reschedule Failed.</strong> The selected time slot may already be booked or invalid.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="card shadow-lg border-0 rounded-3">
        <div class="card-header bg-dark text-white p-3">
            <h4 class="h5 mb-0 fw-bold">
                <i class="bi bi-calendar2-plus-fill text-warning me-2"></i>Administrative Appointment Reschedule
            </h4>
        </div>

        <div class="card-body p-4">
            <!-- Current Appointment Summary -->
            <div class="bg-light p-3 rounded-3 border mb-4">
                <div class="row g-2 text-sm">
                    <div class="col-6">
                        <small class="text-muted d-block">Appointment ID</small>
                        <strong class="text-dark">#<c:out value="${appointment.appointmentID}" /></strong>
                    </div>
                    <div class="col-6">
                        <small class="text-muted d-block">Current Status</small>
                        <span class="badge bg-warning text-dark"><c:out value="${appointment.status}" /></span>
                    </div>
                    <div class="col-6 mt-2">
                        <small class="text-muted d-block">Patient Name</small>
                        <strong class="text-dark"><c:out value="${appointment.patientName}" /></strong>
                    </div>
                    <div class="col-6 mt-2">
                        <small class="text-muted d-block">Assigned Doctor</small>
                        <strong class="text-dark"><c:out value="${appointment.doctorName}" /></strong>
                    </div>
                    <div class="col-12 mt-2">
                        <small class="text-muted d-block">Current Scheduled Date & Time</small>
                        <span class="fw-semibold text-primary"><c:out value="${appointment.dateTime}" /></span>
                    </div>
                </div>
            </div>

            <!-- Reschedule Form -->
            <form action="${pageContext.request.contextPath}/admin/appointments/reschedule" method="POST">
                <input type="hidden" name="appointmentID" value="${appointment.appointmentID}" />

                <div class="mb-3">
                    <label for="newDate" class="form-label fw-semibold">New Appointment Date</label>
                    <input type="date" id="newDate" name="newDate" class="form-control" required />
                </div>

                <div class="mb-4">
                    <label for="newTime" class="form-label fw-semibold">New Appointment Time</label>
                    <select id="newTime" name="newTime" class="form-select" required>
                        <option value="" disabled selected>Select a time slot...</option>
                        <option value="08:00">08:00 AM</option>
                        <option value="09:00">09:00 AM</option>
                        <option value="10:00">10:00 AM</option>
                        <option value="11:00">11:00 AM</option>
                        <option value="12:00">12:00 PM</option>
                        <option value="13:00">01:00 PM</option>
                        <option value="14:00">02:00 PM</option>
                        <option value="15:00">03:00 PM</option>
                        <option value="16:00">04:00 PM</option>
                        <option value="17:00">05:00 PM</option>
                    </select>
                </div>

                <div class="d-flex justify-content-between align-items-center pt-2">
                    <a href="${pageContext.request.contextPath}/adminDashboard" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Cancel
                    </a>
                    <button type="submit" class="btn btn-warning fw-bold px-4">
                        <i class="bi bi-check-circle-fill me-1"></i>Save Changes
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Disallow selecting past dates
    document.addEventListener("DOMContentLoaded", function () {
        const dateInput = document.getElementById("newDate");
        const today = new Date().toISOString().split("T")[0];
        dateInput.setAttribute("min", today);
    });
</script>
</body>
</html>