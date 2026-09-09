<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<jsp:useBean id="user" scope="session" class="com.webmedicalportaldemo.model.User" />

<%-- Clean Role-Based Access Control --%>
<c:if test="${empty sessionScope.user || sessionScope.user.role != 'PHARMACIST'}">
    <c:redirect url="/login" />
</c:if>

<html>
<head>
    <title>Pharmacist Dashboard | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .order-container { max-height: 650px; overflow-y: auto; }
        .bg-pharmacy { background: linear-gradient(45deg, #198754, #20c997); }
        .patient-card-group { transition: all 0.2s ease-in-out; }
        .patient-card-group:hover { border-color: #20c997 !important; }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>

<div class="container mt-4">
    <%-- Success/Error Alerts --%>
    <c:if test="${param.msg == 'success'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <strong>Success!</strong> Prescription status updated successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <%-- Profile Header --%>
    <div class="p-5 mb-4 bg-pharmacy text-white rounded-3 shadow">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h1 class="display-5 fw-bold text-white">Pharmacy Portal</h1>
                <div class="text-white">
                    <p class="fs-4 mb-0">Welcome, ${user.firstName} ${user.lastName}</p>
                    <p class="fs-6 opacity-75">
                        <i class="bi bi-patch-check-fill me-1"></i>
                        Licensed Pharmacist | Employee ID: ${not empty employee ? employee.employeeID : 'N/A'}
                    </p>
                </div>
            </div>
            <div style="width: 200px;">
                <button type="button" class="btn btn-warning w-100 fw-bold shadow-sm">
                    <i class="bi bi-box-seam me-1"></i> Inventory Log
                </button>
            </div>
        </div>
    </div>

    <%-- Multi-Search Filter Bar --%>
    <div class="row mb-3 g-2 align-items-center">
        <div class="col-md-3">
            <div class="input-group shadow-sm">
                <span class="input-group-text bg-white"><i class="bi bi-person-fill text-success"></i></span>
                <input type="text" id="patientSearch" class="form-control" placeholder="Search Patient ID...">
            </div>
        </div>
        <div class="col-md-4">
            <div class="input-group shadow-sm">
                <span class="input-group-text bg-white"><i class="bi bi-capsule text-primary"></i></span>
                <input type="text" id="rxSearch" class="form-control" placeholder="Search Rx ID or Medicine...">
            </div>
        </div>
        <div class="col-md-5 text-end">
            <div class="form-check form-switch d-inline-block align-middle mt-1">
                <input class="form-check-input" type="checkbox" id="toggleCompleted" checked>
                <label class="form-check-label fw-bold" for="toggleCompleted">Show Completed/Cancelled</label>
            </div>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center py-3">
            <h5 class="mb-0"><i class="bi bi-clipboard-pulse me-2"></i>Prescription Requests (Grouped by Patient)</h5>
            <span class="badge bg-success">System: MySQL (prescriptions)</span>
        </div>
        <div class="card-body p-3 order-container bg-light">
            <c:choose>
                <c:when test="${not empty groupedOrders}">
                    <c:forEach var="entry" items="${groupedOrders}">
                        <div class="patient-card-group mb-4 border rounded shadow-sm bg-white" data-patient-id="${entry.key}">
                            <div class="p-3 bg-white border-bottom d-flex justify-content-between align-items-center rounded-top">
                                <div class="fw-bold fs-5 text-dark">
                                    <i class="bi bi-person-badge-fill me-2 text-success"></i>Patient ID: #${entry.key}
                                </div>
                                <span class="badge bg-secondary opacity-75 fs-6">${entry.value.size()} Item(s)</span>
                            </div>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light small text-uppercase text-muted">
                                    <tr>
                                        <th style="width: 15%;">Rx ID</th>
                                        <th style="width: 35%;">Medicine Info</th>
                                        <th style="width: 20%;">Status</th>
                                        <th style="width: 30%;">Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="prescription" items="${entry.value}">
                                        <tr class="order-row">
                                            <td class="fw-bold rx-id">#${prescription.prescriptionID}</td>
                                            <td class="med-info">
                                                <span class="fw-semibold text-dark">${prescription.medicineName}</span>
                                                <span class="text-muted small ms-1">(x${prescription.quantity})</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${prescription.status == 'COMPLETED'}"><span class="badge bg-success status-badge">READY</span></c:when>
                                                    <c:when test="${prescription.status == 'CANCELLED'}"><span class="badge bg-danger status-badge">CANCELLED</span></c:when>
                                                    <c:when test="${prescription.status == 'PENDING'}"><span class="badge bg-primary status-badge">PENDING</span></c:when>
                                                    <c:otherwise><span class="badge bg-warning text-dark status-badge">${prescription.status}</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="d-flex gap-2">
                                                    <c:choose>
                                                        <c:when test="${prescription.status == 'PENDING'}">
                                                            <a href="${pageContext.request.contextPath}/updatePrescription?id=${prescription.prescriptionID}&action=complete" class="btn btn-sm btn-success">
                                                                Ready
                                                            </a>
                                                            <button class="btn btn-sm btn-outline-danger" onclick="confirmCancel('${prescription.prescriptionID}')">
                                                                Cancel
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn btn-sm btn-outline-secondary" disabled>Closed</button>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <button type="button" class="btn btn-info btn-sm text-white"
                                                            data-bs-toggle="modal" data-bs-target="#rxModal${prescription.prescriptionID}">
                                                        Details
                                                    </button>
                                                </div>

                                                    <%-- Prescription Detail Modal --%>
                                                <div class="modal fade" id="rxModal${prescription.prescriptionID}" tabindex="-1" aria-hidden="true">
                                                    <div class="modal-dialog modal-dialog-centered">
                                                        <div class="modal-content border-0 shadow">
                                                            <div class="modal-header bg-info text-white">
                                                                <h5 class="modal-title"><i class="bi bi-capsule me-2"></i>Prescription Summary</h5>
                                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body p-4">
                                                                <div class="row g-3">
                                                                    <div class="col-12">
                                                                        <label class="text-muted small fw-bold text-uppercase">Medicine Name</label>
                                                                        <p class="fw-bold fs-5 text-primary mb-0">${prescription.medicineName}</p>
                                                                    </div>
                                                                    <div class="col-6">
                                                                        <label class="text-muted small fw-bold text-uppercase">Quantity</label>
                                                                        <p class="mb-0 text-dark">${prescription.quantity} units</p>
                                                                    </div>
                                                                    <div class="col-6 text-end">
                                                                        <label class="text-muted small fw-bold text-uppercase">Total Cost</label>
                                                                        <p class="fw-bold text-success mb-0">
                                                                            <fmt:formatNumber value="${prescription.medicinePrice}" type="currency" currencySymbol="LKR" />
                                                                        </p>
                                                                    </div>
                                                                    <div class="col-12">
                                                                        <label class="text-muted small fw-bold text-uppercase">Status</label>
                                                                        <p class="mb-0"><span class="badge ${prescription.status == 'COMPLETED' ? 'bg-success' : 'bg-primary'}">${prescription.status}</span></p>
                                                                    </div>
                                                                    <div class="col-12 mt-3">
                                                                        <hr class="my-0 opacity-10">
                                                                    </div>
                                                                    <div class="col-12">
                                                                        <div class="alert alert-warning border-0 small mb-0 d-flex align-items-center">
                                                                            <i class="bi bi-exclamation-triangle-fill fs-4 me-2"></i>
                                                                            <span>Verify Patient ID <strong>#${prescription.patientID}</strong> before dispensing medication.</span>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer bg-light border-0">
                                                                <button type="button" class="btn btn-secondary btn-sm px-4" data-bs-dismiss="modal">Close</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5 text-muted bg-white border rounded">No prescriptions found in database.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmCancel(id) {
        if (confirm("Are you sure you want to cancel Prescription #" + id + "?")) {
            window.location.href = "updatePrescription?id=" + id + "&action=cancel";
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        const patientInput = document.getElementById('patientSearch');
        const rxInput = document.getElementById('rxSearch');
        const toggle = document.getElementById('toggleCompleted');

        function filterTable() {
            const patientQuery = patientInput.value.toLowerCase().trim();
            const rxQuery = rxInput.value.toLowerCase().trim();
            const showInactive = toggle.checked;

            document.querySelectorAll('.patient-card-group').forEach(patientGroup => {
                const patientId = patientGroup.getAttribute('data-patient-id').toString().toLowerCase();

                // Patient ID condition match
                let matchesPatientFilter = (patientQuery === '' || patientId.includes(patientQuery));
                let visibleCount = 0;

                patientGroup.querySelectorAll('.order-row').forEach(row => {
                    const rxId = row.querySelector('.rx-id').innerText.toLowerCase();
                    const medInfo = row.querySelector('.med-info').innerText.toLowerCase();
                    const status = row.querySelector('.status-badge').innerText.trim();

                    // Prescription ID / Medicine condition match
                    let matchesRxFilter = (rxQuery === '' || rxId.includes(rxQuery) || medInfo.includes(rxQuery));

                    // Status toggle condition
                    let isInactive = (status === 'READY' || status === 'COMPLETED' || status === 'CANCELLED');
                    let matchesStatus = showInactive || !isInactive;

                    if (matchesPatientFilter && matchesRxFilter && matchesStatus) {
                        row.style.display = "";
                        visibleCount++;
                    } else {
                        row.style.display = "none";
                    }
                });

                // Display card only if patient filter matches AND at least one row is visible
                patientGroup.style.display = (matchesPatientFilter && visibleCount > 0) ? "" : "none";
            });
        }

        patientInput.addEventListener('input', filterTable);
        rxInput.addEventListener('input', filterTable);
        toggle.addEventListener('change', filterTable);
    });
</script>
</body>
</html>