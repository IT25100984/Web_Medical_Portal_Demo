<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Role: ${selectedRole} | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<%@ include file="../../shared/header.jsp" %>

<main class="container pb-5 mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">
                <i class="bi bi-shield-lock-fill text-primary me-2"></i>Edit Role: ${selectedRole}
            </h1>
            <p class="text-muted mb-0">View and update users assigned to this system role.</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/system/roles" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i> Back to Roles
            </a>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-header bg-white py-3">
            <h5 class="card-title mb-0 fw-bold">Assigned Accounts (${roleUsers.size()})</h5>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                    <tr>
                        <th>User ID</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th class="text-end">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${roleUsers}">
                        <tr>
                            <td>#${u.userID}</td>
                            <td class="fw-bold">${u.firstName} ${u.lastName}</td>
                            <td>${u.email}</td>
                            <td>
                                    <span class="badge ${u.active ? 'bg-success' : 'bg-danger'}">
                                            ${u.active ? 'Active' : 'Disabled'}
                                    </span>
                            </td>
                            <td class="text-end">
                                <form action="${pageContext.request.contextPath}/system/roles/update" method="post" class="d-inline">
                                    <input type="hidden" name="userID" value="${u.userID}" />
                                    <select name="role" class="form-select form-select-sm d-inline-block w-auto me-1">
                                        <option value="PATIENT">PATIENT</option>
                                        <option value="DOCTOR">DOCTOR</option>
                                        <option value="PHARMACIST">PHARMACIST</option>
                                        <option value="LAB_TECHNICIAN">LAB_TECHNICIAN</option>
                                        <option value="HOSPITAL_ADMIN">HOSPITAL_ADMIN</option>
                                        <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-primary">Reassign</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty roleUsers}">
                        <tr>
                            <td colspan="5" class="text-center py-4 text-muted">No users currently assigned to ${selectedRole}.</td>
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