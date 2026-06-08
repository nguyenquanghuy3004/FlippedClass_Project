$(document).ready(function() {
    const spaceId = $('#spaceId').val();
    let token = localStorage.getItem('jwt_token');
    
    // Temporary fallback if user logged in differently
    if (!token) {
        token = localStorage.getItem('token');
    }

    if (!token) {
        window.location.href = '/login';
    }
    
    // Hide New Space button in sidebar since we are in the learning path creator
    $('#sidebar .mt-auto').hide();

    // Update stats from rendered DOM
    $('#totalModules').text($('.edit-path-btn').length);
    $('#totalLessons').text($('.edit-node-btn').length);

    // ---------------------------------------------------------
    // BIND EVENT LISTENERS (DOM already rendered by Thymeleaf)
    // ---------------------------------------------------------

    $('.archive-path-btn').click(function(e) {
        e.preventDefault();
        const id = $(this).data('id');
        fetch(`/api/learning-spaces/${spaceId}/learning-paths/${id}/archive`, {
            method: 'PUT',
            headers: { 'Authorization': 'Bearer ' + token }
        }).then(res => {
            if (res.ok) window.location.reload();
            else alert("Failed to archive");
        });
    });

    $('.delete-path-btn').click(function(e) {
        e.preventDefault();
        const id = $(this).data('id');
        if (confirm('Are you sure you want to permanently delete this module?')) {
            fetch(`/api/learning-spaces/${spaceId}/learning-paths/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            }).then(res => {
                if (res.ok) window.location.reload();
                else alert("Failed to delete");
            });
        }
    });

    $('.edit-path-btn').click(function(e) {
        e.preventDefault();
        const id = $(this).data('id');
        const title = $(this).data('title');
        const description = $(this).data('description');
        
        $('#editModuleId').val(id);
        $('#editModuleTitle').val(title);
        $('#editModuleDescription').val(description !== 'null' ? description : '');
        $('#editModuleError').hide();
        $('#editModuleModal').modal('show');
    });

    // Create Module
    $('#btnSaveModule').click(function() {
        const title = $('#moduleTitle').val();
        const desc = $('#moduleDescription').val();
        const errorDiv = $('#createModuleError');
        const btn = $(this);
        
        errorDiv.hide();
        
        if (!title) {
            errorDiv.text("Title is required").show();
            return;
        }

        btn.prop('disabled', true).text('Creating...');

        fetch(`/api/learning-spaces/${spaceId}/learning-paths`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ title: title, description: desc })
        })
        .then(res => {
            if (!res.ok) throw new Error("Failed to create module");
            return res.json();
        })
        .then(data => {
            window.location.reload();
        })
        .catch(err => {
            errorDiv.text(err.message).show();
            btn.prop('disabled', false).text('Create Module');
        });
    });

    // Edit Module
    $('#btnUpdateModule').click(function() {
        const id = $('#editModuleId').val();
        const title = $('#editModuleTitle').val();
        const desc = $('#editModuleDescription').val();
        const errorDiv = $('#editModuleError');
        const btn = $(this);
        
        errorDiv.hide();
        
        if (!title) {
            errorDiv.text("Title is required").show();
            return;
        }

        btn.prop('disabled', true).text('Saving...');

        fetch(`/api/learning-spaces/${spaceId}/learning-paths/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ title: title, description: desc })
        })
        .then(res => {
            if (!res.ok) throw new Error("Failed to update module");
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

    // Delete All
    $('#btnDeleteAll').click(function() {
        if (confirm('WARNING: Are you sure you want to delete ALL modules in this space? This action cannot be undone.')) {
            fetch(`/api/learning-spaces/${spaceId}/learning-paths`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(res => {
                if (res.ok) window.location.reload();
                else alert("Failed to delete all modules.");
            });
        }
    });

    // Trash Bin
    $('#btnOpenTrash').click(function() {
        fetch(`/api/learning-spaces/${spaceId}/learning-paths/deleted`, {
            headers: { 'Authorization': 'Bearer ' + token }
        })
        .then(res => res.json())
        .then(data => {
            const trashList = document.getElementById('trashList');
            trashList.innerHTML = '';
            
            if (!data || data.length === 0) {
                trashList.innerHTML = '<div class="text-center py-5 text-muted"><i class="ti ti-ghost fs-1 d-block mb-2"></i>Trash is empty.</div>';
                return;
            }
            
            data.forEach(path => {
                const item = document.createElement('div');
                item.className = 'list-group-item d-flex justify-content-between align-items-center py-3';
                item.innerHTML = `
                    <div>
                        <h6 class="mb-1 fw-bold">${path.title}</h6>
                        <small class="text-muted">Deleted module</small>
                    </div>
                    <button class="btn btn-sm btn-outline-success rounded-pill fw-bold px-3 restore-path-btn" data-id="${path.id}">
                        <i class="ti ti-refresh"></i> Restore
                    </button>
                `;
                trashList.appendChild(item);
            });
            
            // Bind restore
            $('.restore-path-btn').click(function() {
                const id = $(this).data('id');
                fetch(`/api/learning-spaces/${spaceId}/learning-paths/${id}/restore`, {
                    method: 'PUT',
                    headers: { 'Authorization': 'Bearer ' + token }
                })
                .then(res => {
                    if (res.ok) {
                        window.location.reload();
                    }
                });
            });
        });
    });

});
