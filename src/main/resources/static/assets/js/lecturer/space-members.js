const spaceId = $('#spaceId').val();

$(document).ready(function() {
    loadMembers();
});

function loadMembers() {
    $.ajax({
        url: `/api/spaces/${spaceId}/members`,
        method: 'GET',
        success: function(data) {
            renderTable(data.content);
        },
        error: function() {
            alert('Failed to load members.');
        }
    });
}

function renderTable(members) {
    let html = '';
    if(members.length === 0) {
        html = '<tr><td colspan="5" class="text-center py-4 text-muted">No members found.</td></tr>';
    } else {
        members.forEach(m => {
            let badgeClass = 'bg-secondary-subtle text-secondary border border-secondary';
            if (m.role === 'OWNER') badgeClass = 'bg-primary-subtle text-primary border border-primary';
            if (m.role === 'SUPPORTER') badgeClass = 'bg-success-subtle text-success border border-success';

            let actionsHtml = '';
            if (m.role !== 'OWNER') {
                if (m.role === 'MEMBER') {
                    actionsHtml += `<button class="btn btn-sm btn-outline-success me-2" onclick="promote(${m.memberId})"><i class="ti ti-arrow-up"></i> Promote</button>`;
                } else if (m.role === 'SUPPORTER') {
                    actionsHtml += `<button class="btn btn-sm btn-outline-warning me-2" onclick="demote(${m.memberId})"><i class="ti ti-arrow-down"></i> Demote</button>`;
                }
                actionsHtml += `<button class="btn btn-sm btn-outline-danger" onclick="removeMember(${m.memberId})"><i class="ti ti-trash"></i> Remove</button>`;
            } else {
                actionsHtml = '<span class="text-muted fst-italic">No actions</span>';
            }

            html += `
                <tr>
                    <td class="ps-4">
                        <div class="fw-semibold text-dark">${m.fullName || 'N/A'}</div>
                    </td>
                    <td>${m.email}</td>
                    <td><span class="badge ${badgeClass}">${m.role}</span></td>
                    <td>${new Date(m.joinedAt).toLocaleDateString()}</td>
                    <td class="text-end pe-4">${actionsHtml}</td>
                </tr>
            `;
        });
    }
    $('#members-table-body').html(html);
}

function promote(memberId) {
    if(!confirm('Promote this member to Supporter?')) return;
    $.ajax({
        url: `/api/spaces/${spaceId}/members/${memberId}/promote`,
        method: 'POST',
        success: function() {
            loadMembers();
        },
        error: function(err) {
            alert('Error promoting member');
        }
    });
}

function demote(memberId) {
    if(!confirm('Demote this supporter to Member?')) return;
    $.ajax({
        url: `/api/spaces/${spaceId}/members/${memberId}/demote`,
        method: 'POST',
        success: function() {
            loadMembers();
        },
        error: function(err) {
            alert('Error demoting member');
        }
    });
}

function removeMember(memberId) {
    if(!confirm('Are you sure you want to remove this member?')) return;
    $.ajax({
        url: `/api/spaces/${spaceId}/members/${memberId}`,
        method: 'DELETE',
        success: function() {
            loadMembers();
        },
        error: function(err) {
            alert('Error removing member');
        }
    });
}
