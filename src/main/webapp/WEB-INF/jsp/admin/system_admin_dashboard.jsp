<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>System Administration | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
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
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            transition: all 0.2s ease-in-out;
        }
        .quick-action-button:hover {
            background-color: #f8f9fa;
            border-color: #0d6efd !important;
            transform: translateY(-2px);
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
                                    <c:when test="${not empty employee.employeeId}">
                                        <c:out value="${employee.employeeId}" />
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
                                    <c:otherwise>IT & Infrastructure</c:otherwise>
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
                            Application Operational
                        </p>
                        <small class="text-muted">
                            Status based on database connectivity and application health.
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Overview Cards mapped to Backlog Requirements -->
    <section class="row g-3 mb-4">
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-primary-subtle text-primary mb-3">
                        <i class="bi bi-people-fill" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">User Accounts</h2>
                    <p class="text-muted mb-0">
                        Manage user accounts, status, and credentials across all 6 roles (PBI-24).
                    </p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-success-subtle text-success mb-3">
                        <i class="bi bi-shield-lock-fill" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">Role Access</h2>
                    <p class="text-muted mb-0">
                        Configure role-based access control (RBAC) permissions (PBI-21).
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
                    <h2 class="h5">Audit Trails</h2>
                    <p class="text-muted mb-0">
                        Review system actions, critical alerts, and emergency logs (PBI-22).
                    </p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="card dashboard-card shadow-sm">
                <div class="card-body">
                    <div class="dashboard-icon bg-danger-subtle text-danger mb-3">
                        <i class="bi bi-database-gear" aria-hidden="true"></i>
                    </div>
                    <h2 class="h5">Database Backups</h2>
                    <p class="text-muted mb-0">
                        Schedule and trigger automated daily differential database backups (PBI-23).
                    </p>
                </div>
            </div>
        </div>
    </section>

    <!-- Corrected System Actions Grid with full <a> tags -->
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
                    <a href="${userManagementUrl}" class="btn btn-outline-dark w-100 quick-action-button rounded-3">
                        <i class="bi bi-person-gear fs-3 mb-2" aria-hidden="true"></i>
                        <span class="fw-semibold">Manage Accounts (PBI-24)</span>
                    </a>
                </div>
                <div class="col-md-6 col-xl-3">
                    <c:url var="roleManagementUrl" value="/system/roles" />
                    <a href="${roleManagementUrl}" class="btn btn-outline-dark w-100 quick-action-button rounded-3">
                        <i class="bi bi-key-fill fs-3 mb-2" aria-hidden="true"></i>
                        <span class="fw-semibold">Role Access Control (PBI-21)</span>
                    </a>
                </div>
                <div class="col-md-6 col-xl-3">
                    <c:url var="auditLogUrl" value="/system/auditLogs" />
                    <a href="${auditLogUrl}" class="btn btn-outline-dark w-100 quick-action-button rounded-3">
                        <i class="bi bi-clock-history fs-3 mb-2" aria-hidden="true"></i>
                        <span class="fw-semibold">View Audit Logs (PBI-22)</span>
                    </a>
                </div>
                <div class="col-md-6 col-xl-3">
                    <c:url var="backupUrl" value="/system/backups" />
                    <a href="${backupUrl}" class="btn btn-outline-dark w-100 quick-action-button rounded-3">
                        <i class="bi bi-database-check fs-3 mb-2" aria-hidden="true"></i>
                        <span class="fw-semibold">Database Backups (PBI-23)</span>
                    </a>
                </div>
            </div>
        </div>
    </section>

    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white d-flex flex-wrap justify-content-between align-items-center gap-2">
            <h2 class="h5 mb-0">
                <i class="bi bi-info-circle-fill text-primary me-2" aria-hidden="true"></i>
                System Administrator Backlog Scope
            </h2>
            <span class="badge bg-primary">PBI-21 to PBI-24</span>
        </div>
        <div class="card-body">
            <div class="row g-4">
                <div class="col-lg-6">
                    <h3 class="h6 fw-bold">Account and Access Management</h3>
                    <ul class="text-muted mb-0">
                        <li>Configure role-based access control across all 6 user roles (PBI-21).</li>
                        <li>Manage user accounts, active statuses, and password resets (PBI-24).</li>
                        <li>Disable compromised or inactive accounts immediately.</li>
                    </ul>
                </div>
                <div class="col-lg-6">
                    <h3 class="h6 fw-bold">Technical Operations & Security</h3>
                    <ul class="text-muted mb-0">
                        <li>Log critical patient alerts and escalations in audit trails (PBI-22).</li>
                        <li>Schedule and monitor automated daily differential database backups (PBI-23).</li>
                        <li>Monitor database health and application uptime.</li>
                    </ul>
                </div>
            </div>
        </div>
    </section>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>