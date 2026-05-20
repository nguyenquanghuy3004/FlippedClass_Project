import React from 'react';
import { useAuth } from '../../context/AuthContext.jsx';

const StudentDashboard = () => {
  const { user } = useAuth();
  
  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <div className="bg-white p-10 rounded-2xl border border-slate-200 shadow-sm">
        <h2 className="text-3xl font-bold text-slate-900 tracking-tight">Chào mừng quay lại, {user?.name || 'Sinh viên'}</h2>
        <p className="text-slate-500 mt-2 font-medium italic">Bạn đã sẵn sàng cho buổi học hôm nay chưa?</p>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          {/* Main content placeholder */}
          <div className="bg-white p-8 rounded-3xl border border-slate-100 border-dashed flex items-center justify-center min-h-[200px]">
            <p className="text-slate-400 italic text-sm">Chưa có nội dung hiển thị</p>
          </div>
        </div>

        <div className="space-y-8">
          {/* Dashboard is currently empty */}
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
