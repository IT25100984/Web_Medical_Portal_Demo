<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>User Accounts Management | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">

<!-- Shared Header -->
<%@ include file="../../shared/header.jsp" %>

<main class="container pb-5 mt-4">

    <!-- Flash Message Notifications -->
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
            <h1 class="h3 fw-bold mb-1"><i class="bi bi-person-gear text-primary me-2"></i>User Accounts</h1>
            <p class="text-muted mb-0">Manage user statuses, roles, and system credentials.</p>
        </div>
        <a href="<c:url value='/system/dashboard'/>" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i> Dashboard
        </a>
    </div>

    <!-- Users Table Card -->
    <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th class="ps-4">ID</th>
                        <th>User Details</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th class="text-end pe-4">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <!-- Column 1: ID -->
                            <td class="ps-4 fw-bold text-secondary">
                                #${u.userID}
                            </td>

                            <!-- Column 2: Full Name & Email -->
                            <td>
                                <div class="fw-bold text-dark">${u.firstName} ${u.lastName}</div>
                                <small class="text-muted"><i class="bi bi-envelope me-1"></i>${u.email}</small>
                            </td>

                            <!-- Column 3: Role Badge -->
                            <td>
                                <c:choose>
                                    <c:when test="${u.role == 'SYSTEM_ADMIN'}">
                                        <span class="badge bg-danger text-uppercase px-2 py-1"><i class="bi bi-shield-lock me-1"></i>${u.role}</span>
                                    </c:when>
                                    <c:when test="${u.role == 'HOSPITAL_ADMIN'}">
                                        <span class="badge bg-primary text-uppercase px-2 py-1"><i class="bi bi-building me-1"></i>${u.role}</span>
                                    </c:when>
                                    <c:when test="${u.role == 'DOCTOR'}">
                                        <span class="badge bg-success text-uppercase px-2 py-1"><i class="bi bi-stethoscope me-1"></i>${u.role}</span>
                                    </c:when>
                                    <c:when test="${u.role == 'PHARMACIST'}">
                                        <span class="badge bg-info text-dark text-uppercase px-2 py-1"><i class="bi bi-capsule me-1"></i>${u.role}</span>
                                    </c:when>
                                    <c:when test="${u.role == 'LAB_TECHNICIAN'}">
                                        <span class="badge bg-warning text-dark text-uppercase px-2 py-1"><i class="bi bi-journal-medical me-1"></i>${u.role}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary text-uppercase px-2 py-1">${u.role}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Column 4: Status Indicator -->
                            <td>
                                <c:choose>
                                    <c:when test="${u.active}">
                                        <span class="badge bg-success-subtle text-success border border-success px-2 py-1">
                                            <i class="bi bi-check-circle-fill me-1"></i>Active
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger-subtle text-danger border border-danger px-2 py-1">
                                            <i class="bi bi-x-circle-fill me-1"></i>Disabled
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Column 5: Actions -->
                            <td class="text-end pe-4">
                                <c:url var="toggleUrl" value="/system/users/toggle-status"/>
                                <form action="${toggleUrl}" method="post" class="d-inline">
                                    <input type="hidden" name="userID" value="${not empty u.userID ? u.userID : u.userID}" />
                                    <input type="hidden" name="active" value="${!u.active}" />
                                    <button type="submit"
                                            class="btn btn-sm ${u.active ? 'btn-outline-danger' : 'btn-outline-success'}"
                                            onclick="return confirm('Are you sure you want to ${u.active ? 'disable' : 'enable'} account for ${u.firstName} ${u.lastName}?');">
                                        <i class="bi ${u.active ? 'bi-person-x' : 'bi-person-check'} me-1"></i>
                                            ${u.active ? 'Disable' : 'Enable'}
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty users}">
                        <tr>
                            <td colspan="5" class="text-center py-4 text-muted">
                                <i class="bi bi-people display-6 d-block mb-2"></i>
                                No user accounts found.
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