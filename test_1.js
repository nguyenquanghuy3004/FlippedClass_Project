
    /* --- Inlined from student-learning-node.js --- */
    let myCodeEditor; // Biến toàn cục để lưu instance của editor
    const token = localStorage.getItem('jwt_token');
    const userId = localStorage.getItem('user_id');
    const urlParams = new URLSearchParams(window.location.search);
    const pathNodeMatch = window.location.pathname.match(/\/student\/learning-nodes\/(\d+)/);
    const nodeId = urlParams.get('nodeId') || pathNodeMatch?.[1] || '';
    const pageRoot = document.getElementById('pageRoot');
    const studentName = document.getElementById('studentName');

    studentName.textContent = localStorage.getItem('full_name') || localStorage.getItem('username') || 'Student';

    function authHeaders(includeJson = true) {
      const headers = { Authorization: `Bearer ${token}` };
      if (includeJson) headers['Content-Type'] = 'application/json';
      return headers;
    }

    function escapeHtml(value) {
      return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
    }

    async function readError(response) {
      const text = await response.text();
      if (!text) return 'Something went wrong. Please try again.';
      try {
        const json = JSON.parse(text);
        return json.message || json.error || text;
      } catch {
        return text;
      }
    }

    function normalizeItemType(type) {
      return String(type || '').toUpperCase();
    }

    function itemUrl(item) {
      return item.fullUrl || item.url || '';
    }

    const resourceLogoSources = {
      DOC: '/uploads/anh/1200x630wa.jpg',
      VIDEO: '/uploads/anh/R.jpg',
      YOUTUBE: '/uploads/anh/OIP.jpg',
      PDF: '/uploads/anh/pdf-file-download-symbols-format-for-texts-vector.jpg',
      GOOGLE_DRIVE: '/uploads/anh/Google-Drive-Logo-2020-present.jpg'
    };

    // Hàm hỗ trợ: Chuyển đổi link video YouTube thường thành dạng link nhúng (embed) để có thể xem trực tiếp trên web
    function youtubeEmbedUrl(url) {
      const value = String(url || '');
      const watchMatch = value.match(/[?&]v=([^&]+)/);
      const shortMatch = value.match(/youtu\.be\/([^?&]+)/);
      const embedMatch = value.match(/youtube\.com\/embed\/([^?&]+)/);
      const id = watchMatch?.[1] || shortMatch?.[1] || embedMatch?.[1];
      return id ? `https://www.youtube.com/embed/${id}` : '';
    }

    function statusLabel(status) {
      const labels = {
        ACTIVE: 'Active',
        DRAFT: 'Draft',
        LOCKED: 'Locked',
        UNLOCKED: 'Unlocked',
        ARCHIVED: 'Archived',
        DELETED: 'Deleted'
      };
      return labels[status] || status || 'Unknown';
    }

    function nodeTypeLabel(type) {
      const labels = {
        LESSON: 'Lesson',
        VIDEO: 'Video',
        PRACTICE: 'Practice',
        QUIZ: 'Quiz',
        REVIEW: 'Review'
      };
      return labels[type] || type || 'Node';
    }

    function resourceTypeLabel(type) {
      const labels = {
        PDF: 'Document PDF',
        CODE: 'Source Code',
        QUIZ: 'Quiz',
        DISCUSSION: 'Discussion',
        VIDEO: 'Video'
      };
      return labels[normalizeItemType(type)] || 'Resource';
    }

    function resourceIcon(type) {
      const icons = {
        PDF: 'ti-file-type-pdf',
        CODE: 'ti-code',
        QUIZ: 'ti-brain',
        DISCUSSION: 'ti-message-circle',
        VIDEO: 'ti-video'
      };
      return icons[normalizeItemType(type)] || 'ti-file';
    }

    function resourceLogo(item) {
      const type = normalizeItemType(item?.itemType);
      const url = itemUrl(item).toLowerCase();
      const title = String(item?.title || '').toLowerCase();
      const sourceText = `${url} ${title}`;

      if (sourceText.includes('youtube') || sourceText.includes('youtu.be')) {
        return resourceLogoSources.YOUTUBE;
      }
      if (sourceText.includes('drive.google')) {
        return resourceLogoSources.GOOGLE_DRIVE;
      }
      if (type === 'PDF' || sourceText.includes('.pdf')) {
        return resourceLogoSources.PDF;
      }
      if (type === 'VIDEO' || /\.(mp4|webm|mov|avi|mkv)(?:$|[?#])/.test(url) || sourceText.includes('video')) {
        return resourceLogoSources.VIDEO;
      }
      if (sourceText.includes('.doc') || sourceText.includes('docx') || sourceText.includes('document')) {
        return resourceLogoSources.DOC;
      }
      return '';
    }

    function resourceIconTemplate(item) {
      const logo = resourceLogo(item);
      if (logo) {
        return `
      <span class="resource-icon resource-icon-image">
        <img class="resource-logo-img" src="${escapeHtml(logo)}" alt="${escapeHtml(resourceTypeLabel(item?.itemType))} logo" loading="lazy">
      </span>`;
      }
      return `<span class="resource-icon"><i class="ti ${resourceIcon(item?.itemType)}"></i></span>`;
    }

    function emptyLightTemplate(icon, message) {
      return `
      <div class="empty-light">
        <div>
          <i class="ti ${icon}"></i>
          ${escapeHtml(message)}
        </div>
      </div>`;
    }

    function videoTemplate(node, items) {
      const video = items.find((item) => normalizeItemType(item.itemType) === 'VIDEO' && itemUrl(item));
      if (!video) {
        return `
        <div class="empty-video">
          <div>
            <i class="ti ti-video-off"></i>
            Your instructor has not added a video for this node yet.
          </div>
        </div>`;
      }

      const url = itemUrl(video);
      const embedUrl = youtubeEmbedUrl(url);
      const player = embedUrl
        ? `<iframe src="${escapeHtml(embedUrl)}" title="${escapeHtml(video.title || node.title || 'Lesson Video')}" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>`
        : `<video controls preload="metadata" src="${escapeHtml(url)}"></video>`;

      return `<div class="video-frame">${player}</div>`;
    }

    function resourceItemTemplate(item) {
      const type = normalizeItemType(item.itemType);
      const url = type === 'QUIZ' && item.quizId
        ? `/student/take-quiz?quizId=${encodeURIComponent(item.quizId)}`
        : itemUrl(item);
      const body = `
      ${resourceIconTemplate(item)}
      <span>
        <span class="resource-title">${escapeHtml(item.title || resourceTypeLabel(item.itemType))}</span>
        <span class="resource-meta">${escapeHtml(resourceTypeLabel(item.itemType))}</span>
      </span>
      <i class="ti ti-arrow-up-right text-primary"></i>`;

      return url
        ? `<a class="resource-item" href="${escapeHtml(url)}" ${type === 'QUIZ' ? '' : 'target="_blank" rel="noopener"'}>${body}</a>`
        : `<div class="resource-item">${body}</div>`;
    }

    function resourcesTemplate(items) {
      const resources = items.filter((item) => !['VIDEO', 'DISCUSSION'].includes(normalizeItemType(item.itemType)));
      if (!resources.length) return emptyLightTemplate('ti-folder-off', 'No resources have been added for this node yet.');
      return `<div class="resource-list">${resources.map((item) => resourceItemTemplate(item)).join('')}</div>`;
    }

    let currentComments = [];

    async function loadComments() {
      const container = document.getElementById('nodeCommentsContainer');
      const badge = document.getElementById('commentsCountBadge');
      if (!container) return;

      try {
        const response = await fetch(`/api/nodes/${nodeId}/discussions`, { headers: authHeaders(false) });
        if (response.status === 403) {
          container.innerHTML = emptyLightTemplate('ti-lock', 'You do not have permission to comment in this class.');
          if (badge) badge.textContent = '0';
          return;
        }
        if (!response.ok) throw new Error(await readError(response));

        currentComments = await response.json();
        renderCommentsUI();
      } catch (err) {
        container.innerHTML = emptyLightTemplate('ti-alert-circle', err.message || 'Could not load comments.');
      }
    }

    function formatTime(dateString) {
      if (!dateString) return '';
      const d = new Date(dateString);
      return d.toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    }

    function renderCommentItem(comment, isReply = false, rootId = null) {
      const isOwner = String(comment.authorId) === String(userId);
      const defaultAvt = '/assets/images/avatar-1.jpg';
      const avatar = comment.authorAvatar || defaultAvt;
      const author = comment.authorName || 'Student';
      const time = formatTime(comment.createdAt);
      const currentRootId = rootId || comment.id;
      const safeAuthor = author.replace(/'/g, "\\'").replace(/"/g, '&quot;');

      let html = `
      <div class="comment-item ${isReply ? 'comment-reply' : ''}" style="display: flex; gap: 12px; margin-bottom: 16px;">
        <img src="${escapeHtml(avatar)}" alt="avatar" style="width: 32px; height: 32px; border-radius: 50%; object-fit: cover;" onerror="this.src='${defaultAvt}'">
        <div style="flex: 1; background: #f8f9fa; border-radius: 8px; padding: 10px 14px;">
          <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 4px;">
            <strong style="font-size: 0.9rem; color: #1e293b;">${escapeHtml(author)}</strong>
            <span style="font-size: 0.75rem; color: #64748b;">${escapeHtml(time)}</span>
          </div>
          <div style="font-size: 0.875rem; color: #334155; margin-bottom: 8px; white-space: pre-wrap;" id="comment-content-${comment.id}">${escapeHtml(comment.content)}</div>

          <div style="display: flex; gap: 12px; font-size: 0.75rem; color: #64748b;">
            <button type="button" onclick="openReplyBox(${currentRootId}, '${safeAuthor}')" style="border:none; background:none; color:#64748b; cursor:pointer; padding:0;">Reply</button>
            ${isOwner ? `<button type="button" onclick="deleteComment(${comment.id})" style="border:none; background:none; color:#ef4444; cursor:pointer; padding:0;">Delete</button>` : ''}
          </div>

          <div id="edit-form-${comment.id}" style="display: none; margin-top: 8px;">
          </div>

          <!-- Reply Form -->
          ${!isReply ? `
          <div id="reply-form-${comment.id}" style="display: none; margin-top: 12px;">
            <textarea id="reply-input-${comment.id}" placeholder="Write a reply..." class="form-control" style="width: 100%; border-radius: 6px; padding: 8px; border: 1px solid #e2e8f0; font-size: 0.875rem; margin-bottom: 8px;" rows="2"></textarea>
            <div style="display: flex; gap: 8px;">
              <button type="button" onclick="submitReply(${comment.id})" style="padding: 4px 12px; font-size: 0.75rem; border-radius: 4px; background: #3b82f6; color: white; border: none; cursor: pointer;">Reply</button>
              <button type="button" onclick="toggleReplyForm(${comment.id})" style="padding: 4px 12px; font-size: 0.75rem; border-radius: 4px; background: #f1f5f9; color: #475569; border: none; cursor: pointer;">Cancel</button>
            </div>
          </div>
          ` : ''}
        </div>
      </div>
    `;

      if (!isReply && comment.replies && comment.replies.length > 0) {
        html += `<div style="padding-left: 32px; border-left: 2px solid #e2e8f0; margin-left: 16px; margin-bottom: 16px;">`;
        comment.replies.forEach(reply => {
          html += renderCommentItem(reply, true, comment.id);
        });
        html += `</div>`;
      }

      return html;
    }

    function renderCommentsUI() {
      const container = document.getElementById('nodeCommentsContainer');
      const badge = document.getElementById('commentsCountBadge');
      if (!container) return;

      let totalCount = currentComments.length;
      currentComments.forEach(c => totalCount += (c.replies ? c.replies.length : 0));
      if (badge) badge.textContent = totalCount;

      let body = '';
      if (currentComments.length === 0) {
        body = emptyLightTemplate('ti-message-off', 'No comments yet. Be the first to start a discussion!');
      } else {
        body = `<div class="comment-list" style="max-height: 400px; overflow-y: auto; padding-right: 8px;">
        ${currentComments.map(c => renderCommentItem(c, false)).join('')}
      </div>`;
      }

      container.innerHTML = `
      ${body}
      <div class="comment-compose" style="margin-top: 16px; border-top: 1px solid #e2e8f0; padding-top: 16px;">
        <textarea id="rootCommentInput" placeholder="Write a comment or question..." style="width: 100%; border-radius: 8px; padding: 10px; border: 1px solid #cbd5e1; font-size: 0.875rem; resize: vertical; margin-bottom: 8px;" rows="3"></textarea>
        <button type="button" onclick="submitRootComment()" id="rootCommentBtn" style="width: 100%; padding: 8px; border-radius: 6px; background: #2563eb; color: white; border: none; font-weight: 500; cursor: pointer; transition: background 0.2s;">Send Comment</button>
      </div>
    `;

      const input = document.getElementById('rootCommentInput');
      const btn = document.getElementById('rootCommentBtn');
      if (input && btn) {
        input.addEventListener('input', () => {
          btn.disabled = input.value.trim().length === 0;
          btn.style.opacity = btn.disabled ? '0.6' : '1';
        });
        btn.disabled = true;
        btn.style.opacity = '0.6';
      }

      const targetCommentId = urlParams.get('commentId');
      if (targetCommentId && !window.hasScrolledToComment) {
        window.hasScrolledToComment = true;
        setTimeout(() => {
          const commentEl = document.getElementById(`comment-content-${targetCommentId}`);
          if (commentEl) {
            commentEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
            const parentBox = commentEl.parentElement;
            if (parentBox) {
              parentBox.style.transition = 'background-color 1.5s ease-in-out';
              parentBox.style.backgroundColor = '#e0f2fe';
              setTimeout(() => {
                parentBox.style.backgroundColor = '#f8f9fa';
              }, 3000);
            }
          }
        }, 500);
      }
    }

    window.toggleReplyForm = function (id) {
      const form = document.getElementById(`reply-form-${id}`);
      if (form) form.style.display = form.style.display === 'none' ? 'block' : 'none';
    };

    window.openReplyBox = function (rootId, mentionName) {
      const form = document.getElementById(`reply-form-${rootId}`);
      const input = document.getElementById(`reply-input-${rootId}`);
      if (form && input) {
        form.style.display = 'block';
        if (mentionName && !input.value.includes(`@${mentionName}`)) {
          input.value = `@${mentionName} ${input.value}`;
        }
        // Put cursor at the end
        input.focus();
        const val = input.value;
        input.value = '';
        input.value = val;
      }
    };

    window.toggleEditForm = function (id) {
      const form = document.getElementById(`edit-form-${id}`);
      const contentDiv = document.getElementById(`comment-content-${id}`);
      const input = document.getElementById(`edit-input-${id}`);

      if (form && contentDiv && input) {
        if (form.style.display === 'none') {
          form.style.display = 'block';
          contentDiv.style.display = 'none';
          input.value = contentDiv.textContent;
        } else {
          form.style.display = 'none';
          contentDiv.style.display = 'block';
        }
      }
    };

    window.submitRootComment = async function () {
      const input = document.getElementById('rootCommentInput');
      const content = input.value.trim();
      if (!content) return;

      const btn = document.getElementById('rootCommentBtn');
      btn.disabled = true;
      btn.textContent = 'Sending...';

      try {
        const res = await fetch(`/api/nodes/${nodeId}/discussions`, {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({ content })
        });
        if (!res.ok) throw new Error(await readError(res));
        await loadComments();
      } catch (err) {
        alert(err.message || 'Could not post comment');
      } finally {
        if (btn) {
          btn.disabled = false;
          btn.textContent = 'Send Comment';
        }
      }
    };

    window.submitReply = async function (parentId) {
      const input = document.getElementById(`reply-input-${parentId}`);
      const content = input?.value.trim();
      if (!content) return;

      try {
        const res = await fetch(`/api/nodes/${nodeId}/discussions`, {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({ content, parentId })
        });
        if (!res.ok) throw new Error(await readError(res));
        await loadComments();
      } catch (err) {
        alert(err.message || 'Could not post reply');
      }
    };

    window.submitEdit = async function (commentId) {
      const input = document.getElementById(`edit-input-${commentId}`);
      const content = input?.value.trim();
      if (!content) return;

      try {
        const res = await fetch(`/api/comments/${commentId}?userId=${userId}`, {
          method: 'PUT',
          headers: authHeaders(),
          body: JSON.stringify({ content })
        });
        if (!res.ok) throw new Error(await readError(res));
        await loadComments();
      } catch (err) {
        alert(err.message || 'Could not update comment');
      }
    };

    window.deleteComment = async function (commentId) {
      if (!confirm('Are you sure you want to delete this comment?')) return;
      try {
        const res = await fetch(`/api/nodes/${nodeId}/discussions/${commentId}`, {
          method: 'DELETE',
          headers: authHeaders(false)
        });
        if (!res.ok) throw new Error(await readError(res));
        await loadComments();
      } catch (err) {
        alert(err.message || 'Could not delete comment');
      }
    };

    function renderError(title, message) {
      pageRoot.className = 'error-state';
      pageRoot.innerHTML = `
      <div>
        <i class="ti ti-alert-circle"></i>
        <h1>${escapeHtml(title)}</h1>
        <p class="mb-4">${escapeHtml(message)}</p>
        <a class="back-link" href="/student/learning-spaces"><i class="ti ti-arrow-left"></i> Back to Roadmap</a>
      </div>`;
    }

    // Hàm hiển thị giao diện Practice Node
    function renderNodePage(node, items) {
        const shell = document.querySelector('.lesson-shell');
        if (shell) {
            shell.style.maxWidth = 'none';
            shell.style.width = '100%';
            shell.style.padding = '0';
            shell.style.margin = '0';
        }
        const topbar = document.querySelector('.lesson-topbar');
        if (topbar) {
            topbar.style.maxWidth = 'none';
            topbar.style.width = '100%';
        }
        document.body.style.overflow = 'hidden';

        pageRoot.className = 'lesson-layout practice-layout';
        pageRoot.style.padding = '0';
        pageRoot.style.gap = '0';
        pageRoot.style.background = '#f8fafc';

        setTimeout(() => {
          initMonaco(node.starterCode);
        }, 100);

        pageRoot.innerHTML = `
        <style>
          /* Clean, light-themed LeetCode style CSS */
          .practice-layout { grid-template-columns: minmax(0, 1.2fr) minmax(0, 1.8fr) !important; align-items: stretch !important; height: calc(100vh - 65px); gap: 8px !important; padding: 8px !important; background: #f0f0f0 !important; }
          
          /* Left Pane */
          .lc-left-pane { background: #fff; border-radius: 8px; display: flex; flex-direction: column; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
          .lc-tabs-header { display: flex; gap: 16px; padding: 0 16px; border-bottom: 1px solid #f0f0f0; background: #fafafa; }
          .lc-tab { padding: 12px 4px; font-size: 0.85rem; font-weight: 500; color: #5c5c5c; cursor: pointer; border-bottom: 2px solid transparent; }
          .lc-tab.active { color: #2cbb5d; border-bottom-color: #2cbb5d; }
          .lc-tab i { margin-right: 6px; font-size: 1rem; vertical-align: middle; }
          .lc-content { padding: 20px; overflow-y: auto; flex: 1; font-size: 0.95rem; color: #262626; line-height: 1.6; }
          .lc-content h1 { font-size: 1.4rem; font-weight: 600; margin-bottom: 16px; color: #262626; }
          
          /* Markdown styling similar to Leetcode */
          .lc-content pre { background: #f7f9fa !important; border: 1px solid #e5e7eb !important; border-radius: 6px; padding: 12px; font-family: 'JetBrains Mono', monospace; font-size: 0.85rem; margin-bottom: 16px; white-space: pre-wrap; color: #262626 !important; }
          .lc-content code { background: #f7f9fa; padding: 2px 6px; border-radius: 4px; color: #262626; font-size: 0.85rem; }
          
          /* Right Pane Wrapper */
          .lc-right-pane { display: flex; flex-direction: column; gap: 8px; min-height: 0; overflow: hidden; }
          
          /* Editor Top Bar */
          .lc-editor-card { background: #fff; border-radius: 8px; display: flex; flex-direction: column; overflow: hidden; flex: 1; min-height: 0; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
          .lc-toolbar { display: flex; justify-content: space-between; align-items: center; padding: 6px 12px; background: #fafafa; border-bottom: 1px solid #f0f0f0; position: relative; }
          .lc-toolbar-left { display: flex; align-items: center; gap: 12px; }
          .lc-lang-select { background: #f2f3f4; border: none; padding: 4px 10px; border-radius: 4px; font-size: 0.8rem; font-weight: 500; color: #262626; cursor: pointer; }
          
          /* Top Central Run/Submit buttons */
          .lc-toolbar-center { display: flex; align-items: center; gap: 8px; position: absolute; left: 50%; transform: translateX(-50%); }
          .btn-lc-run { background: #f2f3f4; color: #3c3c3c; border: none; padding: 5px 16px; border-radius: 6px; font-size: 0.85rem; font-weight: 500; display: flex; align-items: center; gap: 6px; transition: 0.2s; }
          .btn-lc-run:hover { background: #e5e7eb; }
          .btn-lc-submit { background: #2cbb5d; color: #fff; border: none; padding: 5px 16px; border-radius: 6px; font-size: 0.85rem; font-weight: 500; display: flex; align-items: center; gap: 6px; transition: 0.2s; }
          .btn-lc-submit:hover { background: #23994c; }
          
          /* Terminal Bottom Bar */
          .lc-terminal-card { background: #fff; border-radius: 8px; display: flex; flex-direction: column; height: 35vh; min-height: 250px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); transition: height 0.3s ease, min-height 0.3s ease; }
          .lc-terminal-card.collapsed { height: 40px !important; min-height: 40px !important; }
          .lc-term-tabs { display: flex; padding: 0 16px; background: #fafafa; border-bottom: 1px solid #f0f0f0; justify-content: space-between; align-items: center; }
          .lc-term-tab { padding: 8px 12px; font-size: 0.8rem; font-weight: 500; color: #5c5c5c; border: none; background: transparent; cursor: pointer; border-bottom: 2px solid transparent; }
          .lc-term-tab.active { color: #262626; border-bottom-color: #262626; }
          .lc-term-toggle { background: transparent; border: none; color: #5c5c5c; cursor: pointer; padding: 4px; border-radius: 4px; transition: 0.2s; }
          .lc-term-toggle:hover { background: #e5e7eb; color: #262626; }
          
          /* Input/Output areas */
          .lc-io-box { flex: 1; display: flex; flex-direction: column; padding: 12px; }
          .lc-io-label { font-size: 0.8rem; font-weight: 600; color: #5c5c5c; margin-bottom: 6px; }
          .lc-io-input { flex: 1; background: #f7f9fa; border: 1px solid #e5e7eb; border-radius: 6px; padding: 12px; font-family: 'JetBrains Mono', monospace; font-size: 0.85rem; color: #262626; outline: none; resize: none; }
          .lc-io-output { flex: 1; background: #f7f9fa; border: 1px solid #e5e7eb; border-radius: 6px; padding: 12px; font-family: 'JetBrains Mono', monospace; font-size: 0.85rem; color: #262626; overflow-y: auto; margin: 0; white-space: pre-wrap; }
        </style>
        
        <!-- Cột Trái: Đề Bài (Problem Description) -->
        <section class="lc-left-pane">
          <div class="lc-tabs-header">
            <div class="lc-tab active"><i class="ti ti-file-description" style="color: #007bff;"></i> Description</div>
            <div class="lc-tab"><i class="ti ti-bulb"></i> Editorial</div>
            <div class="lc-tab"><i class="ti ti-flask"></i> Solutions</div>
            <div class="lc-tab"><i class="ti ti-history"></i> Submissions</div>
          </div>
          <div class="lc-content">
            <h1>${escapeHtml(node.title || 'Practice Problem')}</h1>
            ${node.content ? node.content : '<p class="text-muted">No specific problem description provided.</p>'}
          </div>
        </section>

        <!-- Cột Phải: Online IDE & Terminal -->
        <aside class="lc-right-pane">
          
          <!-- Code Editor Area -->
          <div class="lc-editor-card">
            <!-- Toolbar -->
            <div class="lc-toolbar">
                <div class="lc-toolbar-left">
                    <div style="display:flex;align-items:center;gap:6px;color:#2cbb5d;font-size:0.85rem;font-weight:600;">
                        <i class="ti ti-code"></i> Code
                    </div>
                    <select class="lc-lang-select">
                        <option>Java</option>
                    </select>
                    <span style="font-size: 0.8rem; color: #8c8c8c; margin-left: 8px;">Auto</span>
                </div>
                
                <div class="lc-toolbar-center">
                    <button id="btnRunCode" class="btn-lc-run">
                        <i class="ti ti-player-play-filled"></i> Run
                    </button>
                    <button id="btnSubmitCode" class="btn-lc-submit">
                        <i class="ti ti-cloud-upload"></i> Submit
                    </button>
                </div>
                
                <div style="display:flex; gap: 8px; color: #8c8c8c; cursor:pointer;">
                    <i class="ti ti-settings"></i>
                    <i class="ti ti-maximize"></i>
                </div>
            </div>
            
            <div id="editor" style="flex: 1; width: 100%; min-height: 300px;"></div>
          </div>
          
          <!-- Terminal & Test Results -->
          <div class="lc-terminal-card" id="terminalCard">
             <div class="lc-term-tabs" role="tablist">
                <div style="display: flex; gap: 16px;">
                    <button class="lc-term-tab active" id="console-tab" data-bs-toggle="tab" data-bs-target="#console-pane" type="button" role="tab">
                        <i class="ti ti-square-check text-success"></i> Testcase
                    </button>
                    <button class="lc-term-tab" id="test-result-tab" data-bs-toggle="tab" data-bs-target="#test-result-pane" type="button" role="tab">
                        <i class="ti ti-terminal text-primary"></i> Test Result
                    </button>
                </div>
                <button class="lc-term-toggle" id="btnToggleTerminal" title="Toggle Terminal">
                    <i class="ti ti-chevron-down" id="terminalToggleIcon" style="font-size: 1.1rem;"></i>
                </button>
             </div>
             
             <div class="tab-content" id="terminalContent" style="flex: 1; overflow: hidden; display: flex;">
                <!-- CONSOLE PANE -->
                <div class="tab-pane fade show active" id="console-pane" role="tabpanel" style="flex: 1; padding: 0;">
                    <div style="display: flex; height: 100%;">
                        <div class="lc-io-box">
                            <div class="lc-io-label">Custom Input (stdin)</div>
                            <textarea id="customInput" class="lc-io-input" placeholder="Enter standard input to test..."></textarea>
                        </div>
                        <div class="lc-io-box">
                            <div class="lc-io-label">Output</div>
                            <pre id="terminalOutput" class="lc-io-output">No commands run yet.</pre>
                        </div>
                    </div>
                </div>
                
                <!-- TEST RESULTS PANE -->
                <div class="tab-pane fade" id="test-result-pane" role="tabpanel" style="flex: 1; overflow-y: auto;">
                    <div id="testResultContainer" style="padding: 20px;">
                        <div class="text-center" style="color: #8c8c8c; padding-top: 40px;">
                            <i class="ti ti-cloud-upload" style="font-size: 2.5rem; opacity: 0.5;"></i>
                            <p class="mt-2">Run or Submit code to see results</p>
                        </div>
                    </div>
                </div>
             </div>
          </div>
        </aside>
      `;
      // --- START: INIT MONACO EDITOR ---
      require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.45.0/min/vs' } });

      function initMonaco(lecturerStarterCode) {
        require(['vs/editor/editor.main'], function () {
          const editorDiv = document.getElementById('editor');
          if (editorDiv) {
            const storageKey = 'saved_code_node_' + nodeId;
            const savedCode = localStorage.getItem(storageKey);

            // Code ưu tiên: Code SV đang gõ dang dở -> Code mẫu giảng viên -> Code mặc định Hello World
            const fallbackCode = "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}";
            const defaultCode = lecturerStarterCode || fallbackCode;

            myCodeEditor = monaco.editor.create(editorDiv, {
              value: savedCode !== null ? savedCode : defaultCode,
              language: "java",
              theme: "vs",
              automaticLayout: true,
              fontSize: 14,
              minimap: { enabled: false }
            });

            // Auto-save
            myCodeEditor.onDidChangeModelContent(function () {
              localStorage.setItem(storageKey, myCodeEditor.getValue());
            });

            // Backup save
            window.addEventListener('beforeunload', function () {
              if (typeof myCodeEditor !== 'undefined') {
                localStorage.setItem(storageKey, myCodeEditor.getValue());
              }
            });
          }
        });

        const btnRunCode = document.getElementById('btnRunCode');
        const btnSubmitCode = document.getElementById('btnSubmitCode');
        const consoleTab = new bootstrap.Tab(document.getElementById('console-tab'));
        const testResultTab = new bootstrap.Tab(document.getElementById('test-result-tab'));

        const terminalCard = document.getElementById('terminalCard');
        const btnToggleTerminal = document.getElementById('btnToggleTerminal');
        const terminalToggleIcon = document.getElementById('terminalToggleIcon');

        if (btnToggleTerminal) {
            btnToggleTerminal.addEventListener('click', function() {
                terminalCard.classList.toggle('collapsed');
                if (terminalCard.classList.contains('collapsed')) {
                    terminalToggleIcon.classList.replace('ti-chevron-down', 'ti-chevron-up');
                } else {
                    terminalToggleIcon.classList.replace('ti-chevron-up', 'ti-chevron-down');
                }
                // Notify Monaco Editor to resize
                setTimeout(() => { if (myCodeEditor) myCodeEditor.layout(); }, 350);
            });
        }

        function expandTerminal() {
            if (terminalCard && terminalCard.classList.contains('collapsed')) {
                terminalCard.classList.remove('collapsed');
                terminalToggleIcon.classList.replace('ti-chevron-up', 'ti-chevron-down');
                setTimeout(() => { if (myCodeEditor) myCodeEditor.layout(); }, 350);
            }
        }

        if (btnRunCode) {
          btnRunCode.addEventListener('click', function () {
            expandTerminal();
            consoleTab.show();
            const terminal = document.getElementById('terminalOutput');
            const customInput = document.getElementById('customInput').value;
            terminal.innerText = "> Compiling code. Please wait for the server...";
            btnRunCode.disabled = true;

            if (typeof myCodeEditor !== 'undefined') {
              const codeString = myCodeEditor.getValue();

              fetch('/api/compiler/execute', {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify({ script: codeString, stdin: customInput })
              })
                .then(res => res.json())
                .then(result => {
                  if (result.output) {
                    terminal.innerText = "> " + result.output;
                  } else if (result.error) {
                    terminal.innerText = "> Server Compiler Error: " + result.error;
                  } else {
                    terminal.innerText = "> " + JSON.stringify(result);
                  }
                })
                .catch(err => {
                  terminal.innerText = "> Network error or connection lost: " + err.message;
                })
                .finally(() => btnRunCode.disabled = false);
            }
          });
        }

        if (btnSubmitCode) {
            btnSubmitCode.addEventListener('click', function() {
                expandTerminal();
                testResultTab.show();
                const container = document.getElementById('testResultContainer');
                container.innerHTML = `
                    <div class="text-center text-muted" style="padding-top: 40px;">
                        <i class="ti ti-loader-2 ti-spin" style="font-size: 2rem; color: #3b82f6;"></i>
                        <p class="mt-2 text-white">Submitting code for grading...</p>
                    </div>
                `;
                btnSubmitCode.disabled = true;

                if (typeof myCodeEditor !== 'undefined') {
                    const codeString = myCodeEditor.getValue();

                    fetch(`/api/learning-nodes/${nodeId}/submit-code`, {
                        method: 'POST',
                        headers: authHeaders(),
                        body: JSON.stringify({ code: codeString })
                    })
                    .then(async res => {
                        if (!res.ok) throw new Error(await readError(res));
                        return res.json();
                    })
                    .then(result => {
                        const scoreColor = result.allPassed ? '#10b981' : (result.passedCount > 0 ? '#f59e0b' : '#ef4444');
                        const statusText = result.allPassed ? 'Accepted' : 'Wrong Answer';
                        
                        let html = `
                            <div style="margin-bottom: 16px;">
                                <h3 style="color: ${scoreColor}; font-weight: bold; margin: 0;">${statusText}</h3>
                                <div style="color: #aaa; font-size: 0.9rem; margin-top: 4px;">
                                    Score: <span style="color: white; font-weight: bold;">${result.totalScore}/${result.maxScore}</span> | 
                                    Passed: <span style="color: white; font-weight: bold;">${result.passedCount}/${result.totalCases} Test Cases</span>
                                </div>
                            </div>
                        `;

                        if (result.results && result.results.length > 0) {
                            html += `<div style="display: flex; flex-direction: column; gap: 12px;">`;
                            result.results.forEach((tc, index) => {
                                const passColor = tc.passed ? '#10b981' : '#ef4444';
                                const passIcon = tc.passed ? 'ti-check' : 'ti-x';
                                
                                html += `
                                <div style="background: #2d2d2d; border-radius: 8px; border: 1px solid #444; overflow: hidden;">
                                    <div style="padding: 8px 16px; background: #333; display: flex; justify-content: space-between; align-items: center;">
                                        <div style="font-weight: 600; color: #fff; font-size: 0.85rem; display: flex; align-items: center; gap: 8px;">
                                            <i class="ti ${passIcon}" style="color: ${passColor};"></i> Test Case #${index + 1}
                                        </div>
                                        <span class="badge" style="background: #444;">${tc.points} pts</span>
                                    </div>
                                    <div style="padding: 12px;">
                                `;

                                if (tc.hidden) {
                                    html += `
                                        <div style="color: #94a3b8; font-size: 0.85rem; font-style: italic; display: flex; align-items: center; gap: 6px;">
                                            <i class="ti ti-lock"></i> Hidden Test Case. ${tc.passed ? 'Passed.' : 'Failed.'}
                                        </div>
                                    `;
                                } else {
                                    html += `
                                        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 8px;">
                                            <div>
                                                <label style="color: #aaa; font-size: 0.7rem; font-family: 'JetBrains Mono', monospace; display: block; margin-bottom: 2px;">Input</label>
                                                <div style="background: #1a1a1a; padding: 6px; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 0.75rem; color: #e2e8f0; white-space: pre-wrap; word-break: break-all;">${escapeHtml(tc.inputData)}</div>
                                            </div>
                                            <div>
                                                <label style="color: #aaa; font-size: 0.7rem; font-family: 'JetBrains Mono', monospace; display: block; margin-bottom: 2px;">Expected</label>
                                                <div style="background: #1a1a1a; padding: 6px; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 0.75rem; color: #e2e8f0; white-space: pre-wrap; word-break: break-all;">${escapeHtml(tc.expectedOutput)}</div>
                                            </div>
                                            <div>
                                                <label style="color: #aaa; font-size: 0.7rem; font-family: 'JetBrains Mono', monospace; display: block; margin-bottom: 2px;">Actual</label>
                                                <div style="background: #1a1a1a; padding: 6px; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 0.75rem; color: ${passColor}; white-space: pre-wrap; word-break: break-all;">${escapeHtml(tc.actualOutput)}</div>
                                            </div>
                                        </div>
                                    `;
                                }

                                html += `</div></div>`;
                            });
                            html += `</div>`;
                        }

                        container.innerHTML = html;
                    })
                    .catch(err => {
                        container.innerHTML = `
                            <div class="text-center text-danger" style="padding-top: 40px;">
                                <i class="ti ti-alert-circle" style="font-size: 2rem;"></i>
                                <p class="mt-2">${err.message}</p>
                            </div>
                        `;
                    })
                    .finally(() => btnSubmitCode.disabled = false);
                }
            });
        }
      } // Đóng function initMonaco
      // --- END: INIT MONACO EDITOR ---

      // --- START: HANDLE SUMMARY SUBMIT ---
      const summaryForm = document.getElementById('summaryForm');
      const saveSummaryBtn = document.getElementById('saveSummaryBtn');
      const summaryStatus = document.getElementById('summaryStatus');

      if (summaryForm && saveSummaryBtn && summaryStatus) {
        summaryForm.addEventListener('submit', async (e) => {
          e.preventDefault();

          const keyTakeaways = document.getElementById('keyTakeaways').value;
          const questions = document.getElementById('questions').value;

          const summaryJson = JSON.stringify({
            keyTakeaways: keyTakeaways,
            confusingPoints: questions
          });

          // Loading effect
          saveSummaryBtn.disabled = true;
          saveSummaryBtn.innerHTML = '<i class="ti ti-loader-2 ti-spin"></i> Saving...';

          try {
            const response = await fetch('/api/lesson-summaries', {
              method: 'POST',
              headers: authHeaders(true),
              body: JSON.stringify({
                learningNodeId: nodeId,
                summaryContent: summaryJson
              })
            });

            if (!response.ok) {
              throw new Error(await readError(response));
            }

            // Show success state
            summaryStatus.classList.add('show');
            setTimeout(() => summaryStatus.classList.remove('show'), 3500);
          } catch (err) {
            alert('Failed to save summary: ' + err.message);
          } finally {
            saveSummaryBtn.disabled = false;
            saveSummaryBtn.innerHTML = '<i class="ti ti-device-floppy"></i> Save Summary';
          }
        });
      }
      // --- END: HANDLE SUMMARY SUBMIT ---
    }

    // Hàm chính: Tải thông tin của bài học (node) và các tài liệu/item liên quan từ backend
    async function loadNodePage() {
      if (!nodeId) {
        renderError('Missing nodeId', 'Could not find the node to open. Please return to the roadmap and select a node again.');
        return;
      }

      try {
        // Gọi đồng thời 2 API: lấy chi tiết node và lấy các item (tài liệu/video) thuộc node đó
        const [nodeResponse, itemsResponse] = await Promise.all([
          fetch(`/api/learning-nodes/${encodeURIComponent(nodeId)}`, { headers: authHeaders(false) }),
          fetch(`/api/learning-nodes/${encodeURIComponent(nodeId)}/items`, { headers: authHeaders(false) })
        ]);

        if (!nodeResponse.ok) {
          throw new Error(await readError(nodeResponse));
        }

        const node = await nodeResponse.json();
        const items = itemsResponse.ok ? await itemsResponse.json() : [];
        // Sắp xếp các tài liệu theo thứ tự (position)
        const sortedItems = Array.isArray(items)
          ? [...items].sort((a, b) => (a.position || 0) - (b.position || 0))
          : [];

        // Tự động chuyển hướng nếu node chứa bài Quiz
        const quizItem = sortedItems.find(i => String(i.itemType || '').toUpperCase() === 'QUIZ');
        if (quizItem && quizItem.quizId) {
          window.location.replace(`/student/take-quiz?quizId=${encodeURIComponent(quizItem.quizId)}`);
          return;
        }

        // Tự động chuyển hướng nếu node là dạng Reading/Document (chỉ có tài liệu đọc)
        const hasVideo = sortedItems.some(i => String(i.itemType || '').toUpperCase() === 'VIDEO');
        if (!hasVideo) {
          const docItem = sortedItems.find(i => ['PDF', 'DOC', 'DOCUMENT'].includes(String(i.itemType || '').toUpperCase()) && (i.fullUrl || i.url));
          if (docItem) {
            window.location.replace(docItem.fullUrl || docItem.url);
            return;
          }
        }

        // Hiển thị nội dung ra giao diện
        renderNodePage(node, sortedItems);
        loadComments();
      } catch (error) {
        renderError('Could not load node', error.message || 'Please try again later.');
      }
    }

    loadNodePage();

  
