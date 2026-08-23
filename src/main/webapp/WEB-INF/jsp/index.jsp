<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home | ProjectMASSx</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        .hero-section {min-height: 520px;display: flex; align-items: center;background:
                    linear-gradient(135deg, rgba(13, 110, 253, 0.10), rgba(25, 135, 84, 0.10));
        }
        .feature-card {
            height: 100%;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .feature-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.10);
        }
        .feature-icon {
            width: 56px;
            height: 56px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            background-color: rgba(13, 110, 253, 0.12);
            color: #0d6efd;
            font-size: 1.5rem;
            font-weight: bold;
        }
    </style>
</head>

<body class="bg-light">
<%@ include file="shared/header.jsp" %>
<main>
    <%-- Main hero section --%>
    <section class="hero-section">
        <div class="container py-5">
            <div class="row align-items-center">
                <div class="col-lg-7">
                    <span class="badge bg-primary mb-3">Smart Hospital Management</span>
                    <h1 class="display-4 fw-bold mb-3">Welcome to ProjectMASSx</h1>
                    <p class="lead text-secondary mb-3">
                        Your gateway to better health and connected
                        hospital services.
                    </p>
                    <p class="text-muted mb-4">
                        Book appointments, access prescriptions,
                        manage health records, review laboratory
                        results, and communicate with hospital
                        services through one secure platform.
                    </p>
                    <div class="d-flex flex-wrap gap-2">
                        <c:choose>
                            <%-- Show dashboards when a user is logged in --%>
                            <c:when test="${not empty sessionScope.user}">
                                <c:choose>
                                    <c:when test="${sessionScope.user.role eq 'PATIENT'}">
                                        <c:url var="dashboardUrl" value="/patientDashboard"/>
                                    </c:when>
                                    <c:when test="${sessionScope.user.role eq 'DOCTOR'}">
                                        <c:url var="dashboardUrl" value="/doctorDashboard"/>
                                    </c:when>
                                    <c:when test="${sessionScope.user.role eq 'PHARMACIST'}">
                                        <c:url var="dashboardUrl" value="/pharmacistDashboard"/>
                                    </c:when>
                                    <c:when test="${sessionScope.user.role eq 'LAB_TECHNICIAN'}">
                                        <c:url var="dashboardUrl" value="/labDashboard"/>
                                    </c:when>
                                    <c:when test="${sessionScope.user.role eq 'HOSPITAL_ADMIN'}">
                                        <c:url var="dashboardUrl" value="/adminDashboard"/>
                                    </c:when>
                                    <c:when test="${sessionScope.user.role eq 'SYSTEM_ADMIN'}">
                                        <c:url var="dashboardUrl" value="/systemAdminDashboard"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:url var="dashboardUrl" value="/login"/>
                                    </c:otherwise>

                                </c:choose>
                                ${dashboardUrl} Open Dashboard </a>
                                <c:url var="logoutUrl" value="/logout"/>
                                ${logoutUrl} Logout </a>
                            </c:when>
                            <%-- Show login and registration when logged out --%>
                            <c:otherwise>
                                <c:url var="loginUrl" value="/login"/>
                                <c:url var="registerUrl" value="/register"/>
                                ${loginUrl} Login </a>
                                ${registerUrl} Create Account </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-lg-5 mt-5 mt-lg-0">
                    <div class="card border-0 shadow">
                        <div class="card-body p-4">
                            <h2 class="h4 text-primary mb-3">Access Hospital Services</h2>
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-0">Online appointment scheduling</li>
                                <li class="list-group-item px-0">Electronic health records</li>
                                <li class="list-group-item px-0">Prescription and pharmacy services</li>
                                <li class="list-group-item px-0">Laboratory test results</li>
                                <li class="list-group-item px-0">Emergency care requests</li>
                                <li class="list-group-item px-0">Teleconsultation support</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <%-- Services section --%>
    <section class="py-5 bg-white">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="fw-bold">ProjectMASSx Services</h2>
                <p class="text-muted">
                    Healthcare tools designed for patients and
                    hospital professionals.
                </p>
            </div>
            <div class="row g-4">
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon mb-3">A</div>
                            <h3 class="h5">Appointments</h3>
                            <p class="text-muted mb-0">
                                Search for available doctors, book
                                appointments, and review upcoming
                                consultations.
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon mb-3">E</div>
                            <h3 class="h5">Electronic Health Records</h3>
                            <p class="text-muted mb-0">
                                Access clinical information, treatment
                                plans, and relevant medical history.
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon mb-3">P</div>
                            <h3 class="h5">Pharmacy Services</h3>
                            <p class="text-muted mb-0">
                                Review prescriptions, monitor order
                                progress, and access medication
                                information.
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon mb-3">L</div>
                            <h3 class="h5">Laboratory Services</h3>
                            <p class="text-muted mb-0">
                                Follow laboratory test progress and
                                review approved diagnostic results.
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon mb-3">T</div>
                            <h3 class="h5">Teleconsultation</h3>
                            <p class="text-muted mb-0">
                                Join scheduled remote consultations
                                through integrated communication
                                services.
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon mb-3">F</div>
                            <h3 class="h5">Patient Feedback</h3>
                            <p class="text-muted mb-0">
                                Share feedback after completed
                                appointments and help improve hospital
                                services.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <%--
        GlobalLayoutAdvice automatically adds publicReviews
        before this shared JSP is rendered.
    --%>
    <%@ include file="shared/feedback.jsp" %>
</main>
<footer class="bg-dark text-white py-4">
    <div class="container text-center">
        <p class="mb-1">ProjectMASSx Hospital Management System</p>
        <small class="text-white-50">
            Healthcare information displayed by this system does not
            replace professional medical advice.
        </small>
    </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>