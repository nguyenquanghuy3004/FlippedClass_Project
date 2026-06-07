$(document).ready(function () {
    const spaceId = $('#spaceId').val();
    let token = localStorage.getItem('jwt_token');
    if (!token) token = localStorage.getItem('token');

    // Open Create Node Modal
    $('.create-node-btn').click(function (e) {
        e.preventDefault();
        const pathId = $(this).data('pathid');
        $('#nodePathId').val(pathId);
        $('#nodeTitle').val('');
        $('#nodeDescription').val('');
        $('#nodeType').val('VIDEO');
        $('#createNodeError').hide();
        $('#createNodeModal').modal('show');
    });

    // Open Edit Node Modal
    $('.edit-node-btn').click(function (e) {
        e.preventDefault();
        const pathId = $(this).data('pathid');
        const nodeId = $(this).data('nodeid');
        const title = $(this).data('title');
        const description = $(this).data('description');
        const type = $(this).data('type');

        $('#editNodePathId').val(pathId);
        $('#editNodeId').val(nodeId);
        $('#editNodeTitle').val(title);
        $('#editNodeDescription').val(description !== 'null' ? description : '');
        $('#editNodeType').val(type || 'VIDEO');
        $('#editNodeError').hide();
        $('#editNodeModal').modal('show');
    });

    // Delete Node
    $('.delete-node-btn').click(function (e) {
        e.preventDefault();
        const pathId = $(this).data('pathid');
        const nodeId = $(this).data('nodeid');
        if (confirm('Are you sure you want to delete this lesson?')) {
            fetch(`/api/learning-spaces/${spaceId}/learning-paths/${pathId}/learning-nodes/${nodeId}`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            }).then(res => {
                if (res.ok) window.location.reload();
                else alert("Failed to delete lesson");
            });
        }
    });

    // Save Node (Create)
    $('#btnSaveNode').click(function () {
        const pathId = $('#nodePathId').val();
        const title = $('#nodeTitle').val();
        const desc = $('#nodeDescription').val();
        const type = $('#nodeType').val();
        const errorDiv = $('#createNodeError');
        const btn = $(this);

        errorDiv.hide();
        if (!title) {
            errorDiv.text("Lesson title is required").show();
            return;
        }

        btn.prop('disabled', true).text('Adding...');

        fetch(`/api/learning-spaces/${spaceId}/learning-paths/${pathId}/learning-nodes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ title: title, description: desc, nodeType: type, learningPathId: pathId })
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to add lesson");
                return res.json();
            })
            .then(data => {
                window.location.reload();
            })
            .catch(err => {
                errorDiv.text(err.message).show();
                btn.prop('disabled', false).text('Add Lesson');
            });
    });

    // Update Node (Edit)
    $('#btnUpdateNode').click(function () {
        const pathId = $('#editNodePathId').val();
        const nodeId = $('#editNodeId').val();
        const title = $('#editNodeTitle').val();
        const desc = $('#editNodeDescription').val();
        const type = $('#editNodeType').val();
        const errorDiv = $('#editNodeError');
        const btn = $(this);

        errorDiv.hide();
        if (!title) {
            errorDiv.text("Lesson title is required").show();
            return;
        }

        btn.prop('disabled', true).text('Saving...');

        fetch(`/api/learning-spaces/${spaceId}/learning-paths/${pathId}/learning-nodes/${nodeId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ title: title, description: desc, nodeType: type, learningPathId: pathId })
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to update lesson");
                return res.json();
            })
            .then(data => {
                window.location.reload();
            })
            .catch(err => {
                errorDiv.text(err.message).show();
                btn.prop('disabled', false).text('Save Changes');
            });
    });

    // -------------------------------------------------------------------
    // Upload Video & Document Logic
    // -------------------------------------------------------------------
    let currentUploadNodeId = null;

    $(document).on('click', '.upload-video-btn', function (e) {
        e.preventDefault();
        currentUploadNodeId = $(this).data('nodeid');
        $('#hiddenVideoInput').click();
    });

    $(document).on('click', '.upload-document-btn', function (e) {
        e.preventDefault();
        currentUploadNodeId = $(this).data('nodeid');
        $('#hiddenDocumentInput').click();
    });

    $(document).on('click', '.add-youtube-btn', function (e) {
        e.preventDefault();
        const nodeId = $(this).data('nodeid');
        $('#youtubeNodeId').val(nodeId);
        $('#youtubeUrl').val('');
        $('#addYoutubeError').hide();
        $('#addYoutubeModal').modal('show');
    });

    $('#hiddenVideoInput').change(function () {
        if (!this.files || this.files.length === 0) return;
        uploadItemFile(this.files[0], `/api/learning-nodes/${currentUploadNodeId}/items/upload-video`, 'VIDEO');
        $(this).val('');
    });

    $('#hiddenDocumentInput').change(function () {
        if (!this.files || this.files.length === 0) return;
        uploadItemFile(this.files[0], `/api/learning-nodes/${currentUploadNodeId}/items/upload-document`, 'PDF');
        $(this).val('');
    });

    $('#btnSaveYoutube').click(function () {
        const nodeId = $('#youtubeNodeId').val();
        const url = $('#youtubeUrl').val();
        const errorDiv = $('#addYoutubeError');
        const btn = $(this);

        errorDiv.hide();
        if (!url) {
            errorDiv.text("Vui lòng nhập đường dẫn Youtube").show();
            return;
        }

        btn.prop('disabled', true).text('Đang thêm...');

        fetch(`/api/learning-nodes/${nodeId}/items`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                title: 'Youtube Video',
                itemType: 'VIDEO',
                url: url,
                learningNodeId: nodeId
            })
        })
            .then(res => {
                if (!res.ok) throw new Error("Lỗi lưu đường dẫn Youtube");
                return res.json();
            })
            .then(data => {
                window.location.reload();
            })
            .catch(err => {
                errorDiv.text(err.message).show();
                btn.prop('disabled', false).text('Add Video');
            });
    });

    function uploadItemFile(file, uploadUrl, itemType) {
        let formData = new FormData();
        formData.append('file', file);

        // Use a simple prompt for loading state, or Toast in a real app
        const loadingToast = document.createElement('div');
        loadingToast.className = 'position-fixed bottom-0 end-0 p-3';
        loadingToast.style.zIndex = '9999';
        loadingToast.innerHTML = `<div class="toast show align-items-center text-white bg-primary border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body"><i class="ti ti-loader ti-spin me-2"></i> Đang tải lên, vui lòng đợi...</div>
            </div>
        </div>`;
        document.body.appendChild(loadingToast);

        fetch(uploadUrl, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token },
            body: formData
        })
            .then(res => {
                if (!res.ok) throw new Error("Lỗi tải file lên server");
                return res.json();
            })
            .then(data => {
                const fileUrl = data.url;
                return fetch(`/api/learning-nodes/${currentUploadNodeId}/items`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({
                        title: file.name,
                        itemType: itemType,
                        url: fileUrl,
                        learningNodeId: currentUploadNodeId
                    })
                });
            })
            .then(res => {
                if (!res.ok) throw new Error("Lỗi lưu thông tin file vào bài học");
                window.location.reload();
            })
            .catch(err => {
                alert(err.message);
                document.body.removeChild(loadingToast);
            });
    }

    // Delete Item (Video/Document)
    $(document).on('click', '.delete-item-btn', function (e) {
        e.preventDefault();
        const itemId = $(this).data('itemid');
        if (confirm('Bạn có chắc muốn xóa file này?')) {
            fetch(`/api/learning-nodes/0/items/item/${itemId}`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            }).then(res => {
                if (res.ok) window.location.reload();
                else alert("Lỗi khi xóa file");
            });
        }
    });

    // -------------------------------------------------------------------
    // Fetch and render items dynamically
    // -------------------------------------------------------------------
    function extractYoutubeId(url) {
        const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
        const match = url.match(regExp);
        return (match && match[2].length === 11) ? match[2] : null;
    }

    $('.node-items-container').each(function () {
        const container = $(this);
        const nodeId = container.data('nodeid');
        const nodeType = container.data('nodetype');

        fetch(`/api/learning-nodes/${nodeId}/items`, {
            headers: { 'Authorization': 'Bearer ' + token }
        })
            .then(res => res.json())
            .then(items => {
                if (items && items.length > 0) {
                    let html = '';
                    items.forEach(item => {
                        if (item.itemType === 'VIDEO') {
                            let videoHtml = '';
                            if (item.fullUrl.includes('youtube.com') || item.fullUrl.includes('youtu.be')) {
                                const videoId = extractYoutubeId(item.fullUrl);
                                const embedUrl = videoId ? `https://www.youtube.com/embed/${videoId}` : item.fullUrl;
                                videoHtml = `<iframe src="${embedUrl}" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>`;
                            } else {
                                videoHtml = `<video src="${item.fullUrl}" controls></video>`;
                            }

                            html += `
                        <div class="mb-3">
                            <div class="ratio ratio-16x9 rounded-4 overflow-hidden shadow-sm border border-light" style="background: #000;">
                                ${videoHtml}
                            </div>
                            <div class="text-end mt-2">
                                <button class="btn btn-link text-danger text-decoration-none p-0 small fw-semibold delete-item-btn" data-itemid="${item.id}">
                                    <i class="ti ti-trash"></i>
                                </button>
                            </div>
                        </div>`;
                        } else if (item.itemType === 'PDF') {
                            html += `
                        <div class="mb-3">
                            <div class="border rounded-3 p-3 d-flex align-items-center gap-3 shadow-sm bg-white">
                                <div class="bg-danger-subtle text-danger p-2 rounded-2">
                                    <i class="ti ti-file-pdf fs-3"></i>
                                </div>
                                <div class="flex-grow-1 overflow-hidden">
                                    <a href="${item.fullUrl}" target="_blank" class="text-decoration-none text-dark">
                                        <h6 class="mb-0 text-truncate fw-bold hover-text-primary" style="transition: color 0.2s;">${item.title}</h6>
                                    </a>
                                </div>
                                <button class="btn btn-sm btn-outline-danger border-0 delete-item-btn" data-itemid="${item.id}" title="Remove Document">
                                    <i class="ti ti-trash"></i>
                                </button>
                            </div>
                        </div>`;
                        }
                    });
                    container.html(html);
                }
            })
            .catch(err => console.error('Failed to load items for node', nodeId, err));
    });
});
