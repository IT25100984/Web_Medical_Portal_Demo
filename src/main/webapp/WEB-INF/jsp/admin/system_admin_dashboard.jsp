<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>System Administration | ProjectMASSx</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css
    <style>
        .system-admin-header {
            background: linear-gradient(135deg, #212529, #343a40, #0d6efd);
        }
        .admin-information-panel {
            background-color: rgba(255, 255, 255, 0.12);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 0.75rem;
        }
        .dashboard-card {
            height: 100%;
            border: 0;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .dashboard-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.10);
        }
        .dashboard-icon {
            width: 52px;
            height: 52px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            font-size: 1.35rem;
        }
        .system-status-indicator {
            width: 12px;
            height: 12px;
            display: inline-block;
            border-radius: 50%;
            background-color: #198754;
            margin-right: 0.4rem;
        }
        .quick-action-button {
            min-height: 110px;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<main class="container pb-5">
    <c:if test="${param.msg eq 'success'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2" aria-hidden="true"></i>
            The system administration operation was completed successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.error eq 'operationFailed'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            The requested system operation could not be completed.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <section class="system-admin-header text-white rounded-3 shadow p-4 p-lg-5 mb-4">
        <div class="row align-items-center g-4">
            <div class="col-lg-8">
                <h1 class="display-6 fw-bold mb-3">
                    <i class="bi bi-pc-display-horizontal me-2" aria-hidden="true"></i>
                    System Administration Dashboard
                </h1>
                <p class="fs-4 mb-3">
                    Welcome, <strong><c:out value="${currentUser.fullName}" /></strong>
                </p>
                <div class="admin-information-panel p-3">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <small class="d-block text-white-50">Employee ID</small>
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty employee.employeeID}">
                                        <c:out value="${employee.employeeID}" />
                                    </c:when>
                                    <c:otherwise>Not available</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                        <div class="col-md-4">
                            <small class="d-block text-white-50">Department</small>
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty employee.department}">
                                        <c:out value="${employee.department}" />
                                    </c:when>
                                    <c:otherwise>System Administration</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                        <div class="col-md-4">
                            <small class="d-block text-white-50">Account Role</small>
                            <strong>System Administrator</strong>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 text-lg-end">
                <div class="card border-0 shadow-sm">
                    <div class="card-body text-dark text-start">
                        <h2 class="h6 fw-bold">System Status</h2>
                        <p class="mb-2">
                            <span class="system-status-indicator" aria-hidden="true"></span>
                            Application available
                        </p>
                        <small class="text-muted">
                            Status is based on successful access to the administration dashboard.
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="row g-3 mb-4">
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-primary-subtle text-primary mb-3">
                        <i class="bi bi-people-fill" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">User Management</h2>
                    <p class="text-muted mb-0">
                        Review user accounts, roles, activation status, and access permissions.
                    </p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-success-subtle text-success mb-3">
                        <i class="bi bi-shield-check" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">Security</h2>
                    <p class="text-muted mb-0">
                        Monitor account access, authentication settings, and role-based permissions.
                    </p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-warning-subtle text-warning mb-3">
                        <i class="bi bi-journal-text" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">Audit Logs</h2>
                    <p class="text-muted mb-0">
                        Review important system actions, account changes, and administrative activity.
                    </p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-danger-subtle text-danger mb-3">
                        <i class="bi bi-database-check" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">Database Status</h2>
                    <p class="text-muted mb-0">
                        Review database availability, backup information, and storage status.
                    </p>
                </div>
            </div>
        </div>
    </section>
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-dark text-white">
            <h2 class="h5 mb-0">
                <i class="bi bi-lightning-charge-fill me-2" aria-hidden="true"></i>
                System Administration Actions
            </h2>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-6 col-xl-3">
                    <c:url var="userManagementUrl" value="/system/users" />
                    ${userManagementUrl}
                    <i class="bi bi-person-gear fs-3 mb-2" aria-hidden="true"></i>
                    <span class="fw-semibold">Manage Users</span>
                    </a>
                </div>
                <div class="col-md-6 col-xl-3">
                    <c:url var="roleManagementUrl" value="/system/roles" />
                    ${roleManagementUrl}
                    <i class="bi bi-key-fill fs-3 mb-2" aria-hidden="true"></i>
                    <span class="fw-semibold">Manage Roles</span>
                    </a>
                </div>
                <div class="col-md-6 col-xl-3">
                    <c:url var="auditLogUrl" value="/system/auditLogs" />
                    ${auditLogUrl}
                    <i class="bi bi-clock-history fs-3 mb-2" aria-hidden="true"></i>
                    <span class="fw-semibold">View Audit Logs</span>
                    </a>
                </div>
                <div class="col-md-6 col-xl-3">
                    <c:url var="systemSettingsUrl" value="/system/settings" />
                    ${systemSettingsUrl}
                    <i class="bi bi-gear-fill fs-3 mb-2" aria-hidden="true"></i>
                    <span class="fw-semibold">System Settings</span>
                    </a>
                </div>
            </div>
        </div>
    </section>
    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white d-flex flex-wrap justify-content-between align-items-center gap-2">
            <h2 class="h5 mb-0">
                <i class="bi bi-info-circle-fill text-primary me-2" aria-hidden="true"></i>
                System Administrator Responsibilities
            </h2>
            <span class="badge bg-primary">Technical Administration</span>
        </div>
        <div class="card-body">
            <div class="row g-4">
                <div class="col-lg-6">
                    <h3 class="h6 fw-bold">Account and Access Management</h3>
                    <ul class="text-muted mb-0">
                        <li>Review and activate system accounts.</li>
                        <li>Manage role-based access permissions.</li>
                        <li>Disable compromised or invalid accounts.</li>
                        <li>Monitor authentication-related activity.</li>
                    </ul>
                </div>
                <div class="col-lg-6">
                    <h3 class="h6 fw-bold">Technical System Management</h3>
                    <ul class="text-muted mb-0">
                        <li>Review system and audit logs.</li>
                        <li>Monitor database and application availability.</li>
                        <li>Manage backup and recovery processes.</li>
                        <li>Maintain technical system configuration.</li>
                    </ul>
                </div>
            </div>
        </div>
    </section>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>