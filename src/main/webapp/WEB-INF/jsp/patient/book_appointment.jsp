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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <style>
        .booking-card {
            max-width: 600px;
        }
        .price-preview {
            font-size: 1.35rem;
        }
        .history-container {
            max-height: 240px;
            overflow-y: auto;
        }
        .step-divider {
            border-top: 1px dashed #cfdcea;
            margin: 1.5rem 0;
        }
    </style>
</head>
<body class="auth-page">
<%@ include file="../shared/header.jsp" %>
<main class="container py-4 py-md-5">
    <div class="card auth-card booking-card shadow border-0 mx-auto">
        <div class="auth-card-header">
            <div class="auth-logo"><i class="bi bi-calendar-check" aria-hidden="true"></i></div>
            <h1 class="h4 fw-bold mb-1">Schedule Appointment</h1>
            <p class="small mb-0 opacity-75">Choose a doctor, a date and a time that suits you</p>
        </div>
        <div class="card-body p-4">
            <c:if test="${param.error eq 'missingTime'}">
                <div class="alert alert-warning shadow-sm border-0" role="alert">
                    <i class="bi bi-clock-fill me-1" aria-hidden="true"></i>Please select an available appointment time.
                </div>
            </c:if>
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
            <form id="bookingForm" action="${bookAppointmentUrl}" method="POST">
                <c:if test="${not empty _csrf}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </c:if>

                <%-- STEP 1: appointment type --%>
                <div class="step-label"><span class="step-badge">1</span> Appointment Type</div>
                <div class="mb-3">
                    <label for="typeSelect" class="form-label fw-semibold">What do you need?</label>
                    <select class="form-select" id="typeSelect" name="appointmentType" required>
                        <option value="CONSULTATION" selected>General Consultation</option>
                        <option value="SURGERY">Surgery / Operation</option>
                    </select>
                </div>
                <div class="mb-3 p-3 bg-warning-subtle rounded-3 border border-warning d-none" id="surgeryOptions">
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

                <div class="step-divider"></div>

                <%-- STEP 2: doctor --%>
                <div class="step-label"><span class="step-badge">2</span> Choose Your Doctor</div>
                <div class="mb-3">
                    <label for="specializationSelect" class="form-label fw-semibold">Specialization</label>
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
                    <label for="doctorSelect" class="form-label fw-semibold">Doctor</label>
                    <div class="input-group">
                        <select name="doctorID" id="doctorSelect" class="form-select" required>
                            <option value="" selected disabled>Choose a doctor...</option>
                            <c:forEach var="doc" items="${doctorList}">
                                <option value="${doc.userID}" data-doctor-id="${doc.doctorID}">
                                    Dr. <c:out value="${doc.firstName}" /> <c:out value="${doc.lastName}" />
                                </option>
                            </c:forEach>
                        </select>
                        <button class="btn btn-outline-primary" type="button" id="viewHistoryBtn" disabled>
                            <i class="bi bi-clock-history" aria-hidden="true"></i> History
                        </button>
                    </div>
                </div>
                <div id="historyDisplay" class="history-container mt-2 mb-3 shadow-sm p-3 bg-white border rounded-3 d-none">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h2 class="h6 mb-0">Appointment Billing History</h2>
                        <button type="button" class="btn-close" id="closeHistoryButton" aria-label="Close history"></button>
                    </div>
                    <ul id="historyList" class="list-group list-group-flush small"></ul>
                </div>

                <div class="step-divider"></div>

                <%-- STEP 3: date and time --%>
                <div class="step-label"><span class="step-badge">3</span> Pick a Date &amp; Time</div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="datePicker" class="form-label fw-semibold">Date</label>
                        <input type="date" name="date" id="datePicker" class="form-control" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="timeSlotSelect" class="form-label fw-semibold">Available Time Slots</label>
                        <select name="time" id="timeSlotSelect" class="form-select" required disabled>
                            <option value="" selected disabled>Choose a date and doctor first...</option>
                        </select>
                        <div class="invalid-feedback">Please select an available appointment time.</div>
                    </div>
                </div>

                <%-- Estimate --%>
                <div class="price-box p-3 d-flex justify-content-between align-items-center mt-2 mb-2">
                    <span class="fw-bold text-uppercase small text-secondary">
                        <i class="bi bi-cash-stack me-1" aria-hidden="true"></i>Estimated Total
                    </span>
                    <span id="pricePreview" class="price-preview fw-bold text-primary">LKR 1,500.00</span>
                </div>
                <p class="small text-muted mb-4">The displayed amount is an estimate. WMP recalculates and stores the final fee securely on the server when the appointment is booked.</p>

                <div class="d-grid gap-2">
                    <button type="submit" id="bookingButton" class="btn btn-success btn-lg fw-bold">
                        <i class="bi bi-check2-circle me-1" aria-hidden="true"></i> Confirm Booking
                    </button>
                    <c:url var="patientDashboardUrl" value="/patientDashboard" />
                    <a href="${patientDashboardUrl}" class="btn btn-outline-secondary">Cancel</a>
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
        // Local calendar date as YYYY-MM-DD. (toISOString() uses UTC, which is the previous day
        // for the first 5.5 hours after midnight in Sri Lanka.)
        function toLocalISODate(date) {
            const month = String(date.getMonth() + 1).padStart(2, "0");
            const day = String(date.getDate()).padStart(2, "0");
            return date.getFullYear() + "-" + month + "-" + day;
        }
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
                        option.dataset.doctorId = doctor.doctorID;
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
            const selectedOption = doctorSelect.options[doctorSelect.selectedIndex];
            const doctorID = selectedOption && selectedOption.dataset ? selectedOption.dataset.doctorId : "";
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
                    timeSlotSelect.classList.remove("is-invalid");
                    timeSlotSelect.disabled = false;
                })
                .catch(function (error) {
                    console.error("Unable to load work schedule:", error);
                    timeSlotSelect.innerHTML = '<option value="" selected disabled>Error loading slots</option>';
                    timeSlotSelect.disabled = true;
                });
        }
        function fetchAppointmentHistory() {
            const doctorUserID = doctorSelect.value;
            if (!doctorUserID) {
                return;
            }
            historyDisplay.classList.remove("d-none");
            historyList.innerHTML = '<li class="list-group-item text-muted">Loading appointment history...</li>';
            fetch("${pageContext.request.contextPath}/history?doctorID=" + encodeURIComponent(doctorUserID))
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
                        const wrapper = document.createElement("div");
                        wrapper.className = "d-flex justify-content-between align-items-center";
                        const details = document.createElement("div");
                        const typeBadge = document.createElement("span");
                        typeBadge.className = "badge bg-secondary me-1";
                        typeBadge.textContent = parts[3];
                        const dateText = document.createElement("small");
                        dateText.className = "text-muted";
                        const parsedDate = new Date(parts[5]);
                        dateText.textContent = Number.isNaN(parsedDate.getTime()) ? parts[5] : parsedDate.toLocaleDateString();
                        const amount = document.createElement("span");
                        amount.className = "fw-bold text-success";
                        const parsedAmount = Number.parseFloat(parts[4]);
                        amount.textContent = "LKR " + (Number.isNaN(parsedAmount) ? "0.00" : parsedAmount.toFixed(2));
                        details.appendChild(typeBadge);
                        details.appendChild(dateText);
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
        const todayObj = new Date();
        const today = toLocalISODate(todayObj);
        datePicker.min = today;
        datePicker.value = today;
        const maxDateObj = new Date();
        maxDateObj.setDate(todayObj.getDate() + 90);
        datePicker.max = toLocalISODate(maxDateObj);
        typeSelect.addEventListener("change", updatePricePreview);
        extraCharge.addEventListener("change", updatePricePreview);
        specializationSelect.addEventListener("change", updateDoctorList);
        doctorSelect.addEventListener("change", function () {
            viewHistoryBtn.disabled = doctorSelect.value === "";
            historyDisplay.classList.add("d-none");
            doctorSelect.classList.remove("is-invalid");
            fetchSlots();
        });
        datePicker.addEventListener("change", function () {
            datePicker.classList.remove("is-invalid");
            fetchSlots();
        });
        viewHistoryBtn.addEventListener("click", fetchAppointmentHistory);
        closeHistoryButton.addEventListener("click", function () {
            historyDisplay.classList.add("d-none");
        });
        timeSlotSelect.addEventListener("change", function () {
            if (timeSlotSelect.value) {
                timeSlotSelect.classList.remove("is-invalid");
            }
        });
        bookingForm.addEventListener("submit", function (event) {
            if (!doctorSelect.value) {
                event.preventDefault();
                doctorSelect.classList.add("is-invalid");
                doctorSelect.focus();
                return;
            }
            if (!datePicker.value) {
                event.preventDefault();
                datePicker.classList.add("is-invalid");
                datePicker.focus();
                return;
            }
            if (timeSlotSelect.disabled || !timeSlotSelect.value) {
                event.preventDefault();
                timeSlotSelect.classList.add("is-invalid");
                timeSlotSelect.focus();
                window.alert("Please select an available appointment time before confirming the booking.");
                return;
            }
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
