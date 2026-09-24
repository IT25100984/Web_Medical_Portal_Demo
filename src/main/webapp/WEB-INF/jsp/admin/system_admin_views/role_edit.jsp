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

    <!-- Flash Message Notifications -->
    <c:choose>
        <c:when test="${param.msg eq 'success'}">
            <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>User role reassigned successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:when>
        <c:when test="${param.error eq 'patientToStaffForbidden'}">
            <div class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-shield-exclamation me-2"></i><strong>Action Blocked:</strong> Patient accounts cannot be directly reassigned as hospital employees due to medical record integrity constraints.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:when>
        <c:when test="${param.error eq 'staffToPatientForbidden'}">
            <div class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-shield-exclamation me-2"></i><strong>Action Blocked:</strong> Hospital staff members cannot be demoted to patient accounts.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:when>
        <c:when test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>Failed to update user role.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:when>
    </c:choose>

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

    <!-- Info Banner for Patient Role Page -->
    <c:if test="${selectedRole == 'PATIENT'}">
        <div class="alert alert-info border-0 shadow-sm mb-4">
            <i class="bi bi-info-circle-fill me-2"></i>
            Patient accounts are restricted to the Patient Portal. To grant staff privileges to a user, create a dedicated staff credential.
        </div>
    </c:if>

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
                            <td class="fw-bold text-secondary">#<c:out value="${u.userId}" /></td>
                            <td class="fw-bold"><c:out value="${u.firstName}" /> <c:out value="${u.lastName}" /></td>
                            <td><c:out value="${u.email}" /></td>
                            <td>
                                <span class="badge ${u.active ? 'bg-success' : 'bg-danger'}">
                                        ${u.active ? 'Active' : 'Disabled'}
                                </span>
                            </td>
                            <td class="text-end">
                                <form action="${pageContext.request.contextPath}/system/roles/update" method="post" class="d-inline">
                                    <input type="hidden" name="userID" value="${u.userId}" />

                                    <c:choose>
                                        <c:when test="${selectedRole == 'PATIENT'}">
                                            <select name="role" class="form-select form-select-sm d-inline-block w-auto me-1" disabled>
                                                <option value="PATIENT" selected>PATIENT</option>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-secondary" disabled>Restricted</button>
                                        </c:when>
                                        <c:otherwise>
                                            <select name="role" class="form-select form-select-sm d-inline-block w-auto me-1">
                                                <option value="DOCTOR" ${u.role == 'DOCTOR' ? 'selected' : ''}>DOCTOR</option>
                                                <option value="PHARMACIST" ${u.role == 'PHARMACIST' ? 'selected' : ''}>PHARMACIST</option>
                                                <option value="LAB_TECHNICIAN" ${u.role == 'LAB_TECHNICIAN' ? 'selected' : ''}>LAB_TECHNICIAN</option>
                                                <option value="HOSPITAL_ADMIN" ${u.role == 'HOSPITAL_ADMIN' ? 'selected' : ''}>HOSPITAL_ADMIN</option>
                                                <option value="SYSTEM_ADMIN" ${u.role == 'SYSTEM_ADMIN' ? 'selected' : ''}>SYSTEM_ADMIN</option>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-primary">Reassign</button>
                                        </c:otherwise>
                                    </c:choose>
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