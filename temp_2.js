
    let myCodeEditor; // Biáº¿n toÃ n cá»¥c Ä‘á»ƒ lÆ°u instance cá»§a editor
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

    // HÃ m há»— trá»£: Chuyá»ƒn Ä‘á»•i link video YouTube thÆ°á»ng thÃ nh dáº¡ng link nhÃºng (embed) Ä‘á»ƒ cÃ³ thá»ƒ xem trá»±c tiáº¿p trÃªn web
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
        const groupParam = window.currentGroupId ? `?groupId=${window.currentGroupId}` : '';
        const response = await fetch(`/api/nodes/${nodeId}/discussions${groupParam}`, { headers: authHeaders(false) });
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
      const heroBadge = document.getElementById('heroCommentsCount');
      if (!container) return;

      let totalCount = currentComments.length;
      currentComments.forEach(c => totalCount += (c.replies ? c.replies.length : 0));
      if (badge) badge.textContent = totalCount;
      if (heroBadge) heroBadge.textContent = totalCount;

      let body = '';
      if (currentComments.length === 0) {
        body = emptyLightTemplate('ti-message-off', 'No comments yet. Be the first to start a discussion!');
      } else {
        body = `<div id="commentListScrollArea" class="comment-list" style="max-height: 400px; overflow-y: auto; padding-right: 8px;">
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
      } else {
        // Auto scroll to bottom
        setTimeout(() => {
          const scrollArea = document.getElementById('commentListScrollArea');
          if (scrollArea) {
            scrollArea.scrollTop = scrollArea.scrollHeight;
          }
        }, 100);
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
        const groupParam = window.currentGroupId ? `?groupId=${window.currentGroupId}` : '';
        const res = await fetch(`/api/nodes/${nodeId}/discussions${groupParam}`, {
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
        const groupParam = window.currentGroupId ? `?groupId=${window.currentGroupId}` : '';
        const res = await fetch(`/api/nodes/${nodeId}/discussions${groupParam}`, {
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
        const groupParam = window.currentGroupId ? `?groupId=${window.currentGroupId}` : '';
        const res = await fetch(`/api/nodes/${nodeId}/discussions/${commentId}${groupParam}`, {
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

    // HÃ m hiá»ƒn thá»‹ giao diá»‡n toÃ n bá»™ ná»™i dung cá»§a bÃ i há»c (node)
    function renderNodePage(node, items) {
      // PhÃ¢n loáº¡i cÃ¡c tÃ i liá»‡u Ä‘Ã­nh kÃ¨m (items) thÃ nh: tÃ i nguyÃªn (tÃ i liá»‡u/code), bÃ¬nh luáº­n, vÃ  video
      const resources = items.filter((item) => !['VIDEO', 'DISCUSSION'].includes(normalizeItemType(item.itemType)));
      const discussions = items.filter((item) => normalizeItemType(item.itemType) === 'DISCUSSION');
      const videoCount = items.some((item) => normalizeItemType(item.itemType) === 'VIDEO' && itemUrl(item)) ? 1 : 0;

      if (node.nodeType === 'PRACTICE') {
        window.location.replace('/student/learning-nodes/' + encodeURIComponent(nodeId) + '/practice');
        return;
      }
        // Giao diá»‡n BÃ i há»c LÃ½ thuyáº¿t / Video bÃ¬nh thÆ°á»ng
        pageRoot.className = 'lesson-layout';
        pageRoot.innerHTML = `
        <section class="player-column">
          <section class="node-hero">
            <span class="eyebrow"><i class="ti ti-player-play"></i> Learning Spaces node</span>
            <h1 class="node-title">${escapeHtml(node.title || 'Learning Node')}</h1>
            <p class="node-description" style="white-space: pre-wrap;">${escapeHtml(node.content || 'Your instructor has not added a description for this node yet.')}</p>
            <div class="meta-row">
              <span class="soft-chip"><i class="ti ti-layers-subtract"></i>${escapeHtml(nodeTypeLabel(node.nodeType))}</span>
              <span class="soft-chip"><i class="ti ti-lock-open"></i>${escapeHtml(statusLabel(node.status))}</span>
              <span class="soft-chip"><i class="ti ti-video"></i>${videoCount ? 'Video available' : 'No video'}</span>
              <span class="soft-chip"><i class="ti ti-messages"></i><span id="heroCommentsCount">0</span> comments</span>
            </div>
          </section>

          <section class="video-shell">
            ${videoTemplate(node, items)}
            <div class="player-controls">
              <button class="server-pill active" type="button">Lesson Video</button>
              <button class="server-pill" type="button">${resources.length} resource</button>
              <button class="server-pill" type="button">${discussions.length} comments</button>
            </div>
          </section>

          <section class="content-card">
            <h2>Node Content</h2>
            <p>${escapeHtml(node.content || 'Detailed content will be updated by your instructor later.')}</p>
          </section>

          <!-- START: LESSON SUMMARY UI -->
          <section class="summary-card" style="margin-top: 1.5rem;">
            <div class="summary-card-header">
              <h2 class="summary-title">
                <i class="ti ti-notebook"></i> Lesson Summary
              </h2>
              <span id="summaryStatus" class="summary-status">
                <i class="ti ti-check"></i> Saved successfully
              </span>
            </div>

            <form id="summaryForm" class="summary-form">
              <div>
                <label for="keyTakeaways" class="summary-label">ðŸ“š Key takeaways from the lesson</label>
                <textarea id="keyTakeaways" class="summary-textarea" placeholder="Record the important knowledge you learned from the lesson..." required></textarea>
              </div>

              <div>
                <label for="questions" class="summary-label">â“ Concepts you didn't understand</label>
                <textarea id="questions" class="summary-textarea" placeholder="Record any content you are still wondering about or didn't understand..."></textarea>
              </div>

              <div style="display: flex; justify-content: flex-end; margin-top: 0.5rem;">
                <button type="submit" id="saveSummaryBtn" class="btn-primary">
                  <i class="ti ti-device-floppy"></i> Save Summary
                </button>
              </div>
            </form>
          </section>
          <!-- END: LESSON SUMMARY UI -->

          ${node.quizzes && node.quizzes.length > 0 ? `
          <!-- START: TAKE QUIZ UI -->
          <section class="content-card mt-3" style="text-align: center; background: #f8fafc; border: 1px dashed #cbd5e1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 1.25rem; border-radius: 12px;">
            <i class="ti ti-brain" style="font-size: 2rem; color: #6366f1; margin-bottom: 0.5rem;"></i>
            <h3 style="font-size: 1.1rem; font-weight: 600; color: #1e293b; margin-bottom: 0.25rem;">Knowledge Check</h3>
            <p style="color: #64748b; margin-bottom: 1rem; font-size: 0.85rem;">${node.quizzes.length > 1 ? 'There are multiple quizzes available.' : 'There is a quiz available to test your understanding of this lesson.'}</p>
            <div style="display: flex; gap: 8px; flex-wrap: wrap; justify-content: center;">
              ${node.quizzes.map((quiz, index) => `
                <a href="/student/take-quiz?quizId=${quiz.id}" class="btn-primary" style="display: inline-flex; align-items: center; gap: 6px; text-decoration: none; padding: 0.5rem 1rem; font-size: 0.9rem; border-radius: 6px;">
                  <i class="ti ti-player-play-filled"></i> ${node.quizzes.length > 1 ? escapeHtml(quiz.title || ('Quiz ' + (index + 1))) : 'Take Quiz Now'}
                </a>
              `).join('')}
            </div>
          </section>
          <!-- END: TAKE QUIZ UI -->
          ` : ''}
        </section>

        <aside class="side-column">


          <section class="side-card">
            <div class="side-card-header">
              <div>
                <h2>Comments</h2>
                <p>Discussions and questions related to this node.</p>
              </div>
              <span class="soft-chip" id="commentsCountBadge">0</span>
            </div>
            <div id="nodeCommentsContainer">
              <div class="empty-light">
                <div><i class="ti ti-loader-2"></i> Loading comments...</div>
              </div>
            </div>
          </section>
        </aside>`;
      }

      require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.45.0/min/vs' } });

      function initMonaco(lecturerStarterCode) {
        require(['vs/editor/editor.main'], function () {
          const editorDiv = document.getElementById('editor');
          if (editorDiv) {
            const storageKey = 'saved_code_node_' + nodeId;
            const savedCode = localStorage.getItem(storageKey);
            const fallbackCode = "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}";
            const defaultCode = lecturerStarterCode || fallbackCode;

            myCodeEditor = monaco.editor.create(editorDiv, {
              value: savedCode !== null ? savedCode : defaultCode,
              language: "java",
              theme: "vs-dark",
              automaticLayout: true,
              fontSize: 14,
              minimap: { enabled: false }
            });

            myCodeEditor.onDidChangeModelContent(function () {
              localStorage.setItem(storageKey, myCodeEditor.getValue());
            });

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

        if (btnRunCode) {
          btnRunCode.addEventListener('click', function () {
            consoleTab.show();
            const terminal = document.getElementById('terminalOutput');
            const customInput = document.getElementById('customInput').value;
            terminal.innerText = "> Äang biÃªn dá»‹ch code. Vui lÃ²ng Ä‘á»£i Backend API...";
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
                    terminal.innerText = "> Lá»—i Server Compiler: " + result.error;
                  } else {
                    terminal.innerText = "> " + JSON.stringify(result);
                  }
                })
                .catch(err => {
                  terminal.innerText = "> Lá»—i máº¡ng hoáº·c máº¥t káº¿t ná»‘i Backend: " + err.message;
                })
                .finally(() => btnRunCode.disabled = false);
            }
          });
        }

        if (btnSubmitCode) {
            btnSubmitCode.addEventListener('click', function() {
                testResultTab.show();
                const container = document.getElementById('testResultContainer');
                container.innerHTML = `
                    <div class="text-center text-muted" style="padding-top: 40px;">
                        <i class="ti ti-loader-2 ti-spin" style="font-size: 2rem; color: #3b82f6;"></i>
                        <p class="mt-2 text-white">Äang gá»­i code lÃªn Server cháº¥m Ä‘iá»ƒm...</p>
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
                                    Äiá»ƒm: <span style="color: white; font-weight: bold;">${result.totalScore}/${result.maxScore}</span> |
                                    VÆ°á»£t qua: <span style="color: white; font-weight: bold;">${result.passedCount}/${result.totalCases} Test Cases</span>
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
                                            <i class="ti ti-lock"></i> Hidden Test Case. ${tc.passed ? 'Báº¡n Ä‘Ã£ vÆ°á»£t qua.' : 'Báº¡n chÆ°a vÆ°á»£t qua test nÃ y.'}
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
      }

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

    // HÃ m chÃ­nh: Táº£i thÃ´ng tin cá»§a bÃ i há»c (node) vÃ  cÃ¡c tÃ i liá»‡u/item liÃªn quan tá»« backend
    async function loadNodePage() {
      if (!nodeId) {
        renderError('Missing nodeId', 'Could not find the node to open. Please return to the roadmap and select a node again.');
        return;
      }

      try {
        // Gá»i Ä‘á»“ng thá»i 2 API: láº¥y chi tiáº¿t node vÃ  láº¥y cÃ¡c item (tÃ i liá»‡u/video) thuá»™c node Ä‘Ã³
        const [nodeResponse, itemsResponse] = await Promise.all([
          fetch(`/api/learning-nodes/${encodeURIComponent(nodeId)}`, { headers: authHeaders(false) }),
          fetch(`/api/learning-nodes/${encodeURIComponent(nodeId)}/items`, { headers: authHeaders(false) })
        ]);

        if (!nodeResponse.ok) {
          throw new Error(await readError(nodeResponse));
        }

        const node = await nodeResponse.json();
        const items = itemsResponse.ok ? await itemsResponse.json() : [];
        // Sáº¯p xáº¿p cÃ¡c tÃ i liá»‡u theo thá»© tá»± (position)
        const sortedItems = Array.isArray(items)
          ? [...items].sort((a, b) => (a.position || 0) - (b.position || 0))
          : [];

        // Tá»± Ä‘á»™ng chuyá»ƒn hÆ°á»›ng náº¿u node chá»©a bÃ i Quiz
        const quizItem = sortedItems.find(i => String(i.itemType || '').toUpperCase() === 'QUIZ');
        if (quizItem && quizItem.quizId && node.nodeType !== 'GROUP_ACTIVITY') {
          window.location.replace(`/student/take-quiz?quizId=${encodeURIComponent(quizItem.quizId)}`);
          return;
        }

        // Tá»± Ä‘á»™ng chuyá»ƒn hÆ°á»›ng náº¿u node lÃ  dáº¡ng Reading/Document (chá»‰ cÃ³ tÃ i liá»‡u Ä‘á»c)
        const hasVideo = sortedItems.some(i => String(i.itemType || '').toUpperCase() === 'VIDEO');
        if (!hasVideo) {
          const docItem = sortedItems.find(i => ['PDF', 'DOC', 'DOCUMENT'].includes(String(i.itemType || '').toUpperCase()) && (i.fullUrl || i.url));
          if (docItem) {
            window.location.replace(docItem.fullUrl || docItem.url);
            return;
          }
        }

        // Hiá»ƒn thá»‹ ná»™i dung ra giao diá»‡n
        if (node.nodeType === 'GROUP_ACTIVITY') {
            if (typeof window.renderGroupActivityPage === 'function') {
                window.renderGroupActivityPage(node, nodeId);
            } else {
                renderError('Lá»—i', 'Module Group Activity chÆ°a Ä‘Æ°á»£c táº£i.');
            }
        } else {
            renderNodePage(node, sortedItems);
            loadComments();
        }
      } catch (error) {
        renderError('Could not load node', error.message || 'Please try again later.');
      }
    }

    loadNodePage();

    let stompClient = null;

    function shouldPauseWebSocketUpdate() {
      const rootInput = document.getElementById('rootCommentInput');
      if (rootInput && (rootInput.value.trim().length > 0)) return true;

      const replyInputs = document.querySelectorAll('textarea[id^="reply-input-"]');
      for (let input of replyInputs) {
        if (input.value.trim().length > 0) return true;
      }
      return false;
    }

    function connectWebSocket() {
      if (!nodeId) return;
      console.log('Attempting to connect to WebSocket for Node ID:', nodeId);
      const socket = new SockJS('/ws');
      stompClient = Stomp.over(socket);
      // stompClient.debug = null; // Removed to enable debugging
      stompClient.connect({}, function (frame) {
        console.log('Connected to STOMP WebSocket:', frame);
        stompClient.subscribe('/topic/nodes/' + nodeId + '/discussions', function (message) {
          console.log('Received WebSocket message:', message.body);
          if (message.body) {
             const payload = JSON.parse(message.body);
             if (payload.action === 'UPDATE') {
                 console.log('Action is UPDATE. Should pause? ', shouldPauseWebSocketUpdate());
                 if (!shouldPauseWebSocketUpdate()) {
                     console.log('Reloading comments...');
                     loadComments();
                 }
             }
          }
        });
      }, function(error) {
        console.error('WebSocket connection error:', error);
        setTimeout(connectWebSocket, 5000);
      });
    }

    connectWebSocket();

  
