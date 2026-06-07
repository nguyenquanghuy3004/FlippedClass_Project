$(document).ready(function() {
    $('.filter-pill').click(function() {
        $('.filter-pill').removeClass('active');
        $(this).addClass('active');
    });

    const token = localStorage.getItem('jwt_token');
    
    const banners = ['banner-green', 'banner-blue', 'banner-darkblue', 'banner-red', 'banner-gray'];
    const avatars = ['avatar-blue', 'avatar-orange', 'avatar-green', 'avatar-red'];

    function fetchSpaces() {
        fetch('/api/learning-spaces/my-spaces', {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(res => {
            if (res.status === 401) {
                alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                localStorage.removeItem('jwt_token');
                window.location.href = '/signin';
                throw new Error("Unauthorized");
            }
            if(!res.ok) throw new Error("Failed to fetch");
            return res.json();
        })
        .then(data => {
            const activeSpaces = data.filter(space => space.status !== 'DELETE');
            const trashedSpaces = data.filter(space => space.status === 'DELETE');
            
            const grid = document.getElementById('spaceGrid');
            grid.innerHTML = '';
            
            $('#statActiveSpaces').text(activeSpaces.length);

            // Populate Active Spaces
            activeSpaces.forEach((space, index) => {
                const bannerClass = banners[index % banners.length];
                const avatarClass = avatars[index % avatars.length];
                const firstLetter = space.name ? space.name.charAt(0).toUpperCase() : 'S';
                const subtitle = space.description ? space.description : 'No description provided';
                
                // Check visibility
                const visText = space.visibility || 'PRIVATE';

                const template = document.getElementById('spaceCardTemplate');
                const clone = template.content.cloneNode(true);
                
                // Bind data to template elements
                const banner = clone.querySelector('.card-banner');
                banner.classList.add(bannerClass);
                
                clone.querySelector('.card-title-link').href = '/lecturer/space/' + space.id;
                clone.querySelector('.course-title').textContent = space.name;
                clone.querySelector('.course-subtitle').textContent = subtitle;
                clone.querySelector('.visibility-text').textContent = visText;
                
                const status = space.status || 'ACTIVE';
                const statusIndicator = clone.querySelector('.status-indicator');
                statusIndicator.classList.add('status-' + status);
                statusIndicator.title = 'Status: ' + status;
                
                const avatar = clone.querySelector('.card-avatar');
                avatar.classList.add(avatarClass);
                avatar.textContent = firstLetter;
                
                clone.querySelector('.share-btn').dataset.code = space.inviteCode;
                
                const editBtn = clone.querySelector('.edit-space-btn');
                editBtn.dataset.id = space.id;
                editBtn.dataset.name = space.name;
                editBtn.dataset.description = space.description || '';
                editBtn.dataset.visibility = space.visibility || 'PRIVATE';
                
                const archiveBtn = clone.querySelector('.archive-space-btn');
                archiveBtn.dataset.id = space.id;
                archiveBtn.dataset.status = status;
                
                if (status === 'ARCHIVE') {
                    archiveBtn.querySelector('i').className = 'ti ti-archive-off';
                    archiveBtn.querySelector('.archive-text').textContent = 'Unarchive Space';
                } else {
                    archiveBtn.querySelector('i').className = 'ti ti-archive';
                    archiveBtn.querySelector('.archive-text').textContent = 'Archive Space';
                }
                
                clone.querySelector('.delete-space-btn').dataset.id = space.id;
                
                grid.appendChild(clone);
            });
            
            // Populate Trash Modal
            const trashList = document.getElementById('trashList');
            trashList.innerHTML = '';
            if (trashedSpaces.length === 0) {
                trashList.innerHTML = '<div class="text-center py-5 text-muted"><i class="ti ti-ghost fs-1 d-block mb-2"></i>Trash is empty.</div>';
            } else {
                trashedSpaces.forEach(space => {
                    const item = document.createElement('div');
                    item.className = 'list-group-item d-flex justify-content-between align-items-center py-3';
                    item.innerHTML = `
                        <div>
                            <h6 class="mb-1 fw-bold">${space.name}</h6>
                            <small class="text-muted">Deleted on: ${new Date(space.createdAt).toLocaleDateString()}</small>
                        </div>
                        <button class="btn btn-sm btn-outline-success rounded-pill fw-bold px-3 restore-space-btn" data-id="${space.id}">
                            <i class="ti ti-refresh"></i> Restore
                        </button>
                    `;
                    trashList.appendChild(item);
                });
                
                // Bind restore events
                document.querySelectorAll('.restore-space-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const spaceId = this.dataset.id;
                        if(confirm('Are you sure you want to restore this learning space?')) {
                            fetch(`/api/learning-spaces/${spaceId}/restore`, {
                                method: 'PUT',
                                headers: {
                                    'Authorization': 'Bearer ' + token
                                }
                            })
                            .then(response => {
                                if (response.ok) {
                                    fetchSpaces(); // reload both grid and trash
                                } else {
                                    alert('Failed to restore space.');
                                }
                            })
                            .catch(err => {
                                console.error('Error:', err);
                                alert('An error occurred while restoring.');
                            });
                        }
                    });
                });
            }
        })
        .catch(err => console.error('Error fetching spaces', err));
    }

    if (token) {
        fetchSpaces();
    } else {
        window.location.href = '/signin';
    }

    $('#btnCreateSpace').click(function() {
        const name = $('#spaceName').val();
        const desc = $('#spaceDescription').val();
        const vis = $('#spaceVisibility').val();
        const errorDiv = $('#createSpaceError');
        const btn = $(this);
        
        errorDiv.hide();
        
        if (!name) {
            errorDiv.text("Name is required").show();
            return;
        }

        const requestBody = {
            name: name,
            description: desc,
            visibility: vis
        };

        btn.prop('disabled', true).text('Creating...');

        fetch('/api/learning-spaces', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(requestBody)
        })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { throw new Error(text) });
            }
            return res.json();
        })
        .then(data => {
            $('#createSpaceModal').modal('hide');
            $('#createSpaceForm')[0].reset();
            fetchSpaces(); 
        })
        .catch(err => {
            errorDiv.text("Error: " + err.message).show();
        })
        .finally(() => {
            btn.prop('disabled', false).text('Create Space');
        });
    });

    // Invite Share Button click
    $(document).on('click', '.share-btn', function(e) {
        e.preventDefault();
        let code = $(this).data('code') || 'N/A';
        
        // Format code using CSS letter-spacing instead of physical spaces to prevent overflow
        let displayCode = code;

        $('#displayInviteCode').text(displayCode);
        $('#copyInviteCodeBtn').data('code', code).html('<i class="ti ti-copy fs-5"></i> Copy Code').removeClass('btn-success').addClass('btn-primary').css({'background-color': '#5a67d8', 'border-color': '#5a67d8'});
        $('#inviteModal').modal('show');
    });

    // Copy Code Button click
    $('#copyInviteCodeBtn').click(function() {
        const code = $(this).data('code');
        const btn = $(this);
        if (code && code !== 'N/A') {
            navigator.clipboard.writeText(code).then(() => {
                btn.html('<i class="ti ti-check fs-5"></i> Copied!')
                   .removeClass('btn-primary')
                   .addClass('btn-success')
                   .css({'background-color': '#10b981', 'border-color': '#10b981'});
                
                setTimeout(() => {
                    btn.html('<i class="ti ti-copy fs-5"></i> Copy Code')
                       .removeClass('btn-success')
                       .addClass('btn-primary')
                       .css({'background-color': '#5a67d8', 'border-color': '#5a67d8'});
                }, 2000);
            }).catch(err => {
                console.error('Failed to copy text: ', err);
                alert('Failed to copy. Please manually select the code.');
            });
        }
    });

    // Edit Space Action
    $(document).on('click', '.edit-space-btn', function(e) {
        e.preventDefault();
        $('#editSpaceId').val($(this).data('id'));
        $('#editSpaceName').val($(this).data('name'));
        $('#editSpaceDescription').val($(this).data('description'));
        $('#editSpaceVisibility').val($(this).data('visibility'));
        $('#editSpaceError').hide();
        $('#editSpaceModal').modal('show');
    });

    // Submit Edit Space Update
    $('#btnUpdateSpace').click(function() {
        const id = $('#editSpaceId').val();
        const name = $('#editSpaceName').val();
        const desc = $('#editSpaceDescription').val();
        const vis = $('#editSpaceVisibility').val();
        const errorDiv = $('#editSpaceError');
        const btn = $(this);
        
        errorDiv.hide();
        if (!name) {
            errorDiv.text("Name is required").show();
            return;
        }

        const requestBody = { name: name, description: desc, visibility: vis };
        btn.prop('disabled', true).text('Saving...');

        fetch(`/api/learning-spaces/${id}/update`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(requestBody)
        })
        .then(res => {
            if (res.ok) {
                $('#editSpaceModal').modal('hide');
                fetchSpaces(); // reload UI
            } else {
                res.json().then(data => errorDiv.text(data.message || "Error updating space").show());
            }
        })
        .catch(err => errorDiv.text("Connection error").show())
        .finally(() => btn.prop('disabled', false).text('Save Changes'));
    });

    // Archive/Unarchive Space Action
    $(document).on('click', '.archive-space-btn', function(e) {
        e.preventDefault();
        const id = $(this).data('id');
        const status = $(this).data('status');
        
        if (status === 'ARCHIVE') {
            // Unarchive -> Restore to ACTIVE
            fetch(`/api/learning-spaces/${id}/restore`, {
                method: 'PUT',
                headers: { 'Authorization': 'Bearer ' + token }
            }).then(res => { if(res.ok) fetchSpaces(); });
        } else {
            // Archive
            fetch(`/api/learning-spaces/${id}/archive`, {
                method: 'PUT',
                headers: { 'Authorization': 'Bearer ' + token }
            }).then(res => { if(res.ok) fetchSpaces(); });
        }
    });

    // Delete Space Action
    $(document).on('click', '.delete-space-btn', function(e) {
        e.preventDefault();
        if(confirm('Are you sure you want to delete this space?')) {
            const id = $(this).data('id');
            fetch(`/api/learning-spaces/${id}/delete`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            }).then(res => { if(res.ok) fetchSpaces(); });
        }
    });
});
