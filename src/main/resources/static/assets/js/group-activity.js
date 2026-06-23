// Group Activity UI Logic for FlippedClass

async function loadGroupActivityData(nodeId) {
    try {
        const actRes = await fetch(`/api/v1/activities/nodes/${nodeId}/details`, { headers: authHeaders(false) });
        if (!actRes.ok) throw new Error("Activity not found for this node");
        const activity = await actRes.json();
        
        let myGroup = null;
        try {
            const groupRes = await fetch(`/api/v1/activities/${activity.id}/my-group`, { headers: authHeaders(false) });
            if (groupRes.ok) myGroup = await groupRes.json();
        } catch(e) {}
        
        let availableGroups = [];
        if (!myGroup) {
            const availRes = await fetch(`/api/v1/activities/${activity.id}/available-groups`, { headers: authHeaders(false) });
            if (availRes.ok) availableGroups = await availRes.json();
        }
        return { activity, myGroup, availableGroups };
    } catch (error) {
        console.error(error);
        return null;
    }
}

window.renderGroupActivityPage = async function(node, nodeId) {
    const pageRoot = document.getElementById('pageRoot');
    if (!pageRoot) return;
    
    pageRoot.className = 'lesson-layout';
    pageRoot.innerHTML = `
        <div style="width: 100%; max-width: 900px; margin: 0 auto; padding: 2rem;">
            <div class="text-center" style="padding-top: 40px;">
                <i class="ti ti-loader-2 ti-spin" style="font-size: 2rem; color: #3b82f6;"></i>
                <p class="mt-2 text-muted">Đang tải không gian nhóm...</p>
            </div>
        </div>
    `;
    
    const data = await loadGroupActivityData(nodeId);
    if (!data) {
        pageRoot.innerHTML = `
            <div style="width: 100%; max-width: 900px; margin: 0 auto; padding: 2rem; text-align: center;">
                <h2 style="color: #ef4444;">Không tìm thấy Activity</h2>
                <p>Bài học này chưa được cấu hình Group Activity. Vui lòng liên hệ Giảng viên.</p>
            </div>
        `;
        return;
    }
    
    const { activity, myGroup, availableGroups } = data;
    
    const escapeHtml = (unsafe) => {
        return (unsafe || '').replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
    };

    const statusBadge = activity.status === 'OPEN' ? '<span class="ga-badge ga-badge-open"><i class="ti ti-circle-filled" style="font-size:8px;"></i> OPEN</span>' 
                      : activity.status === 'LOCKED' ? '<span class="ga-badge ga-badge-locked"><i class="ti ti-lock"></i> LOCKED</span>'
                      : '<span class="ga-badge ga-badge-draft"><i class="ti ti-circle-filled" style="font-size:8px;"></i> DRAFT</span>';

    const styles = `
        <style>
            #pageRoot.lesson-layout { display: flex; flex-direction: row; min-height: calc(100vh - 65px); background-image: url('/uploads/anh/group_activies.jpg'); background-size: cover; background-position: center; background-attachment: fixed; gap: 24px; padding: 24px; align-items: stretch; }
            @media (max-width: 992px) { #pageRoot.lesson-layout { flex-direction: column; } }
            .ga-left-col { flex: 1; display: flex; flex-direction: column; gap: 24px; width: 100%; max-width: 800px; margin: 0 auto; }
            .ga-right-col { flex: 1; display: flex; flex-direction: column; gap: 24px; width: 100%; max-width: 800px; margin: 0 auto; }
            .ga-card { background: rgba(255, 255, 255, 0.95); border-radius: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); padding: 24px; border: 1px solid rgba(255, 255, 255, 0.5); backdrop-filter: blur(10px); }
            .ga-title { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin-bottom: 8px; }
            .ga-badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 99px; font-size: 0.75rem; font-weight: 600; }
            .ga-badge-draft { background: #fef2f2; color: #ef4444; border: 1px solid #fecaca; }
            .ga-badge-open { background: #ecfdf5; color: #10b981; border: 1px solid #a7f3d0; }
            .ga-badge-locked { background: #f1f5f9; color: #64748b; border: 1px solid #cbd5e1; }
            .ga-btn { padding: 8px 16px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.2s; border: none; font-size: 0.875rem; }
            .ga-btn-primary { background: #4f46e5; color: white; }
            .ga-btn-primary:hover { background: #4338ca; }
            .ga-btn-outline { background: transparent; border: 1px solid #cbd5e1; color: #475569; }
            .ga-btn-outline:hover { background: #f8fafc; color: #0f172a; }
            .ga-btn-danger { background: #fef2f2; color: #ef4444; border: 1px solid #fecaca; }
            .ga-btn-danger:hover { background: #fee2e2; }
            .ga-input { width: 100%; padding: 10px 14px; border: 1px solid #cbd5e1; border-radius: 8px; outline: none; transition: border-color 0.2s; background: rgba(255,255,255,0.8); }
            .ga-input:focus { border-color: #4f46e5; box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1); background: #ffffff; }
            .member-list { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; }
            .member-item { display: flex; align-items: center; justify-content: space-between; padding: 12px; background: rgba(248, 250, 252, 0.8); border-radius: 12px; border: 1px solid rgba(226, 232, 240, 0.8); }
        </style>
    `;

    let ui = `
        ${styles}
        <div class="ga-left-col">
            <div class="ga-card">
                <div style="font-size: 0.75rem; font-weight: 700; color: #64748b; letter-spacing: 1px; margin-bottom: 8px; display: flex; align-items: center; gap: 6px;">
                    <i class="ti ti-users text-primary"></i> GROUP ACTIVITY
                </div>
                <h1 class="ga-title">${escapeHtml(activity.title)}</h1>
                <p style="color: #475569; font-size: 0.9rem; margin-bottom: 16px;">${escapeHtml(activity.description || 'Chưa có mô tả.')}</p>
                <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                    <span class="ga-badge" style="background: white; border: 1px solid #e2e8f0; color: #475569;"><i class="ti ti-calendar"></i> Deadline: ${activity.deadline ? new Date(activity.deadline).toLocaleDateString('vi-VN') : 'Chưa set'}</span>
                    <span class="ga-badge" style="background: white; border: 1px solid #e2e8f0; color: #475569;"><i class="ti ti-users"></i> Member: ${activity.maxMembers || 'N/A'}</span>
                    ${statusBadge}
                </div>
            </div>
    `;

    if (!myGroup) {
        if (activity.status === 'LOCKED') {
            ui += `
                <div class="ga-card">
                    <div style="text-align: center; padding: 2rem 0;">
                        <i class="ti ti-lock text-muted" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                        <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a; margin-bottom: 8px;">Hoạt động đã khóa</h2>
                        <p style="color: #64748b; font-size: 0.9rem;">Thời gian tham gia hoạt động nhóm này đã kết thúc. Bạn không thể tạo hoặc tham gia nhóm nữa.</p>
                    </div>
                </div>
            `;
        } else {
            ui += `
                <div class="ga-card" style="border: 1px dashed rgba(253, 224, 71, 0.8); background: rgba(254, 252, 232, 0.5);">
                    <h2 style="font-size: 1.25rem; font-weight: 700; color: #ca8a04; margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
                        <i class="ti ti-alert-circle"></i> Bạn chưa có nhóm
                    </h2>
                    <p style="color: #713f12; font-size: 0.9rem; margin-bottom: 16px;">Hãy tham gia một nhóm có sẵn hoặc tạo nhóm mới để bắt đầu làm bài.</p>
                    
                    <div style="display: flex; gap: 12px; margin-bottom: 24px;">
                        <input type="text" id="newGroupName" class="ga-input" placeholder="Tên nhóm mới..." style="flex: 1;" />
                        <button class="ga-btn ga-btn-primary" onclick="window.handleCreateGroup(${activity.id}, ${nodeId})"><i class="ti ti-plus"></i> Tạo nhóm</button>
                    </div>

                    <h3 style="font-size: 1rem; font-weight: 600; color: #0f172a; margin-bottom: 12px;">Các nhóm đang tìm thành viên:</h3>
                    ${availableGroups.length === 0 ? '<p style="color: #64748b; font-size: 0.9rem; font-style: italic;">Không có nhóm nào trống.</p>' : ''}
                    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 16px;">
                        ${availableGroups.map(g => `
                            <div style="background: white; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px;">
                                <div style="font-weight: 600; color: #0f172a; margin-bottom: 4px;">${escapeHtml(g.groupName)}</div>
                                <div style="font-size: 0.8rem; color: #64748b; margin-bottom: 12px;">${g.currentMembers} / ${g.maxMembers} thành viên</div>
                                <div id="join-btn-container-${g.id}">
                                    <button class="ga-btn ga-btn-outline" style="width: 100%; padding: 6px;" onclick="document.getElementById('join-btn-container-${g.id}').style.display='none'; document.getElementById('join-input-container-${g.id}').style.display='block';">Tham gia nhóm</button>
                                </div>
                                <div id="join-input-container-${g.id}" style="display: none;">
                                    <input type="text" id="inviteCode-${g.id}" class="ga-input" placeholder="Nhập Invite Code..." style="margin-bottom: 8px; padding: 6px 10px; font-size: 0.85rem;" />
                                    <div style="display: flex; gap: 8px;">
                                        <button class="ga-btn ga-btn-primary" style="flex: 1; padding: 6px;" onclick="window.handleJoinGroup(${activity.id}, ${g.id}, ${nodeId})">Vào nhóm</button>
                                        <button class="ga-btn ga-btn-outline" style="padding: 6px;" onclick="document.getElementById('join-input-container-${g.id}').style.display='none'; document.getElementById('join-btn-container-${g.id}').style.display='block';"><i class="ti ti-x"></i></button>
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        }
        
        ui += `
            </div> <!-- Close ga-left-col -->
            <div class="ga-right-col">
                 <div class="ga-card" style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #94a3b8; min-height: 400px; border: 1px dashed #cbd5e1; background: transparent;">
                     <i class="ti ti-messages" style="font-size: 3rem; margin-bottom: 16px;"></i>
                     <p>Tham gia nhóm để mở khóa thảo luận</p>
                 </div>
            </div>
        `;
    } else {
        const isLeader = myGroup.leaderId === parseInt(userId);
        ui += `
            <div class="ga-card">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px;">
                    <div>
                        <div style="font-size: 0.8rem; font-weight: 600; color: #10b981; margin-bottom: 4px; display: flex; align-items: center; gap: 6px;">
                            <i class="ti ti-check"></i> Trạng thái: Đã có nhóm
                        </div>
                        <h2 style="font-size: 1.5rem; font-weight: 700; color: #0f172a; display: flex; align-items: center; gap: 8px;">
                            ${escapeHtml(myGroup.groupName)}
                            <span class="ga-badge ga-badge-locked" style="font-size: 0.7rem;">Code: ${myGroup.inviteCode}</span>
                        </h2>
                        <p style="color: #64748b; font-size: 0.9rem;">${myGroup.members.length} / ${activity.maxMembers} Members | Vai trò của bạn: ${isLeader ? '<span style="color:#eab308; font-weight:bold;"><i class="ti ti-crown"></i> LEADER</span>' : 'Thành viên'}</p>
                    </div>
                    ${activity.status === 'LOCKED' ? '' : `<button class="ga-btn ga-btn-danger" onclick="window.handleLeaveGroup(${myGroup.id}, ${nodeId})"><i class="ti ti-logout"></i> Rời nhóm</button>`}
                </div>

                <div style="display: grid; grid-template-columns: 1.5fr 1fr; gap: 24px; margin-top: 24px;">
                    <div>
                        <h3 style="font-size: 1.1rem; font-weight: 600; border-bottom: 1px solid #e2e8f0; padding-bottom: 8px; margin-bottom: 12px;">Thành viên</h3>
                        <div class="member-list">
                            ${myGroup.members.map(m => `
                                <div class="member-item">
                                    <div style="display: flex; align-items: center; gap: 12px;">
                                        <div style="width: 36px; height: 36px; background: #e2e8f0; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #475569;">
                                            ${m.fullName.charAt(0).toUpperCase()}
                                        </div>
                                        <div>
                                            <div style="font-weight: 600; font-size: 0.9rem; color: #0f172a;">${escapeHtml(m.fullName)} ${m.userId === parseInt(userId) ? '(You)' : ''}</div>
                                            <div style="font-size: 0.75rem; color: #64748b;">Tham gia lúc: ${new Date(m.joinedAt).toLocaleDateString('vi-VN')}</div>
                                        </div>
                                    </div>
                                    ${m.role === 'LEADER' ? '<div style="color: #eab308;"><i class="ti ti-crown"></i></div>' : ''}
                                </div>
                            `).join('')}
                        </div>
                    </div>

                    <div>
                        <h3 style="font-size: 1.1rem; font-weight: 600; border-bottom: 1px solid #e2e8f0; padding-bottom: 8px; margin-bottom: 12px;">Nộp bài</h3>
                        ${myGroup.submission ? `
                            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px;">
                                <div style="font-size: 0.8rem; color: #10b981; font-weight: bold; margin-bottom: 8px;"><i class="ti ti-check"></i> Đã nộp bài</div>
                                <div style="font-size: 0.9rem; color: #0f172a; margin-bottom: 4px; word-break: break-all;">
                                    <a href="${myGroup.submission.githubRepoUrl}" target="_blank" style="color: #4f46e5;"><i class="ti ti-brand-github"></i> ${escapeHtml(myGroup.submission.githubRepoUrl)}</a>
                                </div>
                                ${myGroup.submission.score ? `<div style="margin-top: 12px; padding-top: 12px; border-top: 1px dashed #cbd5e1; font-weight: bold; color: #0f172a;">Điểm: ${myGroup.submission.score}</div>` : ''}
                                ${myGroup.submission.feedback ? `<div style="font-size: 0.85rem; color: #475569; margin-top: 4px;">Feedback: ${escapeHtml(myGroup.submission.feedback)}</div>` : ''}
                                
                                ${isLeader && activity.status === 'OPEN' ? `
                                    <button class="ga-btn ga-btn-outline" style="width: 100%; margin-top: 12px;" onclick="document.getElementById('submitForm').style.display='block'">Cập nhật Repo</button>
                                ` : ''}
                            </div>
                        ` : `
                            ${isLeader && activity.status === 'OPEN' ? `
                                <div style="background: #fefce8; border: 1px dashed #ca8a04; border-radius: 12px; padding: 16px; text-align: center;">
                                    <i class="ti ti-cloud-upload text-warning" style="font-size: 2rem;"></i>
                                    <p style="color: #854d0e; font-size: 0.85rem; margin-top: 8px;">Nhóm chưa nộp bài. Chỉ có LEADER mới có quyền nộp.</p>
                                    <button class="ga-btn ga-btn-primary" style="margin-top: 12px;" onclick="document.getElementById('submitForm').style.display='block'">Nộp bài ngay</button>
                                </div>
                            ` : `
                                <div style="background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 12px; padding: 16px; text-align: center;">
                                    <i class="ti ti-lock text-muted" style="font-size: 2rem;"></i>
                                    <p style="color: #64748b; font-size: 0.85rem; margin-top: 8px;">
                                        ${!isLeader ? 'Chỉ Leader mới có quyền nộp bài.' : 'Hoạt động đang bị khóa hoặc chưa mở.'}
                                    </p>
                                </div>
                            `}
                        `}
                        
                        <!-- Bảng nộp bài ẩn -->
                        <div id="submitForm" style="display: none; margin-top: 16px; background: white; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px;">
                            <h4 style="font-size: 0.9rem; font-weight: 600; margin-bottom: 8px;">Đường dẫn Github Repo:</h4>
                            <input type="text" id="repoUrlInput" class="ga-input" placeholder="https://github.com/..." style="margin-bottom: 12px;" value="${myGroup.submission ? myGroup.submission.githubRepoUrl : ''}" />
                            <textarea id="submitNoteInput" class="ga-input" placeholder="Ghi chú (tùy chọn)..." rows="2" style="margin-bottom: 12px; resize: vertical;">${myGroup.submission && myGroup.submission.note ? myGroup.submission.note : ''}</textarea>
                            <div style="display: flex; gap: 8px;">
                                <button class="ga-btn ga-btn-primary" style="flex: 1;" onclick="window.handleSubmitWork(${myGroup.id}, ${nodeId})">Gửi</button>
                                <button class="ga-btn ga-btn-outline" onclick="document.getElementById('submitForm').style.display='none'">Hủy</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            </div> <!-- Close ga-left-col -->

            <!-- START COMMENTS SECTION -->
            <div class="ga-right-col">
                <div class="ga-card" style="display: flex; flex-direction: column; height: 100%;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                        <div>
                            <h2 style="font-size: 1.25rem; font-weight: 700; color: #0f172a;">Comments</h2>
                            <p style="color: #64748b; font-size: 0.9rem;">Discussions and questions related to this group.</p>
                        </div>
                        <span class="ga-badge" style="background: white; border: 1px solid #e2e8f0; color: #475569;" id="commentsCountBadge">0</span>
                    </div>
                    <div id="nodeCommentsContainer" style="flex: 1; overflow-y: auto;">
                        <div style="text-align: center; padding: 2rem; color: #64748b;">
                            <i class="ti ti-loader-2 ti-spin" style="font-size: 1.5rem;"></i>
                            <div style="margin-top: 8px;">Loading comments...</div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- END COMMENTS SECTION -->
        `;
        
        // Cập nhật currentGroupId và gọi loadComments
        window.currentGroupId = myGroup.id;
        setTimeout(() => {
            if (typeof window.loadComments === 'function') {
                window.loadComments();
            }
        }, 100);
    }

    pageRoot.innerHTML = ui;
}

window.handleCreateGroup = async (activityId, nodeId) => {
    const groupName = document.getElementById('newGroupName').value.trim();
    if (!groupName) return alert('Vui lòng nhập tên nhóm!');
    try {
        const res = await fetch(`/api/v1/activities/${activityId}/groups`, {
            method: 'POST',
            headers: authHeaders(true),
            body: JSON.stringify({ groupName })
        });
        if (!res.ok) throw new Error(await readError(res));
        window.renderGroupActivityPage({ nodeType: 'GROUP_ACTIVITY' }, nodeId);
    } catch (e) { alert(e.message); }
};

window.handleJoinGroup = async (activityId, groupId, nodeId) => {
    const inviteCodeInput = document.getElementById(`inviteCode-${groupId}`);
    const inviteCode = inviteCodeInput ? inviteCodeInput.value.trim() : prompt('Vui lòng nhập Invite Code của nhóm này:');
    if (!inviteCode) {
        alert('Vui lòng nhập Invite Code!');
        return;
    }
    
    try {
        const res = await fetch(`/api/v1/activities/${activityId}/groups/${groupId}/join?inviteCode=${encodeURIComponent(inviteCode)}`, {
            method: 'POST',
            headers: authHeaders()
        });
        if (!res.ok) throw new Error(await readError(res));
        window.renderGroupActivityPage({ nodeType: 'GROUP_ACTIVITY' }, nodeId);
    } catch (e) { alert(e.message); }
};

window.handleLeaveGroup = async (groupId, nodeId) => {
    if(!confirm('Bạn có chắc chắn muốn rời nhóm?')) return;
    try {
        const res = await fetch(`/api/v1/groups/${groupId}/leave`, {
            method: 'DELETE',
            headers: authHeaders()
        });
        if (!res.ok) throw new Error(await readError(res));
        window.renderGroupActivityPage({ nodeType: 'GROUP_ACTIVITY' }, nodeId);
    } catch (e) { alert(e.message); }
};

window.handleSubmitWork = async (groupId, nodeId) => {
    const repoUrl = document.getElementById('repoUrlInput').value.trim();
    const note = document.getElementById('submitNoteInput').value.trim();
    if (!repoUrl || !repoUrl.startsWith('https://github.com/')) return alert('URL phải bắt đầu bằng https://github.com/');
    try {
        const res = await fetch(`/api/v1/groups/${groupId}/submission`, {
            method: 'PUT',
            headers: authHeaders(true),
            body: JSON.stringify({ githubRepoUrl: repoUrl, note })
        });
        if (!res.ok) throw new Error(await readError(res));
        window.renderGroupActivityPage({ nodeType: 'GROUP_ACTIVITY' }, nodeId);
    } catch (e) { alert(e.message); }
};
