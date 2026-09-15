document.addEventListener("DOMContentLoaded", function () {
    const alertApiUrl = window.location.pathname.replace('/dashboard', '/api/alerts');
    let currentRequestCount = document.querySelectorAll('tbody tr').length;

    // Check the server for new alerts every 10 seconds
    setInterval(() => {
        fetch(alertApiUrl)
            .then(response => response.json())
            .then(data => {
                // If the number of emergencies returned by the API is different
                // than what is currently rendered on the table, refresh the page.
                if (data.length > currentRequestCount) {
                    console.log("New emergency detected. Refreshing queue...");
                    window.location.reload();
                }
            })
            .catch(error => console.error("Error polling emergency API:", error));
    }, 10000);
});