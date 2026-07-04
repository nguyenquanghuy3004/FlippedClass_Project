// auth-interceptor.js
(function() {
    function logoutOnUnauthorized() {
        console.warn("401 Unauthorized detected. Logging out...");
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_id');
        localStorage.removeItem('full_name');
        localStorage.removeItem('fullName');
        localStorage.removeItem('username');
        localStorage.removeItem('avatar_url');
        localStorage.removeItem('avatarUrl');
        window.location.href = '/signin';
    }

    // Intercept fetch
    const originalFetch = window.fetch;
    window.fetch = async function(...args) {
        const response = await originalFetch.apply(this, args);
        // Only trigger logout if it's an API call inside /api/
        if (response.status === 401 && args[0] && typeof args[0] === 'string' && args[0].includes('/api/')) {
            logoutOnUnauthorized();
        }
        return response;
    };

    // Intercept XMLHttpRequest
    const originalOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function(method, url, ...rest) {
        this.addEventListener('readystatechange', function() {
            if (this.readyState === 4 && this.status === 401 && url && typeof url === 'string' && url.includes('/api/')) {
                logoutOnUnauthorized();
            }
        });
        return originalOpen.call(this, method, url, ...rest);
    };
})();
