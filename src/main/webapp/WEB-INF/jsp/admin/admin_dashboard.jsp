<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hospital Administration | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .dashboard-container {
            max-height: 520px;
            overflow-y: auto;
        }
        .dashboard-container .table thead th {
            position: sticky;
            top: 0;
            z-index: 10;
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
        }
        .bg-admin-gold {
            background: linear-gradient(45deg, #fb8500, #ffb703);
        }
        .btn-gold-action {
            background-color: #ffb703;
            color: #023047;
            font-weight: 600;
            border: none;
        }
        .btn-gold-action:hover {
            background-color: #fb8500;
            color: #ffffff;
        }
        .admin-details {
            background-color: rgba(255, 255, 255, 0.18);
            border: 1px solid rgba(255, 255, 255, 0.35);
            border-radius: 0.75rem;
        }
        .summary-card {
            height: 100%;
            border: none;
        }
        .summary-icon {
            width: 48px;
            height: 48px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            font-size: 1.25rem;
        }
        .appointment-table th,
        .appointment-table td {
            vertical-align: middle;
        }
        .empty-filter-results {
            display: none;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<main class="container pb-5">
    <c:if test="${param.msg eq 'delete_success'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm border-0" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            <strong>Success.</strong> The feedback record was deleted successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'delete_failed'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            <strong>Deletion failed.</strong> The feedback record could not be deleted from the database.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'db_deleted_file_failed'}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm border-0" role="alert">
            <i class="bi bi-exclamation-circle-fill me-2" aria-hidden="true"></i>
            <strong>Partially completed.</strong> The feedback was deleted from MySQL, but the temporary feedback file could not be updated.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.msg eq 'invalid_feedback_id'}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm border-0" role="alert">
            The selected feedback ID was invalid.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <section class="p-4 p-lg-5 mb-4 bg-admin-gold text-white rounded-3 shadow">
        <div class="row align-items-center g-4">
            <div class="col-lg-8">
                <h1 class="display-6 fw-bold text-white mb-3">
                    <i class="bi bi-building-lock me-2" aria-hidden="true"></i>Hospital Administration Dashboard
                </h1>
                <p class="fs-4 mb-2">
                    Welcome, <strong><c:out value="${currentUser.fullName}" /></strong>
                </p>
                <div class="admin-details p-3 mt-3">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <small class="d-block text-white-50">Employee ID</small>
                            <strong><c:out value="${employee.employeeID}" /></strong>
                        </div>
                        <div class="col-md-4">
                            <small class="d-block text-white-50">Department</small>
                            <strong><c:out value="${employee.department}" /></strong>
                        </div>
                        <div class="col-md-4">
                            <small class="d-block text-white-50">Account Role</small>
                            <strong>Hospital Administrator</strong>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <button type="button" class="btn btn-gold-action w-100 py-3 shadow-sm text-uppercase" data-bs-toggle="modal" data-bs-target="#adminFeedbackModal">
                    <i class="bi bi-star-fill me-2" aria-hidden="true"></i>Manage Patient Reviews
                </button>
            </div>
        </div>
    </section>
    <section class="row g-3 mb-4">
        <div class="col-md-6 col-lg-4">
            <div class="card summary-card shadow-sm">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="summary-icon bg-primary-subtle text-primary">
                        <i class="bi bi-calendar-check" aria-hidden="true"></i>
                    </div>
                    <div>
                        <p class="text-muted mb-1">Total Appointments</p>
                        <h2 class="h4 mb-0">
                            <c:choose>
                                <c:when test="${not empty adminApps}"><c:out value="${adminApps.size()}" /></c:when>
                                <c:otherwise>0</c:otherwise>
                            </c:choose>
                        </h2>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card summary-card shadow-sm">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="summary-icon bg-warning-subtle text-warning">
                        <i class="bi bi-chat-square-text" aria-hidden="true"></i>
                    </div>
                    <div>
                        <p class="text-muted mb-1">Feedback Records</p>
                        <h2 class="h4 mb-0">
                            <c:choose>
                                <c:when test="${not empty allFeedback}"><c:out value="${allFeedback.size()}" /></c:when>
                                <c:otherwise>0</c:otherwise>
                            </c:choose>
                        </h2>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card summary-card shadow-sm">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="summary-icon bg-success-subtle text-success">
                        <i class="bi bi-person-badge" aria-hidden="true"></i>
                    </div>
                    <div>
                        <p class="text-muted mb-1">Administrator Status</p>
                        <h2 class="h5 mb-0 text-success">Active</h2>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <div class="row g-3 align-items-center">
                <div class="col-lg-6">
                    <label for="appSearch" class="form-label fw-semibold">Search Appointments</label>
                    <div class="input-group">
                        <span class="input-group-text bg-white">
                            <i class="bi bi-search" aria-hidden="true"></i>
                        </span>
                        <input type="search" id="appSearch" class="form-control" placeholder="Search by ID, patient, doctor, or status" autocomplete="off">
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="form-check form-switch mt-lg-4">
                        <input class="form-check-input" type="checkbox" id="toggleCompleted" checked>
                        <label class="form-check-label fw-semibold" for="toggleCompleted">Show completed appointments</label>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="card shadow-sm border-0">
        <div class="card-header bg-dark text-white d-flex flex-wrap justify-content-between align-items-center gap-2">
            <h2 class="h5 mb-0">
                <i class="bi bi-activity me-2" aria-hidden="true"></i>Hospital Appointment Activity
            </h2>
            <span class="badge bg-warning text-dark">Administrative View</span>
        </div>
        <div class="card-body p-0 dashboard-container">
            <div class="table-responsive">
                <table class="table table-hover appointment-table align-middle mb-0">
                    <thead>
                    <tr>
                        <th scope="col">Appointment ID</th>
                        <th scope="col">Patient</th>
                        <th scope="col">Doctor</th>
                        <th scope="col">Status</th>
                        <th scope="col">Administrative Options</th>
                    </tr>
                    </thead>
                    <tbody id="appointmentTableBody">
                    <c:choose>
                        <c:when test="${not empty adminApps}">
                            <c:forEach var="app" items="${adminApps}">
                                <tr class="app-row" data-status="${app.status}">
                                    <td class="fw-bold">#<c:out value="${app.appointmentID}" /></td>
                                    <td>
                                        <span class="fw-semibold"><c:out value="${app.patientName}" /></span>
                                        <small class="text-muted d-block">Patient ID: #<c:out value="${app.patientId}" /></small>
                                    </td>
                                    <td>
                                        <span class="fw-semibold">Dr. <c:out value="${app.doctorName}" /></span>
                                        <small class="text-muted d-block">Doctor ID: #<c:out value="${app.doctorId}" /></small>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status eq 'COMPLETED'}">
                                                <span class="badge bg-success"><c:out value="${app.status}" /></span>
                                            </c:when>
                                            <c:when test="${app.status eq 'CANCELLED'}">
                                                <span class="badge bg-danger"><c:out value="${app.status}" /></span>
                                            </c:when>
                                            <c:when test="${app.status eq 'CONFIRMED'}">
                                                <span class="badge bg-primary"><c:out value="${app.status}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark"><c:out value="${app.status}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-outline-secondary" disabled>
                                            <i class="bi bi-lock-fill me-1" aria-hidden="true"></i>Managed
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr id="emptySearchResults" class="empty-filter-results">
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="bi bi-search fs-3 d-block mb-2" aria-hidden="true"></i>No appointments match the current filters.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="bi bi-calendar-x fs-3 d-block mb-2" aria-hidden="true"></i>No appointments are currently available.
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
<%@ include file="admin_feedback_modal.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("appSearch");
        const completedToggle = document.getElementById("toggleCompleted");
        const appointmentRows = document.querySelectorAll(".app-row");
        const emptySearchResults = document.getElementById("emptySearchResults");
        function applyAppointmentFilters() {
            const searchQuery = searchInput.value.trim().toLowerCase();
            const showCompleted = completedToggle.checked;
            let visibleRowCount = 0;
            appointmentRows.forEach(function (row) {
                const rowText = row.textContent.toLowerCase();
                const appointmentStatus = (row.dataset.status || "").toUpperCase();
                const matchesSearch = rowText.includes(searchQuery);
                const matchesStatus = showCompleted || appointmentStatus !== "COMPLETED";
                const shouldDisplay = matchesSearch && matchesStatus;
                row.style.display = shouldDisplay ? "" : "none";
                if (shouldDisplay) {
                    visibleRowCount++;
                }
            });
            if (emptySearchResults) {
                emptySearchResults.style.display = visibleRowCount === 0 ? "table-row" : "none";
            }
        }
        searchInput.addEventListener("input", applyAppointmentFilters);
        completedToggle.addEventListener("change", applyAppointmentFilters);
    });
</script>
</body>
</html>