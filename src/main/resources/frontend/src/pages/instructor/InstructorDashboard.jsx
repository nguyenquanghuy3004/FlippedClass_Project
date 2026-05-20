import React from 'react';
import { 
  Users, 
  BookOpen, 
  Calendar, 
  MessageSquare,
  ArrowRight,
  Plus,
  Clock,
  ArrowUpRight,
  TrendingUp,
  ChevronRight,
  PieChart,
  Target
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { cn } from '../../lib/utils.js';

const InstructorDashboard = () => {
  const { user } = useAuth();
  
  return (
    <div className="space-y-8 animate-in fade-in duration-500 pb-12">
      {/* Welcome Section */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-10 rounded-2xl border border-slate-200 shadow-sm">
        <div>
          <h2 className="text-3xl font-bold text-slate-900 tracking-tight">Chào buổi sáng, {user?.name || 'Giảng viên'}</h2>
          <p className="text-slate-500 mt-2 font-medium italic">Bạn có <span className="text-indigo-600 font-bold underline decoration-indigo-200 underline-offset-4">12 sinh viên</span> chờ đánh giá trong tuần này.</p>
        </div>
        <div className="flex gap-3">
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column - Stats & History */}
        <div className="lg:col-span-2 space-y-8">
          {/* Quick Metrics */}
          <div className="grid grid-cols-1 gap-6">
            <div className="bg-white p-8 rounded-2xl border border-slate-200 shadow-sm relative overflow-hidden group">
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-4 italic">Tiến độ chấm điểm (Lớp FC-01)</p>
              <div className="flex items-center gap-6">
                <div className="relative w-16 h-16">
                  <svg className="w-full h-full -rotate-90" viewBox="0 0 36 36">
                    <path className="text-slate-100" strokeWidth="4" stroke="currentColor" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <path className="text-indigo-500" strokeDasharray="75, 100" strokeWidth="4" stroke="currentColor" fill="none" strokeLinecap="round" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                  </svg>
                  <div className="absolute inset-0 flex items-center justify-center text-xs font-bold font-mono text-indigo-600">75%</div>
                </div>
                <div>
                  <h4 className="text-3xl font-bold text-slate-900">45/60</h4>
                  <p className="text-xs text-slate-500 italic">Sinh viên đã hoàn thành</p>
                </div>
              </div>
            </div>
          </div>

          {/* Evaluation Queue Shortcut */}
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-6 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
              <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider">Evaluation Queue</h3>
              <Link to="/evaluation" className="text-[10px] font-bold text-indigo-600 hover:underline underline-offset-4 flex items-center gap-1 uppercase tracking-widest">
                Xem toàn bộ <ChevronRight className="w-3 h-3" />
              </Link>
            </div>
            <div className="p-2 space-y-1">
              {[
                { name: 'Lê Minh Thành', type: 'Final Report', date: '2 ngày trước', score: 'Wait', id: '2012015' },
                { name: 'Trần Văn Dũng', type: 'Presentation Video', date: '1 ngày trước', score: 'Wait', id: '2012008' },
                { name: 'Nguyễn Thị Hoa', type: 'Self Assessment', date: 'Vừa xong', score: 'Wait', id: '2012042' },
              ].map((item, idx) => (
                <div key={idx} className="flex items-center justify-between p-4 hover:bg-slate-50 rounded-xl transition-all group cursor-pointer border border-transparent hover:border-slate-100">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-500 transition-all group-hover:bg-indigo-600 group-hover:text-white border border-slate-200">
                      {item.name.charAt(0)}
                    </div>
                    <div>
                      <p className="text-sm font-bold text-slate-900">{item.name}</p>
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-tight italic">{item.id} • {item.type}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-6">
                    <div className="text-right hidden sm:block">
                      <p className="text-[9px] font-bold text-slate-300 uppercase tracking-widest">Nộp lúc</p>
                      <p className="text-xs font-medium text-slate-600">{item.date}</p>
                    </div>
                    <Link to="/evaluation" className="p-2 text-slate-300 group-hover:text-indigo-600 transition-all">
                      <ArrowRight className="w-4 h-4" />
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column - Deadlines & Activities */}
        <div className="space-y-8">
          {/* Upcoming Deadlines */}
          <div className="bg-white p-8 rounded-2xl border border-slate-200 shadow-sm">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-6 flex items-center gap-2">
              <Clock className="w-4 h-4 text-amber-500" />
              Deadlines chấm điểm
            </h3>
            <div className="space-y-6">
              {[
                { title: 'Chấm Assignment 2', subject: 'Kỹ năng làm việc nhóm', deadline: 'Trong 2 ngày', status: 'high' },
                { title: 'Đánh giá Milestone 1', subject: 'Project Cuối kỳ', deadline: 'Trong 5 ngày', status: 'medium' },
                { title: 'Duyệt Đề tài Nhóm', subject: 'Class FC-02', deadline: 'Trong 7 ngày', status: 'low' },
              ].map((d, i) => (
                <div key={i} className="relative pl-5 pb-1">
                  <div className={cn(
                    "absolute left-0 top-0 bottom-0 w-1 rounded-full",
                    d.status === 'high' ? 'bg-red-500' : d.status === 'medium' ? 'bg-amber-500' : 'bg-emerald-500'
                  )}></div>
                  <p className="text-sm font-bold text-slate-900 mb-0.5">{d.title}</p>
                  <p className="text-[10px] text-slate-400 font-bold uppercase mb-1 tracking-tight">{d.subject}</p>
                  <p className="text-[10px] font-bold text-slate-500 flex items-center gap-1 italic">
                    <Calendar className="w-3 h-3" /> {d.deadline}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InstructorDashboard;
