import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const savedUser = localStorage.getItem('fc_user');
  if (savedUser) {
    const { token } = JSON.parse(savedUser);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// Initialize local mock state in localStorage for running when the server is offline or not configured yet
const getMockLearningSpaces = () => {
  const local = localStorage.getItem('mock_learning_spaces');
  if (local) return JSON.parse(local);
  const defaultSpaces = [
    {
      id: 1,
      name: "Lớp học Flipped Classroom 101",
      description: "Giới thiệu về mô hình lớp học đảo ngược cơ bản.",
      inviteCode: "ABC12345",
      visibility: "PUBLIC",
      ownerId: 2,
      ownerUsername: "instructor",
      createdAt: new Date().toISOString()
    }
  ];
  localStorage.setItem('mock_learning_spaces', JSON.stringify(defaultSpaces));
  return defaultSpaces;
};

// Mocking some API responses for the demo functionality
// In a real app, these would hit actual endpoints
export const authService = {
  login: async (username, password) => {
    try {
      const response = await api.post('/auth/signin', { username, password });
      const data = response.data;
      
      // Map backend roles to frontend roles
      let role = 'Student';
      if (data.roles && data.roles.includes('ADMIN')) role = 'Admin';
      else if (data.roles && data.roles.includes('MENTOR')) role = 'Instructor';
      else if (data.roles && data.roles.includes('STUDENT')) role = 'Student';

      const userData = {
        id: data.id,
        name: data.username,
        email: data.email,
        role: role,
        token: data.token
      };
      
      return { user: userData, token: data.token };
    } catch (err) {
      console.warn("API Error, falling back to local mock authentication strategy:", err.message);
      
      const normalizedUsername = String(username).toLowerCase();
      
      // Support matching by username or email
      if (normalizedUsername === 'instructor' || normalizedUsername === 'instructor@fc.web') {
        const userData = {
          id: 2,
          name: 'instructor',
          email: 'instructor@fc.web',
          role: 'Instructor',
          token: 'mock-jwt-token-for-instructor'
        };
        return { user: userData, token: userData.token };
      } else if (normalizedUsername === 'admin' || normalizedUsername === 'admin@flippedclass.com') {
        const userData = {
          id: 1,
          name: 'admin',
          email: 'admin@flippedclass.com',
          role: 'Admin',
          token: 'mock-jwt-token-for-admin'
        };
        return { user: userData, token: userData.token };
      } else {
        // Fallback or student account
        const userData = {
          id: 3,
          name: username || 'student',
          email: username.includes('@') ? username : 'student@fc.web',
          role: 'Student',
          token: 'mock-jwt-token-for-student'
        };
        return { user: userData, token: userData.token };
      }
    }
  },
  
  register: async (userData) => {
    try {
      const response = await api.post('/auth/signup', userData);
      return response.data;
    } catch (err) {
      console.warn("API Error, falling back to local mock signup success.");
      return { message: "User registered successfully! (Mock Mode)" };
    }
  }
};

export const roadmapService = {
  getRoadmap: async (classroomId) => {
    return {
      nodes: [
        { id: '1', type: 'learning', data: { label: 'Tuần 1: Giới thiệu Flipped Classroom', description: 'Tìm hiểu về triết lý và cách vận hành lớp học đảo ngược.' }, position: { x: 250, y: 5 } },
        { id: '2', type: 'quiz', data: { label: 'Quiz 1: Kiến thức nền tảng', description: 'Kiểm tra mức độ sẵn sàng.' }, position: { x: 250, y: 100 } },
        { id: '3', type: 'assignment', data: { label: 'Bài tập: Thiết kế cấu trúc Lesson Plan', description: 'Xây dựng bài giảng cho video Pre-class.' }, position: { x: 250, y: 200 } },
        { id: '4', type: 'milestone', data: { label: 'Project Milestone: Thành lập nhóm', description: 'Phân chia team và chọn đề tài.' }, position: { x: 250, y: 350 } },
      ],
      edges: [
        { id: 'e1-2', source: '1', target: '2' },
        { id: 'e2-3', source: '2', target: '3' },
        { id: 'e3-4', source: '3', target: '4' },
      ]
    };
  }
};

export const learningSpaceService = {
  getAll: async () => {
    try {
      const response = await api.get('/learning-spaces');
      return response.data;
    } catch (err) {
      console.warn("API Error, falling back to local mock learning spaces:", err.message);
      return getMockLearningSpaces();
    }
  },
  create: async (data) => {
    try {
      const response = await api.post('/learning-spaces', data);
      return response.data;
    } catch (err) {
      console.warn("API Error, falling back to local mock create learning space:", err.message);
      const currentSpaces = getMockLearningSpaces();
      const savedUser = localStorage.getItem('fc_user');
      const currentUser = savedUser ? JSON.parse(savedUser) : { name: 'instructor', id: 2 };

      const newSpace = {
        id: currentSpaces.length + 1,
        name: data.name,
        description: data.description || 'Chưa có mô tả.',
        inviteCode: Math.random().toString(36).substring(2, 10).toUpperCase(),
        visibility: data.visibility || 'PUBLIC',
        ownerId: currentUser.id,
        ownerUsername: currentUser.name,
        createdAt: new Date().toISOString()
      };
      currentSpaces.push(newSpace);
      localStorage.setItem('mock_learning_spaces', JSON.stringify(currentSpaces));
      return newSpace;
    }
  },
  delete: async (id) => {
    try {
      const response = await api.delete(`/learning-spaces/${id}`);
      return response.data;
    } catch (err) {
      console.warn("API Error, falling back to local mock delete learning space:", err.message);
      let currentSpaces = getMockLearningSpaces();
      currentSpaces = currentSpaces.filter(s => s.id !== id);
      localStorage.setItem('mock_learning_spaces', JSON.stringify(currentSpaces));
      return { message: "Xóa thành công (Mock Mode)" };
    }
  }
};

export default api;
