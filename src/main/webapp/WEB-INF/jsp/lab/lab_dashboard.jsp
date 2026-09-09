<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<html>
<head>
    <title>Lab Technician Dashboard | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .lab-container { max-height: 550px; overflow-y: auto; }
        .table thead th { position: sticky; top: 0; z-index: 10; background-color: #212529; }
    </style>
</head>
<body class="bg-light">

<%@ include file="../shared/header.jsp" %>

<div class="container mt-4 mb-5">
    <c:if test="${param.msg == 'status_updated'}">
        <div class="alert alert-info alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i> Sample tracking status updated successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.msg == 'results_submitted'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Diagnostic results logged and published to EHR.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="p-4 mb-4 text-white rounded-3 shadow" style="background: linear-gradient(135deg, #0d9488 0%, #14b8a6 100%);">
        <h1 class="display-6 fw-bold"><i class="bi bi-vial-mutt me-2"></i>Laboratory Operations</h1>
        <p class="fs-5 mb-0">Manage diagnostic sample pipelines and enter test results.</p>
    </div>

    <%-- Filter Section --%>
    <div class="card shadow border-0 mb-4">
        <div class="card-body">
            <div class="row g-3 align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                        <input type="text" id="searchInput" class="form-control" placeholder="Search by patient, doctor, or test name...">
                    </div>
                </div>
                <div class="col-md-6 text-end">
                    <select id="statusFilter" class="form-select d-inline-block w-auto">
                        <option value="ALL">All Statuses</option>
                        <option value="PENDING">Pending</option>
                        <option value="IN_TESTING">In Testing</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                </div>
            </div>
        </div>
    </div>

    <%-- Requests Table --%>
    <div class="card shadow border-0">
        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0"><i class="bi bi-list-task me-2"></i>Diagnostic Requests Queue</h5>
        </div>
        <div class="card-body p-0 lab-container">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-dark">
                <tr>
                    <th>Req #</th>
                    <th>Patient</th>
                    <th>Requested By</th>
                    <th>Test Name</th>
                    <th>Sample Pipeline</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty labRequests}">
                        <c:forEach var="req" items="${labRequests}">
                            <tr class="lab-row">
                                <td><strong>#${req.requestID}</strong></td>
                                <td><i class="bi bi-person me-1"></i>${req.patientName}</td>
                                <td>${req.doctorName}</td>
                                <td><span class="badge bg-secondary">${req.category}</span> ${req.testName}</td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        <i class="bi bi-box-seam me-1"></i>${req.sampleStatus}
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${req.status == 'PENDING'}">
                                            <span class="badge bg-warning text-dark"><i class="bi bi-clock me-1"></i>PENDING</span>
                                        </c:when>
                                        <c:when test="${req.status == 'IN_TESTING'}">
                                            <span class="badge bg-info text-dark"><i class="bi bi-gear-wide-connected me-1"></i>IN TESTING</span>
                                        </c:when>
                                        <c:when test="${req.status == 'COMPLETED'}">
                                            <span class="badge bg-success"><i class="bi bi-check-all me-1"></i>COMPLETED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${req.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex gap-2">
                                            <%-- Update Pipeline Modal Trigger --%>
                                        <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#pipelineModal${req.requestID}">
                                            <i class="bi bi-arrow-repeat me-1"></i>Pipeline
                                        </button>

                                            <%-- Upload Results Modal Trigger --%>
                                        <button class="btn btn-sm btn-success" data-bs-toggle="modal" data-bs-target="#resultsModal${req.requestID}">
                                            <i class="bi bi-file-earmark-medical me-1"></i>Results
                                        </button>
                                    </div>

                                        <%-- Pipeline Status Modal --%>
                                    <div class="modal fade" id="pipelineModal${req.requestID}" tabindex="-1">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">Update Sample Pipeline (#${req.requestID})</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/lab/updateStatus" method="post">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="requestId" value="${req.requestID}">
                                                        <div class="mb-3">
                                                            <label class="form-label fw-bold">Sample Status</label>
                                                            <select name="sampleStatus" class="form-select">
                                                                <option value="NOT_COLLECTED" ${req.sampleStatus == 'NOT_COLLECTED' ? 'selected' : ''}>Not Collected</option>
                                                                <option value="SAMPLE_COLLECTED" ${req.sampleStatus == 'SAMPLE_COLLECTED' ? 'selected' : ''}>Sample Collected</option>
                                                                <option value="IN_LAB" ${req.sampleStatus == 'IN_LAB' ? 'selected' : ''}>Received in Lab</option>
                                                            </select>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label class="form-label fw-bold">Overall Status</label>
                                                            <select name="status" class="form-select">
                                                                <option value="PENDING" ${req.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                                                <option value="IN_TESTING" ${req.status == 'IN_TESTING' ? 'selected' : ''}>In Testing</option>
                                                                <option value="COMPLETED" ${req.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                        <button type="submit" class="btn btn-primary">Save Changes</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>

                                        <%-- Results Entry Modal --%>
                                    <div class="modal fade" id="resultsModal${req.requestID}" tabindex="-1">
                                        <div class="modal-dialog modal-lg">
                                            <div class="modal-content">
                                                <div class="modal-header bg-success text-white">
                                                    <h5 class="modal-title"><i class="bi bi-journal-check me-2"></i>Log Diagnostic Results (#${req.requestID})</h5>
                                                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/lab/submitResults" method="post" enctype="multipart/form-data">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="requestId" value="${req.requestID}">

                                                        <div class="mb-3">
                                                            <label class="form-label fw-bold">Test Name</label>
                                                            <input type="text" class="form-control" value="${req.testName}" readonly>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label class="form-label fw-bold">Results Summary / Observations</label>
                                                            <textarea name="resultsSummary" class="form-control" rows="5" required placeholder="Enter key parameters (e.g., Hemoglobin: 14.2 g/dL, WBC: 6,500 /uL)...">${req.resultsSummary}</textarea>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label class="form-label fw-bold">Upload Digital PDF Report (Optional)</label>
                                                            <input type="file" name="reportFile" class="form-control" accept=".pdf,.png,.jpg">
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                        <button type="submit" class="btn btn-success"><i class="bi bi-send me-1"></i>Publish Results</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="7" class="text-center py-5 text-muted">No lab requests available in queue.</td></tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const searchInput = document.getElementById('searchInput');
        const statusFilter = document.getElementById('statusFilter');
        const rows = document.querySelectorAll('.lab-row');

        function filterTable() {
            const query = searchInput.value.toLowerCase();
            const filter = statusFilter.value;

            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                const statusBadge = row.cells[5].innerText.trim();

                let matchesSearch = text.includes(query);
                let matchesStatus = (filter === 'ALL') || statusBadge.includes(filter);

                row.style.display = (matchesSearch && matchesStatus) ? '' : 'none';
            });
        }

        searchInput.addEventListener('input', filterTable);
        statusFilter.addEventListener('change', filterTable);
    });
</script>
</body>
</html>