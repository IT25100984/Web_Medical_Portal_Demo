<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Inventory | Pharmacy Portal</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<div class="container mt-4 mb-5">
    <c:if test="${not empty param.msg}">
        <div class="alert alert-success alert-dismissible fade show">
            <i class="bi bi-check-circle-fill me-2"></i> Inventory updated successfully.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="p-4 mb-4 text-white rounded-3 shadow" style="background: linear-gradient(135deg, #059669 0%, #047857 100%);">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h1 class="display-6 fw-bold"><i class="bi bi-box-seam me-2"></i>Pharmacy Inventory</h1>
                <p class="mb-0 text-light opacity-75">Manage drug pricing and stock levels.</p>
            </div>
            <a href="${pageContext.request.contextPath}/pharmacist/inventory/add" class="btn btn-warning btn-lg shadow-sm text-dark fw-bold">
                <i class="bi bi-plus-circle me-2"></i>Add New Drug
            </a>
        </div>
    </div>

    <div class="card shadow border-0">
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Drug Name</th>
                    <th>Unit Price (LKR)</th>
                    <th>Stock Quantity</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty inventory}">
                        <c:forEach var="item" items="${inventory}">
                            <tr>
                                <td><span class="badge bg-secondary font-monospace">#${item.itemId}</span></td>
                                <td><strong>${item.drugName}</strong></td>
                                <td>${item.unitPrice}</td>
                                <td>${item.stockQuantity}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.stockQuantity > 50}"><span class="badge bg-success">In Stock</span></c:when>
                                        <c:when test="${item.stockQuantity > 0}"><span class="badge bg-warning text-dark">Low Stock</span></c:when>
                                        <c:otherwise><span class="badge bg-danger">Out of Stock</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/pharmacist/inventory/edit?id=${item.itemId}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-pencil-square"></i> Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/pharmacist/inventory/delete" method="post" class="d-inline" onsubmit="return confirm('Delete this drug from inventory?');">
                                            <input type="hidden" name="itemId" value="${item.itemId}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="6" class="text-center py-5 text-muted">Inventory is empty.</td></tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>