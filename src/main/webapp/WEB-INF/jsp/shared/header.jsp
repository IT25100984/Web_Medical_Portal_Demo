<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">

<c:url var="homeUrl" value="/" />
<c:url var="loginUrl" value="/login" />
<c:url var="registerUrl" value="/register" />
<c:url var="logoutUrl" value="/logout" />
<c:url var="emergencyRequestUrl" value="/emergency/request" />
<c:url var="emergencyDashboardUrl" value="/emergency/dashboard" />

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
            <c:url var="dashboardUrl" value="/labDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'HOSPITAL_ADMIN'}">
            <c:url var="dashboardUrl" value="/adminDashboard" />
        </c:when>
        <c:when test="${sessionScope.user.role eq 'SYSTEM_ADMIN'}">
            <c:url var="dashboardUrl" value="/systemAdminDashboard" />
        </c:when>
        <c:otherwise>
            <c:set var="dashboardUrl" value="${homeUrl}" />
        </c:otherwise>
    </c:choose>
</c:if>

<nav class="navbar navbar-expand-lg navbar-dark wmp-navbar shadow-sm mb-4" aria-label="Main navigation">
    <div class="container">
        <!-- Brand Link -->
        <a class="navbar-brand text-white fw-bold" href="${homeUrl}">
            Web Medical Portal - WMP
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavigation" aria-controls="mainNavigation" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavigation">
            <div class="navbar-nav ms-auto align-items-lg-center gap-2 py-2 py-lg-0">

                <!-- Fast-Track Emergency Button (Visible to Everyone) -->
                <a href="${emergencyRequestUrl}" class="btn btn-sm btn-danger text-white fw-bold shadow-sm">
                    🚑 Report Emergency
                </a>

                <button type="button" class="btn btn-sm btn-outline-warning text-white" data-bs-toggle="modal" data-bs-target="#publicReviewsModal">
                    <span class="text-warning me-1" aria-hidden="true">★</span>
                    Patient Reviews
                </button>

                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <span class="navbar-text text-white me-lg-2">
                            Welcome,
                            <strong>
                                <c:out value="${sessionScope.user.fullName}" />
                            </strong>
                        </span>

                        <!-- Emergency Command Center (Admins Only) -->
                        <c:if test="${sessionScope.user.role eq 'SYSTEM_ADMIN'}">
                            <a href="${emergencyDashboardUrl}" class="btn btn-sm btn-danger fw-bold shadow-sm">
                                🚨 Emergency Center
                            </a>
                        </c:if>

                        <a href="${dashboardUrl}" class="btn btn-sm btn-outline-light">
                            Dashboard
                        </a>

                        <a href="${logoutUrl}" class="btn btn-sm btn-light text-primary fw-semibold">
                            Logout
                        </a>
                    </c:when>

                    <c:otherwise>
                        <a href="${loginUrl}" class="btn btn-sm btn-outline-light">
                            Login
                        </a>

                        <a href="${registerUrl}" class="btn btn-sm btn-light text-primary fw-semibold">
                            Create Account
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<%@ include file="feedback.jsp" %>