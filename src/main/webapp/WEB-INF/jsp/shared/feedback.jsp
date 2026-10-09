<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<div class="modal fade" id="publicReviewsModal" tabindex="-1" aria-labelledby="reviewsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-md">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 1rem; overflow: hidden;">
            <div class="modal-header text-white p-3"
                 style="background: linear-gradient(135deg, #0b3c6f 0%, #1f6fb2 55%, #2aa5d8 100%);">
                <h5 class="modal-title fw-bold" id="reviewsModalLabel">
                    <i class="bi bi-chat-square-heart-fill me-2 text-warning"></i>What Our Patients Say
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body p-3" style="max-height: 400px; overflow-y: auto; background-color: #f4f8fc;">
                <c:choose>
                    <c:when test="${not empty publicReviews}">
                        <c:forEach var="rev" items="${publicReviews}">
                            <div class="bg-white p-3 mb-3 rounded-3 shadow-sm border-start border-primary border-4">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-semibold text-dark small">
                                        <i class="bi bi-person-circle text-primary me-1"></i><c:out value="${rev.patientName}" />
                                    </span>
                                    <span class="text-muted small">Dr. <c:out value="${rev.doctorName}" /></span>
                                </div>
                                <div class="mb-2">
                                    <span class="text-warning small">${rev.getStars()}</span>
                                </div>
                                    <%-- c:out escapes the comment, so reviewers can't inject HTML/scripts --%>
                                <p class="mb-0 text-secondary small fst-italic">&ldquo;<c:out value="${rev.comment}" />&rdquo;</p>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-chat-left-text fs-2 d-block mb-2 text-secondary"></i>
                            No reviews have been published yet.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="modal-footer bg-white justify-content-center py-2">
                <small class="text-muted">Logged-in patients can submit a new review via their dashboard.</small>
            </div>
        </div>
    </div>
</div>
