<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Database Backups (PBI-23) | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<%@ include file="../../shared/header.jsp" %>
<main class="container pb-5 mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">
                <i class="bi bi-database-gear text-danger me-2" aria-hidden="true"></i>Database Backup Management
            </h1>
            <p class="text-muted mb-0">Schedule and trigger automated daily differential backups.</p>
        </div>
        <div class="d-flex gap-2">
            <form action="${pageContext.request.contextPath}/system/backups/trigger" method="post">
                <button type="submit" class="btn btn-danger">
                    <i class="bi bi-lightning-fill me-1"></i> Trigger Manual Backup
                </button>
            </form>
            <a href="${pageContext.request.contextPath}/system/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i> Back
            </a>
        </div>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success"><i class="bi bi-check-circle-fill me-2"></i> Backup process triggered successfully.</div>
    </c:if>

    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <h5 class="card-title mb-3">Recent Backup History</h5>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th>Backup ID</th>
                        <th>Date & Time</th>
                        <th>Type</th>
                        <th>Size</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="backup" items="${backups}">
                        <tr>
                            <td class="fw-semibold">#<c:out value="${backup.backupId}" /></td>
                            <td><c:out value="${backup.timestamp}" /></td>
                            <td>
                                <span class="badge bg-secondary"><c:out value="${backup.type}" /></span>
                            </td>
                            <td><c:out value="${backup.sizeMb}" /> MB</td>
                            <td>
                                <c:choose>
                                    <c:when test="${backup.status eq 'SUCCESS'}"><span class="text-success fw-bold"><i class="bi bi-check-circle"></i> Success</span></c:when>
                                    <c:otherwise><span class="text-danger fw-bold"><i class="bi bi-x-circle"></i> Failed</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>