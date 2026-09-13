<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Role Access Control (PBI-21) | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<!-- Adjust path to your shared header if necessary -->
<%@ include file="../../shared/header.jsp" %>
<main class="container pb-5 mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">
                <i class="bi bi-key-fill text-success me-2" aria-hidden="true"></i>Role Access Control
            </h1>
            <p class="text-muted mb-0">Configure role-based access control (RBAC) permissions across the system.</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/system/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
            </a>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th>Role Name</th>
                        <th>Description</th>
                        <th>Active Users</th>
                        <th class="text-end">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="role" items="${roles}">
                        <tr>
                            <!-- Column 1: Role Name -->
                            <td class="fw-bold">${role}</td>

                            <!-- Column 2: Description -->
                            <td class="text-muted">System Role</td>

                            <!-- Column 3: Active Users (NEW) -->
                            <td>
                                <!-- Pulls the count from the Map passed by your controller, defaults to 0 -->
                                <span class="badge bg-secondary">
                    ${userRoleMappings[role] != null ? userRoleMappings[role] : 0} Users
                </span>
                            </td>

                            <!-- Column 4: Actions (Corrected alignment) -->
                            <td class="text-end">
                                <a href="${pageContext.request.contextPath}/system/roles/edit?role=${role}"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil-square me-1"></i> Edit
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty roles}">
                        <!-- Updated colspan to 4 to match the header -->
                        <tr><td colspan="4" class="text-center py-3 text-muted">No roles configured.</td></tr>
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