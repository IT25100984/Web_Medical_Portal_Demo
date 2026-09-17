<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Book Appointment | WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .booking-card {
            max-width: 560px;
        }
        .price-preview {
            font-size: 1.25rem;
        }
        .history-container {
            max-height: 240px;
            overflow-y: auto;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="../shared/header.jsp" %>
<main class="container py-5">
    <div class="card booking-card shadow border-0 mx-auto">
        <div class="card-header bg-primary text-white py-3">
            <h1 class="h4 mb-0 fw-bold">
                <i class="bi bi-calendar-check me-2" aria-hidden="true"></i>Schedule Appointment
            </h1>
        </div>
        <div class="card-body p-4">
            <c:if test="${param.error eq 'invalidInput'}">
                <div class="alert alert-danger shadow-sm border-0" role="alert">
                    <i class="bi bi-exclamation-octagon-fill me-1" aria-hidden="true"></i>Please complete all required booking fields.
                </div>
            </c:if>
            <c:if test="${param.error eq 'invalidDateTime'}">
                <div class="alert alert-danger shadow-sm border-0" role="alert">
                    <i class="bi bi-calendar-x-fill me-1" aria-hidden="true"></i>Please select a valid future appointment date and time.
                </div>
            </c:if>
            <c:if test="${param.error eq 'slotUnavailable'}">
                <div class="alert alert-warning shadow-sm border-0" role="alert">
                    <i class="bi bi-clock-fill me-1" aria-hidden="true"></i>The selected appointment slot is no longer available.
                </div>
            </c:if>
            <c:url var="bookAppointmentUrl" value="/bookAppointment" />
            <form id="bookingForm" action="${bookAppointmentUrl}" method="POST">            <c:if test="${not empty _csrf}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </c:if>
            <div class="mb-3">
                <label for="typeSelect" class="form-label fw-semibold">Appointment Type</label>
                <select class="form-select border-primary-subtle" id="typeSelect" name="appointmentType" required>
                    <option value="CONSULTATION" selected>General Consultation</option>
                    <option value="SURGERY">Surgery / Operation</option>
                </select>
            </div>
            <div class="mb-3 p-3 bg-warning-subtle rounded border border-warning d-none" id="surgeryOptions">
                <label for="extraCharge" class="form-label fw-semibold text-warning-emphasis">Additional Surgical Requirement</label>
                <select class="form-select" id="extraCharge" name="additionalCharge" disabled>
                    <option value="NONE" selected>Standard Surgery</option>
                    <option value="ANESTHESIA">Anesthesia Service (+LKR 2,500.00)</option>
                    <option value="FACILITY">Facility / Hospital Charge (+LKR 1,500.00)</option>
                    <option value="EQUIPMENT">Specialized Equipment (+LKR 3,000.00)</option>
                    <option value="OTHER">Other Requirement (+LKR 500.00)</option>
                </select>
                <div class="form-text">Base surgery fee: LKR 5,000.00</div>
            </div>
            <div class="mb-3">
                <label for="specializationSelect" class="form-label fw-semibold">Select Specialization</label>
                <select id="specializationSelect" class="form-select">
                    <option value="" selected>All Specializations</option>
                    <option value="General Practitioner">General Practitioner</option>
                    <option value="Dermatology">Dermatology</option>
                    <option value="Cardiology">Cardiology</option>
                    <option value="Pediatric">Pediatric</option>
                    <option value="Neurology">Neurology</option>
                    <option value="Orthopedic">Orthopedic</option>
                </select>
                <div class="form-text">Pharmacists are staff members and are not listed as medical specialists.</div>
            </div>
            <div class="mb-3">
                <label for="doctorSelect" class="form-label fw-semibold">Select Doctor</label>
                <div class="input-group">
                    <select name="doctorID" id="doctorSelect" class="form-select" required>
                        <option value="" selected disabled>Choose a doctor...</option>
                        <c:forEach var="doc" items="${doctorList}">
                            <option value="${doc.userID}">
                                Dr. <c:out value="${doc.firstName}" /> <c:out value="${doc.lastName}" />
                            </option>
                        </c:forEach>
                    </select>
                    <button class="btn btn-outline-primary" type="button" id="viewHistoryBtn" disabled>
                        <i class="bi bi-clock-history" aria-hidden="true"></i> History
                    </button>
                </div>
            </div>
            <div id="historyDisplay" class="history-container mt-2 mb-3 shadow-sm p-3 bg-white border rounded d-none">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h2 class="h6 mb-0">Appointment Billing History</h2>
                    <button type="button" class="btn-close" id="closeHistoryButton" aria-label="Close history"></button>
                </div>
                <ul id="historyList" class="list-group list-group-flush small"></ul>
            </div>
            <div class="mb-3">
                <label for="datePicker" class="form-label fw-semibold">Select Date</label>
                <input type="date" name="date" id="datePicker" class="form-control" required>
            </div>
            <div class="mb-4">
                <label for="timeSlotSelect" class="form-label fw-semibold">Available Time Slots</label>
                <select name="time" id="timeSlotSelect" class="form-select" required disabled>
                    <option value="" selected disabled>Choose a date and doctor first...</option>
                </select>
            </div>
            <div class="alert alert-info py-2 shadow-sm d-flex justify-content-between align-items-center mb-2">
                    <span class="fw-bold text-uppercase small">
                        <i class="bi bi-cash-stack me-1" aria-hidden="true"></i>Estimated Total
                    </span>
                <span id="pricePreview" class="price-preview fw-bold text-primary">LKR 1,500.00</span>
            </div>
            <p class="small text-muted mb-3">The displayed amount is an estimate. WMP recalculates and stores the final fee securely on the server when the appointment is booked.</p>
            <div class="d-grid gap-2">
                <button type="submit" id="bookingButton" class="btn btn-success fw-bold">Confirm Booking</button>
                <c:url var="patientDashboardUrl" value="/patientDashboard" />
                <a href="${patientDashboardUrl}" class="btn btn-secondary fw-bold">Cancel</a>
            </div>
            </form>
        </div>
    </div>
</main>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const typeSelect = document.getElementById("typeSelect");
        const surgeryOptions = document.getElementById("surgeryOptions");
        const extraCharge = document.getElementById("extraCharge");
        const specializationSelect = document.getElementById("specializationSelect");
        const doctorSelect = document.getElementById("doctorSelect");
        const datePicker = document.getElementById("datePicker");
        const timeSlotSelect = document.getElementById("timeSlotSelect");
        const viewHistoryBtn = document.getElementById("viewHistoryBtn");
        const historyDisplay = document.getElementById("historyDisplay");
        const historyList = document.getElementById("historyList");
        const closeHistoryButton = document.getElementById("closeHistoryButton");
        const pricePreview = document.getElementById("pricePreview");
        const bookingForm = document.getElementById("bookingForm");
        const bookingButton = document.getElementById("bookingButton");
        const basePrices = {
            CONSULTATION: 1500,
            SURGERY: 5000
        };
        const additionalPrices = {
            NONE: 0,
            ANESTHESIA: 2500,
            FACILITY: 1500,
            EQUIPMENT: 3000,
            OTHER: 500
        };
        function updatePricePreview() {
            const appointmentType = typeSelect.value;
            const isSurgery = appointmentType === "SURGERY";
            surgeryOptions.classList.toggle("d-none", !isSurgery);
            extraCharge.disabled = !isSurgery;
            if (!isSurgery) {
                extraCharge.value = "NONE";
            }
            const basePrice = basePrices[appointmentType] || 0;
            const extraPrice = isSurgery ? additionalPrices[extraCharge.value] || 0 : 0;
            pricePreview.textContent = "LKR " + (basePrice + extraPrice).toLocaleString("en-LK", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });
        }
        function resetDoctorSelection() {
            doctorSelect.innerHTML = '<option value="" selected disabled>Choose a doctor...</option>';
            timeSlotSelect.innerHTML = '<option value="" selected disabled>Choose a date and doctor first...</option>';
            timeSlotSelect.disabled = true;
            viewHistoryBtn.disabled = true;
            historyDisplay.classList.add("d-none");
        }
        function updateDoctorList() {
            const specialization = specializationSelect.value;
            resetDoctorSelection();
            const url = "${pageContext.request.contextPath}/getDoctorsBySpec?specialization=" + encodeURIComponent(specialization);
            fetch(url)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Doctor request failed with status " + response.status);
                    }
                    return response.json();
                })
                .then(function (doctors) {
                    if (!doctors || doctors.length === 0) {
                        doctorSelect.innerHTML = '<option value="" selected disabled>No doctors available</option>';
                        return;
                    }
                    doctors.forEach(function (doctor) {
                        const option = document.createElement("option");
                        option.value = doctor.userID;
                        option.textContent = "Dr. " + (doctor.firstName || "") + " " + (doctor.lastName || "");
                        doctorSelect.appendChild(option);
                    });
                })
                .catch(function (error) {
                    console.error(error);
                    doctorSelect.innerHTML = '<option value="" selected disabled>Error loading doctors</option>';
                });
        }
        function fetchSlots() {
            const doctorID = doctorSelect.value;
            const selectedDate = datePicker.value;
            if (!doctorID || !selectedDate) {
                timeSlotSelect.disabled = true;
                timeSlotSelect.innerHTML = '<option value="" selected disabled>Choose a date and doctor first...</option>';
                return;
            }
            timeSlotSelect.disabled = true;
            timeSlotSelect.innerHTML = '<option value="" selected disabled>Loading available hours...</option>';
            const parameters = new URLSearchParams();
            parameters.append("doctorID", doctorID);
            parameters.append("date", selectedDate);
            fetch("${pageContext.request.contextPath}/getAvailableSlots?" + parameters.toString())
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Slot request failed with status " + response.status);
                    }
                    return response.json();
                })
                .then(function (slots) {
                    timeSlotSelect.innerHTML = "";
                    if (!slots || slots.length === 0) {
                        timeSlotSelect.innerHTML = '<option value="" selected disabled>No available hours found</option>';
                        timeSlotSelect.disabled = true;
                        return;
                    }
                    timeSlotSelect.innerHTML = '<option value="" selected disabled>Choose a time...</option>';
                    slots.forEach(function (slot) {
                        const option = document.createElement("option");
                        option.value = slot;
                        option.textContent = slot;
                        timeSlotSelect.appendChild(option);
                    });
                    timeSlotSelect.disabled = false;
                })
                .catch(function (error) {
                    console.error(error);
                    timeSlotSelect.innerHTML = '<option value="" selected disabled>Error loading slots</option>';
                    timeSlotSelect.disabled = true;
                });
        }
        function fetchAppointmentHistory() {
            const doctorID = doctorSelect.value;
            if (!doctorID) {
                return;
            }
            historyDisplay.classList.remove("d-none");
            historyList.innerHTML = '<li class="list-group-item text-muted">Loading appointment history...</li>';
            fetch("${pageContext.request.contextPath}/history?doctorID=" + encodeURIComponent(doctorID))
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("History request failed with status " + response.status);
                    }
                    return response.json();
                })
                .then(function (historyEntries) {
                    historyList.innerHTML = "";
                    if (!historyEntries || historyEntries.length === 0) {
                        historyList.innerHTML = '<li class="list-group-item text-center">No previous history found.</li>';
                        return;
                    }
                    historyEntries.forEach(function (line) {
                        const parts = line.split("|");
                        if (parts.length < 6) {
                            return;
                        }
                        const listItem = document.createElement("li");
                        listItem.className = "list-group-item px-0";
                        const appointmentType = document.createElement("span");
                        appointmentType.className = "badge bg-secondary me-1";
                        appointmentType.textContent = parts[3];
                        const appointmentDate = document.createElement("small");
                        appointmentDate.className = "text-muted";
                        const parsedDate = new Date(parts[5]);
                        appointmentDate.textContent = Number.isNaN(parsedDate.getTime()) ? parts[5] : parsedDate.toLocaleDateString();
                        const amount = document.createElement("span");
                        amount.className = "fw-bold text-success";
                        const parsedAmount = Number.parseFloat(parts[4]);
                        amount.textContent = "LKR " + (Number.isNaN(parsedAmount) ? "0.00" : parsedAmount.toFixed(2));
                        const details = document.createElement("div");
                        details.appendChild(appointmentType);
                        details.appendChild(appointmentDate);
                        const wrapper = document.createElement("div");
                        wrapper.className = "d-flex justify-content-between align-items-center";
                        wrapper.appendChild(details);
                        wrapper.appendChild(amount);
                        listItem.appendChild(wrapper);
                        historyList.appendChild(listItem);
                    });
                })
                .catch(function (error) {
                    console.error(error);
                    historyList.innerHTML = '<li class="list-group-item text-danger">Error loading appointment history.</li>';
                });
        }
        const today = new Date().toISOString().split("T")[0];
        datePicker.min = today;
        datePicker.value = today;
        typeSelect.addEventListener("change", updatePricePreview);
        extraCharge.addEventListener("change", updatePricePreview);
        specializationSelect.addEventListener("change", updateDoctorList);
        doctorSelect.addEventListener("change", function () {
            viewHistoryBtn.disabled = doctorSelect.value === "";
            historyDisplay.classList.add("d-none");
            fetchSlots();
        });
        datePicker.addEventListener("change", fetchSlots);
        viewHistoryBtn.addEventListener("click", fetchAppointmentHistory);
        closeHistoryButton.addEventListener("click", function () {
            historyDisplay.classList.add("d-none");
        });
        bookingForm.addEventListener("submit", function (event) {
            if (!bookingForm.checkValidity()) {
                event.preventDefault();
                bookingForm.classList.add("was-validated");
                return;
            }
            bookingButton.disabled = true;
            bookingButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>Booking...';
        });
        updatePricePreview();
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<jsp:include page="/WEB-INF/jsp/ai/ai_assistant_chat.jsp" />
</body>
</html>