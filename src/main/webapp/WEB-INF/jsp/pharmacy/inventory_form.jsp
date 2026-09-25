<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>${isEdit ? 'Edit Drug' : 'Add Drug'} | Pharmacy Portal</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<div class="container mt-5" style="max-width: 600px;">
    <div class="card shadow border-0">
        <div class="card-header bg-success text-white p-3">
            <h4 class="mb-0">
                <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-plus-circle'} me-2"></i>
                ${isEdit ? 'Edit Drug Details' : 'Add New Drug to Inventory'}
            </h4>
        </div>
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/pharmacist/inventory/save" method="post">
                <c:if test="${isEdit}">
                    <input type="hidden" name="itemId" value="${item.itemId}">
                </c:if>

                <div class="mb-3">
                    <label class="form-label fw-bold">Drug Name</label>
                    <input type="text" name="drugName" class="form-control" value="${item.drugName}" required>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Unit Price (LKR)</label>
                        <input type="number" step="0.01" name="unitPrice" class="form-control" value="${item.unitPrice}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Stock Quantity</label>
                        <input type="number" name="stockQuantity" class="form-control" value="${item.stockQuantity}" required>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/pharmacist/inventory" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i> Cancel
                    </a>
                    <button type="submit" class="btn btn-success px-4">
                        <i class="bi bi-check-circle me-1"></i> Save Drug
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>