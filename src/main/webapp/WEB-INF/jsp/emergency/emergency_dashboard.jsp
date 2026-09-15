<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Emergency Command Center - WMP</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../shared/header.jsp" />

<div class="container-fluid px-5 mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>🚨 Emergency Command Center</h2>
        <span class="badge bg-success p-2 fs-6">Live Polling Active</span>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0" id="emergencyTable">
                <thead class="table-dark">
                <tr>
                    <th>Req #</th>
                    <th>Priority</th>
                    <th>Patient Details</th>
                    <th>Location</th>
                    <th>Ambulance</th>
                    <th>Status / Assignment</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="req" items="${emergencies}">
                    <tr class="${req.priorityLevel == 'CRITICAL' ? 'table-danger' : ''}">
                        <td>#${req.requestId}</td>
                        <td>
                                    <span class="badge ${req.priorityLevel == 'CRITICAL' ? 'bg-danger' : (req.priorityLevel == 'URGENT' ? 'bg-warning text-dark' : 'bg-primary')}">
                                            ${req.priorityLevel}
                                    </span>
                        </td>
                        <td>
                            <strong>${req.patientName}</strong><br>
                            <small class="text-muted">📞 ${req.contactNumber}</small>
                        </td>
                        <td>${req.location}</td>
                        <td>
                            <c:if test="${req.requiresAmbulance}">
                                <span class="badge bg-danger">🚑 Dispatch Required</span>
                            </c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${req.status == 'PENDING'}">
                                    <span class="badge bg-secondary">Awaiting Dispatch</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success">Assigned: ${req.assignedDoctorName}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${req.status == 'PENDING'}">
                                <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#assignModal${req.requestId}">
                                    Assign Doctor
                                </button>
                            </c:if>
                        </td>
                    </tr>

                    <!-- Assignment Modal for each pending request -->
                    <div class="modal fade" id="assignModal${req.requestId}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="${pageContext.request.contextPath}/emergency/assign" method="post">
                                    <div class="modal-header bg-primary text-white">
                                        <h5 class="modal-title">Dispatch Doctor - Request #${req.requestId}</h5>
                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <input type="hidden" name="requestId" value="${req.requestId}">
                                        <p><strong>Patient:</strong> ${req.patientName}</p>
                                        <p><strong>Notes:</strong> ${req.description}</p>

                                        <label class="form-label fw-bold mt-3">Select Available Doctor</label>
                                        <select name="doctorId" class="form-select" required>
                                            <option value="" disabled selected>-- Choose On-Duty Doctor --</option>
                                            <c:forEach var="doc" items="${availableDoctors}">
                                                <option value="${doc.userID}">Dr. ${doc.firstName} ${doc.lastName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button type="submit" class="btn btn-primary">Confirm Dispatch</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty emergencies}">
                    <tr>
                        <td colspan="7" class="text-center py-4 text-muted">No active emergency requests at this time.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/emergency_polling.js"></script>
</body>
</html>