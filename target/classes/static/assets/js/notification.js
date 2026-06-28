const notifUserId = localStorage.getItem('user_id') || (document.getElementById('currentUserId')?.value);

if (notifUserId) {
    const socket = new SockJS('/ws');
    window.stompClient = Stomp.over(socket);
    window.stompClient.debug = null; // Ẩn log rác

    window.stompClient.connect({}, function () {
        window.stompClient.subscribe('/topic/notifications/' + notifUserId, function (res) {
            const notif = JSON.parse(res.body);

            // Hiện dấu chấm đỏ trên chuông báo
            const badge = document.getElementById('notifBadge');
            if (badge) {
                badge.style.display = 'inline-block';
            }

            // Hiện popup
            // alert("🔔 THÔNG BÁO MỚI: " + notif.message);
            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    toast: true,
                    position: 'top-end',
                    icon: 'info',
                    title: notif.message,
                    showConfirmButton: false,
                    timer: 5000
                });
            } else {
                alert("🔔 THÔNG BÁO MỚI: " + notif.message);
            }
        });
    });
}
