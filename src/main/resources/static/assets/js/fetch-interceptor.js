(function() {
    function handleUnauthorized() {
        console.warn('Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Đang chuyển hướng về trang đăng nhập...');
        // Xóa token cũ
        localStorage.removeItem('jwt_token');
        sessionStorage.removeItem('jwt_token');
        // Chuyển hướng
        window.location.href = '/signin';
    }

    // 1. Chặn và xử lý Fetch API
    const originalFetch = window.fetch;
    window.fetch = async function(...args) {
        try {
            const response = await originalFetch.apply(this, args);
            // Nếu API trả về 401 Unauthorized (hết hạn token hoặc không có quyền)
            if (response.status === 401) {
                handleUnauthorized();
            }
            return response;
        } catch (error) {
            throw error;
        }
    };

    // 2. Chặn và xử lý XMLHttpRequest (Bao phủ luôn jQuery $.ajax nếu có dùng)
    const originalXhrOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function() {
        this.addEventListener('load', function() {
            if (this.status === 401) {
                handleUnauthorized();
            }
        });
        originalXhrOpen.apply(this, arguments);
    };
})();
