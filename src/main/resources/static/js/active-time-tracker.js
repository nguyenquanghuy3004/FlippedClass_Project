(function() {
    // Avoid running multiple times if the script is loaded twice
    if (window.activeTimeTrackerLoaded) return;
    window.activeTimeTrackerLoaded = true;

    // Configuration
    const HEARTBEAT_INTERVAL_MS = 60000; // 60 seconds
    const ACTIVE_THRESHOLD_MS = 60000; // Time since last interaction to be considered active

    let lastActivityTime = Date.now();
    let isTracking = false;

    // Update last activity time on user interactions
    const resetActivity = () => {
        lastActivityTime = Date.now();
    };

    // Attach listeners
    ['mousemove', 'keydown', 'click', 'scroll', 'touchstart'].forEach(event => {
        document.addEventListener(event, resetActivity, { passive: true });
    });

    const sendHeartbeat = () => {
        const now = Date.now();
        // If user interacted within the last threshold time, send heartbeat
        if (now - lastActivityTime < ACTIVE_THRESHOLD_MS) {
            // Include CSRF token if available (standard Spring Security meta tags)
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
            
            const jwtToken = localStorage.getItem('jwt_token');
            const headers = {
                'Content-Type': 'application/json'
            };
            if (jwtToken) {
                headers['Authorization'] = `Bearer ${jwtToken}`;
            }
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            fetch('/api/activity/heartbeat', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({ activeSeconds: 60 })
            }).catch(err => {
                console.error('Failed to send activity heartbeat', err);
            });
        }
    };

    // Start interval
    setInterval(sendHeartbeat, HEARTBEAT_INTERVAL_MS);
})();
