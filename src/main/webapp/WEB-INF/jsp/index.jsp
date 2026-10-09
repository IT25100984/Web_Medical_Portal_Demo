<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home | Web Medical Portal - WMP</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            font-family: 'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
        }

        /* ---------- Hero ---------- */
        .hero-section {
            min-height: 540px;
            display: flex;
            align-items: center;
            color: #fff;
            background:
                    radial-gradient(circle at 85% 15%, rgba(56, 182, 255, 0.35), transparent 45%),
                    linear-gradient(135deg, #0b3c6f 0%, #1f6fb2 55%, #2aa5d8 100%);
        }

        .hero-section .lead {
            color: rgba(255, 255, 255, 0.85);
        }

        .hero-section .hero-text {
            color: rgba(255, 255, 255, 0.72);
        }

        .hero-badge {
            background: rgba(255, 255, 255, 0.16);
            border: 1px solid rgba(255, 255, 255, 0.3);
            color: #fff;
            font-weight: 500;
            letter-spacing: 0.02em;
        }

        .services-card {
            border-radius: 1rem;
        }

        .services-card .list-group-item {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding-top: 0.7rem;
            padding-bottom: 0.7rem;
        }

        .services-card .list-group-item i {
            color: #198754;
            font-size: 1.1rem;
        }

        /* ---------- Feature cards ---------- */
        .feature-card {
            height: 100%;
            border-radius: 1rem;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .feature-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.10) !important;
        }

        .feature-icon {
            width: 56px;
            height: 56px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 14px;
            font-size: 1.6rem;
        }

        .icon-blue   { background: rgba(13, 110, 253, 0.12); color: #0d6efd; }
        .icon-teal   { background: rgba(32, 201, 151, 0.15); color: #12a07a; }
        .icon-green  { background: rgba(25, 135, 84, 0.13);  color: #198754; }
        .icon-cyan   { background: rgba(13, 202, 240, 0.16); color: #0aa2c0; }
        .icon-purple { background: rgba(111, 66, 193, 0.13); color: #6f42c1; }
        .icon-amber  { background: rgba(255, 193, 7, 0.20);  color: #b78103; }

        /* ---------- Footer ---------- */
        .site-footer a {
            color: rgba(255, 255, 255, 0.7);
        }
    </style>
</head>
<body class="bg-light">

<%@ include file="shared/header.jsp" %>

<c:url var="loginUrl" value="/login" />
<c:url var="registerUrl" value="/register" />
<c:url var="logoutUrl" value="/logout" />

<c:if test="${not empty sessionScope.user}">
    <c:choose>
        <c:when test="${sessionScope.user.role eq 'PATIENT'}">
            <c:url var="dashboardUrl" value="/patientDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'DOCTOR'}">
            <c:url var="dashboardUrl" value="/doctorDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'PHARMACIST'}">
            <c:url var="dashboardUrl" value="/pharmacistDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'LAB_TECHNICIAN'}">
            <%-- Fixed: this was "//labDashboard" (double slash) --%>
            <c:url var="dashboardUrl" value="/labDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'HOSPITAL_ADMIN'}">
            <c:url var="dashboardUrl" value="/adminDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'SYSTEM_ADMIN'}">
            <c:url var="dashboardUrl" value="/systemAdminDashboard" />
        </c:when>
        <c:otherwise>
            <c:url var="dashboardUrl" value="/" />
        </c:otherwise>
    </c:choose>
</c:if>

<main>
    <section class="hero-section">
        <div class="container py-5">
            <div class="row align-items-center">
                <div class="col-lg-7">
                    <span class="badge hero-badge rounded-pill px-3 py-2 mb-3">
                        <i class="bi bi-hospital me-1"></i> Smart Hospital Management
                    </span>

                    <h1 class="display-4 fw-bold mb-3">
                        Welcome to Your Web Medical Portal - WMP
                    </h1>

                    <p class="lead mb-3">
                        Your gateway to better health and connected hospital services.
                    </p>

                    <p class="hero-text mb-4">
                        Book appointments, access prescriptions, manage health records, review laboratory results, and communicate with hospital services through one secure platform.
                    </p>

                    <div class="d-flex flex-wrap gap-2">
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <a href="${dashboardUrl}" class="btn btn-light btn-lg fw-semibold px-4">
                                    <i class="bi bi-speedometer2 me-1"></i> Open Dashboard
                                </a>

                                <a href="${logoutUrl}" class="btn btn-outline-light btn-lg">
                                    Logout
                                </a>
                            </c:when>

                            <c:otherwise>
                                <a href="${loginUrl}" class="btn btn-light btn-lg fw-semibold px-4">
                                    <i class="bi bi-box-arrow-in-right me-1"></i> Login
                                </a>

                                <a href="${registerUrl}" class="btn btn-outline-light btn-lg px-4">
                                    Create Account
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="col-lg-5 mt-5 mt-lg-0">
                    <div class="card services-card border-0 shadow-lg">
                        <div class="card-body p-4">
                            <h2 class="h4 text-primary fw-bold mb-3">
                                Access Hospital Services
                            </h2>

                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-0"><i class="bi bi-check-circle-fill"></i> Online appointment scheduling</li>
                                <li class="list-group-item px-0"><i class="bi bi-check-circle-fill"></i> Electronic health records</li>
                                <li class="list-group-item px-0"><i class="bi bi-check-circle-fill"></i> Prescription and pharmacy services</li>
                                <li class="list-group-item px-0"><i class="bi bi-check-circle-fill"></i> Laboratory test results</li>
                                <li class="list-group-item px-0"><i class="bi bi-check-circle-fill"></i> Emergency care requests</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="py-5 bg-white">
        <div class="container py-3">
            <div class="text-center mb-5">
                <h2 class="fw-bold">
                    Web Medical Portal Services
                </h2>

                <p class="text-muted mb-0">
                    Healthcare tools designed for patients and hospital professionals.
                </p>
            </div>

            <div class="row g-4">
                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon icon-blue mb-3"><i class="bi bi-calendar2-check"></i></div>
                            <h3 class="h5 fw-semibold">Appointments</h3>
                            <p class="text-muted mb-0">
                                Search for available doctors, book appointments, and review upcoming consultations.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon icon-teal mb-3"><i class="bi bi-file-earmark-medical"></i></div>
                            <h3 class="h5 fw-semibold">Electronic Health Records</h3>
                            <p class="text-muted mb-0">
                                Access clinical information, treatment plans, and relevant medical history.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon icon-green mb-3"><i class="bi bi-capsule"></i></div>
                            <h3 class="h5 fw-semibold">Pharmacy Services</h3>
                            <p class="text-muted mb-0">
                                Review prescriptions, monitor order progress, and access medication information.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon icon-cyan mb-3"><i class="bi bi-eyedropper"></i></div>
                            <h3 class="h5 fw-semibold">Laboratory Services</h3>
                            <p class="text-muted mb-0">
                                Follow laboratory test progress and review approved diagnostic results.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon icon-purple mb-3"><i class="bi bi-camera-video"></i></div>
                            <h3 class="h5 fw-semibold">Teleconsultation</h3>
                            <p class="text-muted mb-0">
                                Join scheduled remote consultations through integrated communication services.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 col-lg-4">
                    <div class="card feature-card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <div class="feature-icon icon-amber mb-3"><i class="bi bi-chat-heart"></i></div>
                            <h3 class="h5 fw-semibold">Patient Feedback</h3>
                            <p class="text-muted mb-0">
                                Share feedback after completed appointments and help improve hospital services.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <%-- Closing call-to-action: only for visitors who aren't logged in --%>
    <c:if test="${empty sessionScope.user}">
        <section class="py-5 bg-light">
            <div class="container text-center py-2">
                <h2 class="h3 fw-bold mb-2">Ready to get started?</h2>
                <p class="text-muted mb-4">Create an account to book your first appointment in minutes.</p>
                <a href="${registerUrl}" class="btn btn-primary btn-lg px-4 me-2">Create Account</a>
                <a href="${loginUrl}" class="btn btn-outline-primary btn-lg px-4">Login</a>
            </div>
        </section>
    </c:if>
</main>

<footer class="site-footer bg-dark text-white py-4">
    <div class="container text-center">
        <p class="mb-1 fw-semibold">
            <i class="bi bi-heart-pulse-fill text-info me-1"></i> Web Medical Portal - WMP
        </p>

        <small class="text-white-50">
            Healthcare information displayed by this system does not replace professional medical advice.
        </small>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<jsp:include page="/WEB-INF/jsp/ai/ai_assistant_chat.jsp" />
</body>
</html>
