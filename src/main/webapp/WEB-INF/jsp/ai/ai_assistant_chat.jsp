<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

<!-- Chat Widget Styles -->
<style>
    :root {
        --wmp-sky-1: #7fc9f0;
        --wmp-sky-2: #4a9fd8;
        --wmp-sky-deep: #2b6ca3;
        --wmp-glass-bg: rgba(255, 255, 255, 0.55);
        --wmp-glass-border: rgba(255, 255, 255, 0.35);
    }

    .chat-toggle-btn {
        position: fixed;
        bottom: 25px;
        right: 25px;
        z-index: 1050;
        width: 64px;
        height: 64px;
        border-radius: 50%;
        border: none;
        background: linear-gradient(145deg, var(--wmp-sky-1), var(--wmp-sky-2));
        box-shadow: 0 8px 24px rgba(74, 159, 216, 0.45), 0 2px 6px rgba(0,0,0,0.08);
        transition: transform 0.25s ease, box-shadow 0.25s ease;
    }
    .chat-toggle-btn:hover {
        transform: scale(1.08) translateY(-2px);
        box-shadow: 0 12px 28px rgba(74, 159, 216, 0.55), 0 4px 8px rgba(0,0,0,0.1);
    }
    .chat-toggle-btn i {
        color: #ffffff;
        filter: drop-shadow(0 1px 2px rgba(0,0,0,0.15));
    }

    .chat-widget-card {
        position: fixed;
        bottom: 100px;
        right: 25px;
        width: 420px;
        max-width: 92vw;
        height: 620px;
        max-height: 80vh;
        z-index: 1050;
        display: none;
        flex-direction: column;
        border-radius: 20px;
        overflow: hidden;
        border: 1px solid var(--wmp-glass-border);
        background: var(--wmp-glass-bg);
        backdrop-filter: blur(18px) saturate(160%);
        -webkit-backdrop-filter: blur(18px) saturate(160%);
        box-shadow: 0 20px 50px rgba(43, 108, 163, 0.25);
    }

    .chat-widget-card .card-header {
        border-radius: 20px 20px 0 0;
        background: linear-gradient(120deg, var(--wmp-sky-1), var(--wmp-sky-2));
        border-bottom: 1px solid rgba(255,255,255,0.25);
    }

    .chat-close-icon {
        background: rgba(255,255,255,0.18);
        border: none;
        border-radius: 50%;
        width: 30px;
        height: 30px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        transition: background 0.2s ease, transform 0.2s ease;
    }
    .chat-close-icon:hover {
        background: rgba(255,255,255,0.32);
        transform: rotate(90deg);
    }

    .chat-messages {
        flex-grow: 1;
        overflow-y: auto;
        padding: 20px;
        background: linear-gradient(180deg, rgba(240, 249, 255, 0.5), rgba(255,255,255,0.35));
        display: flex;
        flex-direction: column;
        gap: 12px;
    }
    .chat-messages::-webkit-scrollbar {
        width: 6px;
    }
    .chat-messages::-webkit-scrollbar-thumb {
        background: rgba(74, 159, 216, 0.35);
        border-radius: 10px;
    }

    .chat-bubble {
        max-width: 85%;
        padding: 12px 16px;
        border-radius: 16px;
        font-size: 0.95rem;
        line-height: 1.55;
        box-shadow: 0 2px 8px rgba(43, 108, 163, 0.08);
    }
    .chat-bubble p { margin: 0 0 8px 0; }
    .chat-bubble p:last-child { margin-bottom: 0; }
    .chat-bubble ul { margin: 6px 0; padding-left: 20px; }
    .chat-bubble li { margin-bottom: 4px; }
    .chat-bubble hr { margin: 10px 0; border-top: 1px solid rgba(0,0,0,0.08); }
    .chat-bubble em { color: #5a7a8f; }
    .chat-bubble strong { color: var(--wmp-sky-deep); }

    .chat-bubble-user {
        background: linear-gradient(135deg, var(--wmp-sky-1), var(--wmp-sky-2));
        color: #ffffff;
        align-self: flex-end;
    }
    .chat-bubble-ai {
        background: rgba(255, 255, 255, 0.85);
        color: #2c3e4a;
        align-self: flex-start;
        border: 1px solid rgba(74, 159, 216, 0.15);
    }

    .chat-widget-card .card-footer {
        background: rgba(255, 255, 255, 0.6);
        border-top: 1px solid rgba(255,255,255,0.4);
        backdrop-filter: blur(10px);
    }
    .chat-widget-card .card-footer input.form-control {
        background: rgba(255,255,255,0.7);
        border: 1px solid rgba(74, 159, 216, 0.25);
        border-radius: 12px;
    }
    .chat-widget-card .card-footer input.form-control:focus {
        border-color: var(--wmp-sky-2);
        box-shadow: 0 0 0 0.2rem rgba(74, 159, 216, 0.2);
    }
    .chat-send-btn {
        background: linear-gradient(135deg, var(--wmp-sky-1), var(--wmp-sky-2));
        border: none;
        border-radius: 12px;
        width: 44px;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    .chat-send-btn:hover {
        transform: scale(1.06);
        box-shadow: 0 4px 12px rgba(74, 159, 216, 0.4);
    }
</style>

<!-- Floating Chat Button -->
<button id="chatToggleBtn" class="chat-toggle-btn d-flex align-items-center justify-content-center" type="button">
    <i class="bi bi-heart-pulse-fill fs-3" id="chatIcon"></i>
</button>

<!-- Floating Chat Card -->
<div id="chatWidget" class="card chat-widget-card">
    <div class="card-header text-white d-flex justify-content-between align-items-center py-3">
        <div class="d-flex align-items-center gap-2">
            <i class="bi bi-robot fs-4"></i>
            <div>
                <h6 class="mb-0 font-weight-bold">WMP Health Assistant</h6>
                <small class="text-white-50" style="font-size: 0.75rem;">AI-Powered Guidance</small>
            </div>
        </div>
        <button type="button" class="chat-close-icon" id="closeChatBtn" aria-label="Close">
            <i class="bi bi-x-lg" style="font-size: 0.85rem;"></i>
        </button>
    </div>

    <div class="chat-messages" id="chatMessages">
        <div class="chat-bubble chat-bubble-ai">
            Hello! I am your AI Health Assistant. How can I help you with symptoms or appointment inquiries today?
        </div>
    </div>

    <div class="card-footer p-2">
        <form id="chatForm" class="d-flex gap-2">
            <input type="text" id="userInput" class="form-control" placeholder="Type your message..." autocomplete="off" required>
            <button type="submit" class="btn chat-send-btn d-flex align-items-center justify-content-center" id="sendBtn">
                <i class="bi bi-send-fill text-white"></i>
            </button>
        </form>
    </div>
</div>

<!-- Chat Widget Logic -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const chatToggleBtn = document.getElementById("chatToggleBtn");
        const closeChatBtn = document.getElementById("closeChatBtn");
        const chatWidget = document.getElementById("chatWidget");
        const chatForm = document.getElementById("chatForm");
        const userInput = document.getElementById("userInput");
        const chatMessages = document.getElementById("chatMessages");
        const sendBtn = document.getElementById("sendBtn");
        const chatIcon = document.getElementById("chatIcon");
        const contextPath = "${pageContext.request.contextPath}";

        chatToggleBtn.addEventListener("click", function () {
            const isVisible = chatWidget.style.display === "flex";
            chatWidget.style.display = isVisible ? "none" : "flex";
            chatIcon.className = isVisible ? "bi bi-heart-pulse-fill fs-3" : "bi bi-x-lg fs-3";
            if (!isVisible) userInput.focus();
        });

        closeChatBtn.addEventListener("click", function () {
            chatWidget.style.display = "none";
            chatIcon.className = "bi bi-heart-pulse-fill fs-3";
        });

        function scrollToBottom() {
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }

        function escapeHtml(str) {
            const div = document.createElement("div");
            div.innerText = str;
            return div.innerHTML;
        }

        function markdownToHtml(raw) {
            let text = escapeHtml(raw);
            text = text.replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>");
            text = text.replace(/\*(.+?)\*/g, "<em>$1</em>");
            text = text.replace(/^\s*---+\s*$/gm, "<hr>");

            const lines = text.split("\n");
            let html = "";
            let inList = false;
            for (const line of lines) {
                const trimmed = line.trim();
                if (/^\*\s+/.test(trimmed) || /^-\s+/.test(trimmed)) {
                    if (!inList) { html += "<ul>"; inList = true; }
                    html += "<li>" + trimmed.replace(/^[-*]\s+/, "") + "</li>";
                } else {
                    if (inList) { html += "</ul>"; inList = false; }
                    if (trimmed === "<hr>") {
                        html += "<hr>";
                    } else if (trimmed.length > 0) {
                        html += "<p>" + trimmed + "</p>";
                    }
                }
            }
            if (inList) html += "</ul>";
            return html;
        }

        function appendMessage(text, sender) {
            const bubble = document.createElement("div");
            bubble.classList.add("chat-bubble", sender === "user" ? "chat-bubble-user" : "chat-bubble-ai");
            if (sender === "user") {
                bubble.innerText = text;
            } else {
                bubble.innerHTML = markdownToHtml(text);
            }
            chatMessages.appendChild(bubble);
            scrollToBottom();
            return bubble;
        }

        chatForm.addEventListener("submit", async function (e) {
            e.preventDefault();
            const message = userInput.value.trim();
            if (!message) return;

            appendMessage(message, "user");
            userInput.value = "";
            userInput.disabled = true;
            sendBtn.disabled = true;

            const loadingBubble = appendMessage("Thinking...", "ai");

            try {
                const response = await fetch(contextPath + "/api/ai/chat", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ message: message })
                });

                if (!response.ok) throw new Error("Server error");

                const data = await response.json();
                loadingBubble.innerHTML = markdownToHtml(data.response || "No response received.");
            } catch (error) {
                loadingBubble.innerText = "Error connecting to AI service.";
                loadingBubble.classList.add("text-danger");
            } finally {
                userInput.disabled = false;
                sendBtn.disabled = false;
                userInput.focus();
                scrollToBottom();
            }
        });
    });
</script>