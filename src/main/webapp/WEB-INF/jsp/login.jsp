<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login | Web Medical Portal - WMP</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<%@ include file="shared/header.jsp" %>

<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-5 col-xl-4">

            <%-- Alert Messages --%>
            <c:if test="${param.status eq 'loggedOut'}">
                <div class="alert alert-info shadow-sm text-center" role="alert">
                    You have been successfully logged out.
                </div>
            </c:if>
            <c:if test="${param.status eq 'registered'}">
                <div class="alert alert-success shadow-sm text-center" role="alert">
                    Your account was created successfully. Please log in.
                </div>
            </c:if>
            <c:if test="${param.msg eq 'deleted'}">
                <div class="alert alert-success shadow-sm text-center" role="alert">
                    Your account has been closed successfully.
                </div>
            </c:if>
            <c:if test="${param.error eq 'invalidCredentials'}">
                <div class="alert alert-danger shadow-sm text-center" role="alert">
                    The email address or password is incorrect.
                </div>
            </c:if>
            <c:if test="${param.error eq 'missingCredentials'}">
                <div class="alert alert-warning shadow-sm text-center" role="alert">
                    Please enter both your email address and password.
                </div>
            </c:if>
            <c:if test="${param.error eq 'accountDisabled'}">
                <div class="alert alert-danger shadow-sm text-center" role="alert">
                    This account is currently disabled. Please contact the hospital administrator.
                </div>
            </c:if>
            <c:if test="${param.error eq 'invalidRole'}">
                <div class="alert alert-danger shadow-sm text-center" role="alert">
                    Your account role could not be recognized. Please contact the system administrator.
                </div>
            </c:if>
            <c:if test="${param.error eq 'true' || param.error eq 'failed'}">
                <div class="alert alert-danger shadow-sm text-center" role="alert">
                    Login was unsuccessful. Please verify your email address and password.
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success shadow-sm text-center" role="alert">
                    <c:out value="${successMessage}" />
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger shadow-sm text-center" role="alert">
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <div class="card border-0 shadow">
                <div class="card-header bg-primary text-white text-center py-3">
                    <h4 class="mb-1">Hospital Portal Login</h4>
                    <p class="small mb-0">Sign in to access your Web Medical Portal account</p>
                </div>
                <div class="card-body p-4">
                    <c:url var="loginActionUrl" value="/login" />
                    <form action="${loginActionUrl}" method="post" id="loginForm">
                        <c:if test="${not empty _csrf}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        </c:if>

                        <div class="mb-3">
                            <label for="username" class="form-label">Email Address</label>
                            <input type="email" id="username" name="username" class="form-control" placeholder="name@example.com"
                                   maxlength="100" autocomplete="email" required autofocus>
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-group">
                                <!-- CHANGED name to "password" for Spring Security / Controller compatibility -->
                                <input type="password" id="password" name="password" class="form-control"
                                       placeholder="Enter your password" autocomplete="current-password" required>
                                <button type="button" id="togglePassword" class="btn btn-outline-secondary"
                                        aria-label="Show or hide password" aria-pressed="false">
                                    Show
                                </button>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100" id="loginButton">Login</button>
                    </form>
                </div>
                <div class="card-footer bg-white text-center py-3">
                    <c:url var="registerUrl" value="/register" />
                    <small>
                        Do not have an account?
                        <a href="${registerUrl}">Register here</a>
                    </small>
                    <div class="mt-2">
                        <small class="text-muted">
                            Hospital staff require a valid employee ID to register.
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="shared/feedback.jsp" %>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const loginForm = document.getElementById("loginForm");
        const loginButton = document.getElementById("loginButton");
        const passwordInput = document.getElementById("password");
        const togglePasswordButton = document.getElementById("togglePassword");

        if (togglePasswordButton && passwordInput) {
            togglePasswordButton.addEventListener("click", function () {
                const passwordIsHidden = passwordInput.type === "password";
                passwordInput.type = passwordIsHidden ? "text" : "password";
                togglePasswordButton.textContent = passwordIsHidden ? "Hide" : "Show";
                togglePasswordButton.setAttribute("aria-pressed", String(passwordIsHidden));
            });
        }

        if (loginForm) {
            loginForm.addEventListener("submit", function () {
                loginButton.disabled = true;
                loginButton.textContent = "Signing in...";
            });
        }
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<jsp:include page="/WEB-INF/jsp/ai/ai_assistant_chat.jsp" />
</body>
</html>