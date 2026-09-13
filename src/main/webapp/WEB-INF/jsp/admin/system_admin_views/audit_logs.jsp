<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Audit Trails (PBI-22) | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<%@ include file="../../shared/header.jsp" %>
<main class="container pb-5 mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">
                <i class="bi bi-journal-text text-warning me-2" aria-hidden="true"></i>System Audit Trails
            </h1>
            <p class="text-muted mb-0">Review system actions, critical alerts, and emergency logs.</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/system/dashboard" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
            </a>
        </div>
    </div>

    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <form class="row g-3 align-items-end" action="${pageContext.request.contextPath}/system/auditLogs" method="get">
                <div class="col-md-3">
                    <label class="form-label">Filter by Role/User</label>
                    <input type="text" name="userFilter" class="form-control" placeholder="Search user...">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Severity</label>
                    <select name="severity" class="form-select">
                        <option value="">All</option>
                        <option value="INFO">Info</option>
                        <option value="WARNING">Warning</option>
                        <option value="CRITICAL">Critical</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100"><i class="bi bi-filter"></i> Filter</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th>Timestamp</th>
                        <th>User ID</th>
                        <th>Action Taken</th>
                        <th>Severity</th>
                        <th>IP Address</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="log" items="${auditLogs}">
                        <tr>
                            <td><c:out value="${log.timestamp}" /></td>
                            <td><c:out value="${log.userID}" /></td>
                            <td><c:out value="${log.action}" /></td>
                            <td>
                                    <span class="badge ${log.severity eq 'CRITICAL' ? 'bg-danger' : (log.severity eq 'WARNING' ? 'bg-warning' : 'bg-info')}">
                                        <c:out value="${log.severity}" />
                                    </span>
                            </td>
                            <td class="text-muted"><small><c:out value="${log.ipAddress}" /></small></td>
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