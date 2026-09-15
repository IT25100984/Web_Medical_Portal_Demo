<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Emergency Fast-Track Intake - WMP</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../shared/header.jsp" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow border-danger">
                <div class="card-header bg-danger text-white">
                    <h4 class="mb-0">🚨 Fast-Track Emergency Request</h4>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/emergency/submit" method="post">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Patient Name</label>
                                <input type="text" name="patientName" class="form-control" required placeholder="John Doe or 'Unknown'">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Emergency Contact</label>
                                <input type="text" name="contactNumber" class="form-control" required placeholder="Phone Number">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Current Location</label>
                            <input type="text" name="location" class="form-control" required placeholder="Ward, Room, or Address">
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold text-danger">Priority Level</label>
                                <select name="priorityLevel" class="form-select border-danger">
                                    <option value="CRITICAL">Critical (Life-Threatening)</option>
                                    <option value="URGENT" selected>Urgent (Immediate Attention)</option>
                                    <option value="STANDARD">Standard (Needs Care)</option>
                                </select>
                            </div>
                            <div class="col-md-6 d-flex align-items-center mt-4">
                                <div class="form-check form-switch fs-5">
                                    <input class="form-check-input" type="checkbox" name="requiresAmbulance" value="true" id="ambulanceSwitch">
                                    <label class="form-check-label text-danger fw-bold" for="ambulanceSwitch">🚑 Dispatch Ambulance</label>
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold">Clinical Description / Symptoms</label>
                            <textarea name="description" class="form-control" rows="3" required placeholder="Briefly describe the emergency..."></textarea>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-danger btn-lg">Trigger Emergency Alert</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<jsp:include page="/WEB-INF/jsp/ai/ai_assistant_chat.jsp" />
</body>
</html>