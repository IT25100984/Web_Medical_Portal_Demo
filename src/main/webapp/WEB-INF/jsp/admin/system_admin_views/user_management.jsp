<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>User Management | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<!-- Path goes up TWO levels to jsp/, then into shared/ -->
<%@ include file="../../shared/header.jsp" %>
<main class="container pb-5 mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1"><i class="bi bi-person-gear text-primary me-2"></i>User Accounts</h1>
            <p class="text-muted mb-0">Manage user statuses and credentials.</p>
        </div>
        <a href="${pageContext.request.contextPath}/system/dashboard" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i> Dashboard
        </a>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <!-- Assuming your DB maps user_id to userID in Java -->
                        <td>#${u.userID}</td>

                        <!-- Replaced u.username with firstName, lastName, and email -->
                        <td>
                            <span class="fw-bold">${u.firstName} ${u.lastName}</span><br>
                            <small class="text-muted">${u.email}</small>
                        </td>

                        <td><span class="badge bg-secondary">${u.role}</span></td>
                        <td>
                <span class="badge ${u.active ? 'bg-success' : 'bg-danger'}">
                        ${u.active ? 'Active' : 'Disabled'}
                </span>
                        </td>
                        <td class="text-end">
                            <form action="${pageContext.request.contextPath}/system/users/toggle-status" method="post" class="d-inline">
                                <input type="hidden" name="userID" value="${u.userID}" />
                                <input type="hidden" name="active" value="${!u.active}" />
                                <button type="submit" class="btn btn-sm ${u.active ? 'btn-outline-danger' : 'btn-outline-success'}">
                                        ${u.active ? 'Disable' : 'Enable'}
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr><td colspan="5" class="text-center py-3 text-muted">No accounts found.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>