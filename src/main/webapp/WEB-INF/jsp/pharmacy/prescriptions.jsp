<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prescriptions | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<%@ include file="../shared/header.jsp" %>

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow border-0">
                <div class="card-header bg-primary text-white p-3">
                    <h4 class="mb-0"><i class="bi bi-capsule-pill me-2"></i>Order Prescription</h4>
                </div>
                <div class="card-body p-4">

                    <%-- Medicine Selection Area --%>
                    <div class="mb-4">
                        <label for="medSelect" class="form-label fw-bold">Select Medicine</label>
                        <div class="input-group">
                            <select id="medSelect" class="form-select">
                                <option value="" disabled selected>Choose medicine...</option>

                                <c:forEach var="item" items="${inventoryList}">
                                    <c:if test="${item.stockQuantity > 0}">
                                        <option value="<c:out value='${item.drugName}' />"
                                                data-price="${item.unitPrice}"
                                                data-stock="${item.stockQuantity}">
                                            <c:out value="${item.drugName}" /> (LKR ${item.unitPrice})
                                        </option>
                                    </c:if>
                                </c:forEach>

                            </select>
                            <input type="number" id="medQty" class="form-control" placeholder="Qty" min="1" value="1" style="max-width: 100px;" aria-label="Quantity">
                            <button type="button" class="btn btn-primary" onclick="addItem()">
                                <i class="bi bi-plus-lg"></i> Add
                            </button>
                        </div>
                    </div>

                    <%-- Cart Table --%>
                    <div class="table-responsive">
                        <table class="table table-hover border">
                            <thead class="table-light">
                            <tr>
                                <th>Medicine</th>
                                <th class="text-center">Qty</th>
                                <th class="text-end">Price (LKR)</th>
                                <th class="text-center">Action</th>
                            </tr>
                            </thead>
                            <tbody id="orderList">
                            <tr>
                                <td colspan="4" class="text-center text-muted py-4">Your prescription list is empty.</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>

                    <%-- Total Display --%>
                    <div class="d-flex justify-content-between align-items-center mt-3 p-3 bg-light rounded border mb-4">
                        <span class="fw-bold text-uppercase small text-muted">Estimated Total:</span>
                        <span id="grandTotal" class="fs-4 fw-bold text-success">LKR 0.00</span>
                    </div>

                    <%-- Main Form Submission --%>
                    <form id="prescriptionForm" action="submitPrescription" method="POST">
                        <c:if test="${not empty _csrf}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        </c:if>

                        <%-- Hidden Cart Input --%>
                        <input type="hidden" name="cartData" id="cartData">

                        <div class="d-grid gap-2 mt-4">
                            <button type="button" id="submitBtn" class="btn btn-success btn-lg" onclick="openConfirmModal()" disabled>
                                <i class="bi bi-check2-circle me-1"></i> Submit Prescription
                            </button>
                            <a href="${pageContext.request.contextPath}/patientDashboard" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- Confirmation Modal --%>
<div class="modal fade" id="confirmModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title">Confirm Prescription Order</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4 text-center">
                <i class="bi bi-question-circle text-success mb-3" style="font-size: 3rem;"></i>
                <p class="mb-0">Are you sure you want to submit this prescription? Once submitted, the pharmacist will begin processing your order.</p>
            </div>
            <div class="modal-footer bg-light border-0">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Wait, I need to check</button>
                <button type="button" class="btn btn-success px-4" onclick="finalSubmit()">Yes, Submit</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let cart = [];

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function addItem() {
        const medSelect = document.getElementById('medSelect');
        const selected = medSelect.options[medSelect.selectedIndex];
        const qty = parseInt(document.getElementById('medQty').value, 10);

        const name = selected ? selected.value : "";
        const price = selected ? parseFloat(selected.dataset.price) : NaN;
        const stock = selected ? parseInt(selected.dataset.stock, 10) : 0;

        if (!name || Number.isNaN(price) || !(qty >= 1)) {
            alert("Please select a medicine and valid quantity.");
            return;
        }

        const alreadyInCart = cart
            .filter(function (entry) { return entry.name === name; })
            .reduce(function (sum, entry) { return sum + entry.qty; }, 0);

        if (!Number.isNaN(stock) && alreadyInCart + qty > stock) {
            alert("Only " + stock + " unit(s) of " + name + " are in stock.");
            return;
        }

        cart.push({
            name: name,
            price: price,
            qty: qty,
            total: price * qty
        });

        medSelect.selectedIndex = 0;
        document.getElementById('medQty').value = 1;

        renderTable();
    }

    function renderTable() {
        const tbody = document.getElementById('orderList');
        const submitBtn = document.getElementById('submitBtn');
        let total = 0;

        if (cart.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-4">Your prescription list is empty.</td></tr>';
            submitBtn.disabled = true;
            document.getElementById('grandTotal').innerText = "LKR 0.00";
            return;
        }

        tbody.innerHTML = '';
        submitBtn.disabled = false;

        cart.forEach((item, index) => {
            total += item.total;
            tbody.innerHTML += `
            <tr>
                <td class="fw-bold">\${escapeHtml(item.name)}</td>
                <td class="text-center">\${item.qty}</td>
                <td class="text-end">\${item.total.toFixed(2)}</td>
                <td class="text-center">
                    <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeItem(\${index})" aria-label="Remove item">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>`;
        });
        document.getElementById('grandTotal').innerText = "LKR " + total.toFixed(2);
    }

    function removeItem(index) {
        cart.splice(index, 1);
        renderTable();
    }

    function openConfirmModal() {
        const modal = new bootstrap.Modal(document.getElementById('confirmModal'));
        modal.show();
    }

    function finalSubmit() {
        if (cart.length === 0) {
            alert("Cannot submit an empty cart.");
            return;
        }

        const cartString = cart.map(item => `\${item.name},\${item.qty},\${item.price}`).join(';');
        document.getElementById('cartData').value = cartString;
        document.getElementById('prescriptionForm').submit();
    }
</script>
</body>
</html>