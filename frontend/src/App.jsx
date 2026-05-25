import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext.jsx';
import DashboardLayout from './layouts/DashboardLayout.jsx';

// Pages
import Home from './pages/Home.jsx';
import Login from './pages/auth/Login.jsx';
import InstructorDashboard from './pages/instructor/InstructorDashboard.jsx';
import StudentDashboard from './pages/student/StudentDashboard.jsx';
import EvaluationPage from './pages/instructor/EvaluationPage.jsx';
import RoadmapPage from './pages/instructor/RoadmapPage.jsx';
import SubjectsPage from './pages/instructor/SubjectsPage.jsx';
import QuestionBankPage from './pages/instructor/QuestionBankPage.jsx';
import ProfilePage from './pages/ProfilePage.jsx';

const DashboardSwitch = () => {
  const { user } = useAuth();
  if (user?.role === 'Instructor') return <InstructorDashboard />;
  if (user?.role === 'Student') return <StudentDashboard />;
  if (user?.role === 'Admin') return (
    <div className="flex items-center justify-center h-full">
      <div className="text-center">
        <h2 className="text-xl font-bold text-slate-900 mb-2">Admin Dashboard</h2>
        <p className="text-slate-500 italic text-sm">Trang quản trị hiện đang được bảo trì hoặc bạn không có quyền truy cập dashboard này.</p>
      </div>
    </div>
  );
  return <Navigate to="/login" replace />;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          
          <Route element={<DashboardLayout />}>
            <Route path="/dashboard" element={<DashboardSwitch />} />
            <Route path="/subjects" element={<SubjectsPage />} />
            <Route path="/evaluation" element={<EvaluationPage />} />
            <Route path="/roadmaps" element={<RoadmapPage />} />
            <Route path="/questions" element={<QuestionBankPage />} />
            <Route path="/profile" element={<ProfilePage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
