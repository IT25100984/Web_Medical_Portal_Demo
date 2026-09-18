<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<html>
<head>
    <title>${isEdit ? 'Edit Employee' : 'Add New Employee'} | Hospital Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<%@ include file="../shared/header.jsp" %>

<div class="container mt-5" style="max-width: 650px;">
    <div class="card shadow border-0">
        <div class="card-header bg-dark text-white p-3">
            <h4 class="mb-0">
                <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-person-plus-fill'} me-2"></i>
                ${isEdit ? 'Edit Employee Record' : 'Register New Hospital Staff'}
            </h4>
        </div>
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/admin/employees/save" method="post">

                <div class="mb-3">
                    <label class="form-label fw-bold">Employee ID</label>
                    <input type="text" name="employeeId" class="form-control" value="${employee.employeeId}"
                           placeholder="e.g. DOC002, LAB002, PHA002" ${isEdit ? 'readonly' : 'required'}>
                    <div class="form-text">Unique company ID used during account signup validation.</div>
                </div>

                <div class="row g-3 mb-3">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">First Name</label>
                        <input type="text" name="firstName" class="form-control" value="${employee.firstName}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Last Name</label>
                        <input type="text" name="lastName" class="form-control" value="${employee.lastName}" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Official Email Address</label>
                    <input type="email" name="email" class="form-control" value="${employee.email}" placeholder="employee@hospital.com" required>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Role</label>
                        <select name="role" class="form-select" required>
                            <option value="" disabled ${empty employee.role ? 'selected' : ''}>Select Role...</option>
                            <option value="DOCTOR" ${employee.role == 'DOCTOR' ? 'selected' : ''}>DOCTOR</option>
                            <option value="PHARMACIST" ${employee.role == 'PHARMACIST' ? 'selected' : ''}>PHARMACIST</option>
                            <option value="LAB_TECHNICIAN" ${employee.role == 'LAB_TECHNICIAN' ? 'selected' : ''}>LAB_TECHNICIAN</option>
                            <option value="HOSPITAL_ADMIN" ${employee.role == 'HOSPITAL_ADMIN' ? 'selected' : ''}>HOSPITAL_ADMIN</option>
                            <option value="SYSTEM_ADMIN" ${employee.role == 'SYSTEM_ADMIN' ? 'selected' : ''}>SYSTEM_ADMIN</option>

                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Department</label>
                        <input type="text" name="department" class="form-control" value="${employee.department}" placeholder="e.g. Cardiology, Pharmacy, Laboratory" required>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/admin/employees" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i> Cancel
                    </a>
                    <button type="submit" class="btn btn-success px-4">
                        <i class="bi bi-check-circle me-1"></i> Save Employee
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>