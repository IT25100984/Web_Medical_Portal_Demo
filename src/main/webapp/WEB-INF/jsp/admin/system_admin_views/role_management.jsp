<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Role Access Control (RBAC) | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">

<!-- Header Component -->
<%@ include file="../../shared/header.jsp" %>

<main class="container pb-5 mt-4">

    <!-- Alert Notifications -->
    <c:if test="${not empty message}">
        <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">
                <i class="bi bi-key-fill text-success me-2" aria-hidden="true"></i>Role Access Control
            </h1>
            <p class="text-muted mb-0">Configure role-based access control (RBAC) permissions across the portal.</p>
        </div>
        <div>
            <a href="<c:url value='/system/dashboard'/>" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
            </a>
        </div>
    </div>

    <!-- Role List Card -->
    <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th class="ps-4">Role Name</th>
                        <th>Description</th>
                        <th>Active Users</th>
                        <th class="text-end pe-4">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="role" items="${roles}">
                        <tr>
                            <!-- Column 1: Dynamic Role Badges -->
                            <td class="ps-4">
                                <c:choose>
                                    <c:when test="${role == 'SYSTEM_ADMIN'}">
                                        <span class="badge bg-danger text-uppercase px-2 py-1"><i class="bi bi-shield-lock me-1"></i>${role}</span>
                                    </c:when>
                                    <c:when test="${role == 'HOSPITAL_ADMIN'}">
                                        <span class="badge bg-primary text-uppercase px-2 py-1"><i class="bi bi-building me-1"></i>${role}</span>
                                    </c:when>
                                    <c:when test="${role == 'DOCTOR'}">
                                        <span class="badge bg-success text-uppercase px-2 py-1"><i class="bi bi-stethoscope me-1"></i>${role}</span>
                                    </c:when>
                                    <c:when test="${role == 'PHARMACIST'}">
                                        <span class="badge bg-info text-dark text-uppercase px-2 py-1"><i class="bi bi-capsule me-1"></i>${role}</span>
                                    </c:when>
                                    <c:when test="${role == 'LAB_TECHNICIAN'}">
                                        <span class="badge bg-warning text-dark text-uppercase px-2 py-1"><i class="bi bi-journal-medical me-1"></i>${role}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary text-uppercase px-2 py-1">${role}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Column 2: Role Description -->
                            <td class="text-muted small">
                                <c:choose>
                                    <c:when test="${role == 'SYSTEM_ADMIN'}">Manages RBAC permissions, user accounts, system configuration, and audit logs.</c:when>
                                    <c:when test="${role == 'HOSPITAL_ADMIN'}">Manages hospital staff registry, department assignments, and operational rosters.</c:when>
                                    <c:when test="${role == 'DOCTOR'}">Accesses patient health records, manages appointments, and issues prescriptions.</c:when>
                                    <c:when test="${role == 'PHARMACIST'}">Processes prescription orders, fulfills medicine requests, and tracks inventory.</c:when>
                                    <c:when test="${role == 'LAB_TECHNICIAN'}">Manages lab test requests and uploads diagnostic lab results.</c:when>
                                    <c:when test="${role == 'PATIENT'}">Accesses patient portal, books appointments, and views medical history.</c:when>
                                    <c:otherwise>Standard System Role</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Column 3: Active User Count -->
                            <td>
                                <span class="badge bg-light text-dark border">
                                    <i class="bi bi-people-fill text-secondary me-1"></i>
                                    ${not empty userRoleMappings[role] ? userRoleMappings[role] : 0} Users
                                </span>
                            </td>

                            <!-- Column 4: Edit Actions -->
                            <td class="text-end pe-4">
                                <c:url var="editUrl" value="/system/roles/edit">
                                    <c:param name="role" value="${role}"/>
                                </c:url>
                                <a href="${editUrl}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil-square me-1"></i> Edit
                                </a>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty roles}">
                        <tr>
                            <td colspan="4" class="text-center py-4 text-muted">
                                <i class="bi bi-inbox display-6 d-block mb-2"></i>
                                No system roles configured.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>