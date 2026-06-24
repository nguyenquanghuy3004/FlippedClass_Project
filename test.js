
    /* --- Inlined from student-user-menu.js --- */
    (function () {
      const defaultAvatarUrl = '/assets/images/avatar-1.jpg';

      function storedUser() {
        return {
          fullName: localStorage.getItem('full_name') || localStorage.getItem('fullName') || '',
          username: localStorage.getItem('username') || '',
          avatarUrl: localStorage.getItem('avatar_url') || localStorage.getItem('avatarUrl') || ''
        };
      }

      function normalizeUser(user = {}) {
        const stored = storedUser();
        return {
          name: user.fullName || user.name || user.username || stored.fullName || stored.username || 'Student',
          username: user.username || stored.username || '',
          avatarUrl: user.avatarUrl || user.avatar_url || stored.avatarUrl || defaultAvatarUrl
        };
      }

      function setStudentMenuUser(user = {}) {
        const normalized = normalizeUser(user);

        if (normalized.name && normalized.name !== 'Student') {
          localStorage.setItem('full_name', normalized.name);
        }
        if (normalized.username) {
          localStorage.setItem('username', normalized.username);
        }
        if (normalized.avatarUrl && normalized.avatarUrl !== defaultAvatarUrl) {
          localStorage.setItem('avatar_url', normalized.avatarUrl);
        }

        document.querySelectorAll('[data-user-name]').forEach((element) => {
          element.textContent = normalized.name;
        });
        document.querySelectorAll('[data-user-avatar]').forEach((image) => {
          image.src = normalized.avatarUrl;
          image.onerror = () => {
            image.onerror = null;
            image.src = defaultAvatarUrl;
          };
        });

        const topbarName = document.getElementById('studentName');
        if (topbarName) topbarName.textContent = normalized.name;
      }

      function studentLogout() {
        ['jwt_token', 'user_id', 'full_name', 'fullName', 'username', 'avatar_url', 'avatarUrl'].forEach((key) => {
          localStorage.removeItem(key);
        });
        window.location.href = '/';
      }

      async function refreshStudentMenuUser() {
        const token = localStorage.getItem('jwt_token');
        const userId = localStorage.getItem('user_id');
        if (!token || !userId) return;

        try {
          const response = await fetch(`/api/users/${encodeURIComponent(userId)}`, {
            headers: { Authorization: `Bearer ${token}` }
          });
          if (!response.ok) return;
          const user = await response.json();
          setStudentMenuUser(user);
        } catch (error) {
          console.warn('Could not load user menu profile.', error);
        }
      }

      function closeAllMenus(exceptMenu = null) {
        document.querySelectorAll('[data-user-menu].is-open').forEach((menu) => {
          if (menu !== exceptMenu) {
            menu.classList.remove('is-open');
            menu.querySelector('[data-user-menu-toggle]')?.setAttribute('aria-expanded', 'false');
          }
        });
      }

      function initStudentUserMenus() {
        setStudentMenuUser();
        refreshStudentMenuUser();

        document.querySelectorAll('[data-user-menu]').forEach((menu) => {
          const toggle = menu.querySelector('[data-user-menu-toggle]');
          const logoutButton = menu.querySelector('[data-user-logout]');

          toggle?.addEventListener('click', (event) => {
            event.stopPropagation();
            const shouldOpen = !menu.classList.contains('is-open');
            closeAllMenus(menu);
            menu.classList.toggle('is-open', shouldOpen);
            toggle.setAttribute('aria-expanded', String(shouldOpen));
          });

          logoutButton?.addEventListener('click', studentLogout);
        });

        document.addEventListener('click', () => closeAllMenus());
        document.addEventListener('keydown', (event) => {
          if (event.key === 'Escape') closeAllMenus();
        });
      }

      window.setStudentMenuUser = setStudentMenuUser;
      window.studentLogout = studentLogout;
      if (!window.logout) window.logout = studentLogout;

      document.addEventListener('DOMContentLoaded', initStudentUserMenus);
    })();

  
