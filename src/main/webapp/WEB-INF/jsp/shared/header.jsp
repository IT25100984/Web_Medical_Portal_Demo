<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Context-safe application URLs --%>
<c:url var="homeUrl" value="/" />
<c:url var="loginUrl" value="/login" />
<c:url var="registerUrl" value="/register" />
<c:url var="logoutUrl" value="/logout" />

<%-- Determine the appropriate dashboard URL --%>
<c:if test="${not empty sessionScope.user}">
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
            <c:url var="dashboardUrl" value="/"/>
        </c:otherwise>
    </c:choose>
</c:if>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm mb-4" aria-label="Main navigation">
    <div class="container">
        <%-- Application name and home link --%>
        ${homeUrl}ProjectMASSx</a>
        <%-- Mobile navigation toggle --%>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavigation"
                aria-controls="mainNavigation" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNavigation">
            <div class="navbar-nav ms-auto align-items-lg-center gap-2">
                <%-- Public reviews modal button --%>
                <button type="button" class="btn btn-sm btn-outline-warning text-white"
                        data-bs-toggle="modal" data-bs-target="#publicReviewsModal">
                    <span class="text-warning me-1" aria-hidden="true">★</span>
                    Patient Reviews
                </button>
                <c:choose>
                    <%-- Logged-in navigation --%>
                    <c:when test="${not empty sessionScope.user}">
                        <span class="navbar-text text-white me-lg-2">Welcome,
                            <strong>
                                <c:out value="${sessionScope.user.fullName}"/>
                            </strong>
                        </span>
                        ${dashboardUrl} Dashboard </a>
                        ${logoutUrl} Logout </a>
                    </c:when>
                    <%-- Public navigation --%>
                    <c:otherwise>
                        ${loginUrl} Login </a>
                        ${registerUrl} Register </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
<%--
    Include the public review modal once.

    If header.jsp is included by every page, do not separately
    include shared/feedback.jsp inside index.jsp, login.jsp,
    register.jsp, or other JSP pages.
--%>
<%@ include file="feedback.jsp" %>