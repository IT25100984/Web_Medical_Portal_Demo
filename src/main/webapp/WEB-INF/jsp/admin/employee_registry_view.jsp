<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<html>
<head>
    <title>Employee Registry | Hospital Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<%@ include file="../shared/header.jsp" %>

<div class="container mt-4 mb-5">
    <c:if test="${param.msg == 'added'}">
        <div class="alert alert-success alert-dismissible fade show">
            <i class="bi bi-check-circle-fill me-2"></i> Employee added successfully to the registry.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.msg == 'updated'}">
        <div class="alert alert-info alert-dismissible fade show">
            <i class="bi bi-info-circle-fill me-2"></i> Employee record updated successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.msg == 'deleted'}">
        <div class="alert alert-warning alert-dismissible fade show">
            <i class="bi bi-exclamation-triangle-fill me-2"></i> Employee removed from active registry.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="p-4 mb-4 text-white rounded-3 shadow" style="background: linear-gradient(135deg, #1e293b 0%, #334155 100%);">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h1 class="display-6 fw-bold"><i class="bi bi-people-fill me-2"></i>Hospital Employee Registry</h1>
                <p class="mb-0 text-light opacity-75">Manage verified hospital personnel, departments, and onboarding status.</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/employees/add" class="btn btn-success btn-lg shadow-sm">
                <i class="bi bi-person-plus-fill me-2"></i>Add New Employee
            </a>
        </div>
    </div>

    <div class="card shadow border-0">
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-dark">
                <tr>
                    <th>Employee ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Role</th>
                    <th>Department</th>
                    <th>Account Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty employees}">
                        <c:forEach var="emp" items="${employees}">
                            <tr>
                                <td><span class="badge bg-secondary font-monospace">${emp.employeeId}</span></td>
                                <td><strong>${emp.firstName} ${emp.lastName}</strong></td>
                                <td>${emp.email}</td>
                                <td>
                                    <span class="badge bg-outline-dark border text-dark">${emp.role}</span>
                                </td>
                                <td><i class="bi bi-building me-1 text-primary"></i>${emp.department}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${emp.registered}">
                                            <span class="badge bg-success"><i class="bi bi-person-check-fill me-1"></i>Registered</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark"><i class="bi bi-clock-history me-1"></i>Pending Registration</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/admin/employees/edit/${emp.employeeId}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-pencil-square"></i> Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/employees/delete" method="post" class="d-inline" onsubmit="return confirm('Are you sure you want to remove this employee from the registry?');">
                                            <input type="hidden" name="employeeId" value="${emp.employeeId}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <i class="bi bi-trash"></i> Remove
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="7" class="text-center py-5 text-muted">No employees found in registry.</td></tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>