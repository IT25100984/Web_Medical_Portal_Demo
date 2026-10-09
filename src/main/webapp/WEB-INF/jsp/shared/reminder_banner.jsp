<%-- shared/reminder_banner.jsp
     Include on patient_dashboard.jsp and doctor_dashboard.jsp, inside the page container:
         <%@ include file="../shared/reminder_banner.jsp" %>
     Needs Bootstrap 5 + Bootstrap Icons (both dashboards already load them).
     Uses plain EL only, so it works whatever taglibs the host page declares. --%>
<div id="reminderPanel" class="mb-4" aria-live="polite"></div>
<script>
    (function () {
        var base = '${pageContext.request.contextPath}/api/reminders';
        var csrfHeader = '${_csrf.headerName}';   // empty when Spring Security CSRF isn't active
        var csrfToken = '${_csrf.token}';
        var panel = document.getElementById('reminderPanel');

        function el(tag, className, text) {
            var node = document.createElement(tag);
            if (className) node.className = className;
            if (text !== undefined) node.textContent = text;   // textContent: no HTML injection
            return node;
        }

        function post(url) {
            var headers = {};
            if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;
            return fetch(url, { method: 'POST', headers: headers, credentials: 'same-origin' });
        }

        function untilText(minutes) {
            if (minutes < 1) return 'starting now';
            if (minutes < 60) return 'in ' + minutes + ' min';
            var hours = Math.floor(minutes / 60), rest = minutes % 60;
            if (hours < 48) return 'in ' + hours + ' h' + (rest ? ' ' + rest + ' min' : '');
            return 'in ' + Math.round(hours / 24) + ' days';
        }

        function renderSummary(data) {
            var card = el('div', 'card border-0 shadow-sm mb-3');
            var body = el('div', 'card-body d-flex flex-wrap justify-content-between align-items-center gap-3');

            var left = el('div');
            left.appendChild(el('i', 'bi bi-bell-fill text-primary me-2'));
            if (data.next) {
                left.appendChild(el('strong', null, 'Next appointment: '));
                left.appendChild(document.createTextNode(
                    data.next.type + ' with ' + data.next.withName + ' on ' + data.next.date +
                    ' at ' + data.next.time + ' (' + untilText(data.next.minutesAway) + ')'));
            } else {
                left.appendChild(el('span', 'text-muted', 'No upcoming confirmed appointments.'));
            }

            var right = el('div', 'd-flex gap-2');
            right.appendChild(el('span', 'badge bg-primary', data.upcomingCount + ' upcoming'));
            right.appendChild(el('span', 'badge bg-secondary', data.todayCount + ' today'));

            body.appendChild(left);
            body.appendChild(right);
            card.appendChild(body);
            return card;
        }

        function renderNotification(item) {
            var alert = el('div', 'alert alert-warning alert-dismissible fade show shadow-sm mb-2');
            alert.setAttribute('role', 'alert');
            alert.appendChild(el('i', 'bi bi-alarm-fill me-2'));
            alert.appendChild(document.createTextNode(item.message));
            var close = el('button', 'btn-close');
            close.type = 'button';
            close.setAttribute('data-bs-dismiss', 'alert');
            close.setAttribute('aria-label', 'Dismiss');
            close.addEventListener('click', function () { post(base + '/' + item.notificationID + '/read'); });
            alert.appendChild(close);
            return alert;
        }

        function render(data) {
            panel.replaceChildren(renderSummary(data));
            (data.notifications || []).forEach(function (item) {
                panel.appendChild(renderNotification(item));
            });
            if ((data.notifications || []).length > 1) {
                var all = el('button', 'btn btn-sm btn-outline-secondary', 'Dismiss all reminders');
                all.type = 'button';
                all.addEventListener('click', function () {
                    post(base + '/read-all').then(load);
                });
                panel.appendChild(all);
            }
        }

        function load() {
            fetch(base, { credentials: 'same-origin', headers: { 'Accept': 'application/json' } })
                .then(function (response) { return response.ok ? response.json() : null; })
                .then(function (data) { if (data) render(data); })
                .catch(function () { /* leave the dashboard usable if the call fails */ });
        }

        load();
        setInterval(load, 60000);   // pick up new reminders without a page reload
    })();
</script>
