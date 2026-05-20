import React, { useState, useMemo } from 'react';
import { 
  Users, 
  Search, 
  Filter, 
  History, 
  Calendar, 
  CheckCircle2, 
  AlertCircle,
  TrendingUp,
  MessageSquare,
  ChevronRight,
  ChevronDown,
  MoreVertical,
  Clock,
  ArrowUpRight,
  ArrowRight,
  PieChart
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from '../../lib/utils.js';

// Mock Data
const MOCK_STUDENTS = [
  { id: 1, name: 'Lê Minh Thành', studentId: '2012015', status: 'pending', progress: 85, lastActivity: '2 giờ trước' },
  { id: 2, name: 'Nguyễn Thị Hoa', studentId: '2012042', status: 'evaluated', progress: 98, lastActivity: '5 phút trước' },
  { id: 3, name: 'Trần Văn Dũng', studentId: '2012008', status: 'pending', progress: 62, lastActivity: '1 ngày trước' },
  { id: 4, name: 'Phạm Bảo Trân', studentId: '2012099', status: 'pending', progress: 75, lastActivity: '4 giờ trước' },
  { id: 5, name: 'Đặng Hoàng Nam', studentId: '2012031', status: 'needs_review', progress: 80, lastActivity: '10 giờ trước' },
];

const MOCK_ACTIVITY = [
  { id: 1, type: 'submission', title: 'Nộp Assignment 2: Cấu trúc Lesson Plan', date: '15/05/2026', time: '14:30', status: 'Ontime' },
  { id: 2, type: 'quiz', title: 'Hoàn thành Quiz 1: 9/10', date: '12/05/2026', time: '09:15', status: 'Passed' },
  { id: 3, type: 'discussion', title: 'Comment thảo luận Node 2: 3 bài viết', date: '10/05/2026', time: '21:00', status: 'Active' },
  { id: 4, type: 'attendance', title: 'Vắng buổi học Node 1', date: '08/05/2026', time: '08:00', status: 'Absent' },
];

const EvaluationPage = () => {
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [score, setScore] = useState('');
  const [feedback, setFeedback] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const students = useMemo(() => {
    return MOCK_STUDENTS.filter(s => 
      s.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
      s.studentId.includes(searchTerm)
    );
  }, [searchTerm]);

  const handleSubmitEvaluation = (e) => {
    e.preventDefault();
    if (!score || !feedback) return;
    
    setIsSubmitting(true);
    setTimeout(() => {
      alert('Đã lưu kết quả đánh giá cho ' + selectedStudent.name);
      setIsSubmitting(false);
      setSelectedStudent(null);
      setScore('');
      setFeedback('');
    }, 1000);
  };

  return (
    <div className="h-full flex flex-col gap-6 animate-in fade-in duration-500 overflow-hidden">
      {/* Header with Search */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 tracking-tight">Đánh giá Sinh viên</h2>
          <p className="text-sm text-slate-500 font-medium italic">Quản lý và đánh giá tiến độ học tập thực tế</p>
        </div>
        <div className="relative group">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-indigo-500 transition-colors" />
          <input 
            type="text" 
            placeholder="Tìm kiếm MSSV, Tên..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm w-full md:w-72 outline-none focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-300 transition-all font-medium"
          />
        </div>
      </div>

      {/* Accordion List */}
      <div className="flex-1 overflow-y-auto space-y-4 pr-2">
        {students.map((student) => {
          const isExpanded = selectedStudent?.id === student.id;
          
          return (
            <div 
              key={student.id} 
              className={cn(
                "bg-white rounded-2xl border transition-all duration-300 overflow-hidden",
                isExpanded ? "border-indigo-200 shadow-md" : "border-slate-200 hover:border-indigo-100 hover:shadow-sm"
              )}
            >
              {/* Accordion Header */}
              <div 
                className={cn(
                  "p-5 md:p-6 flex items-center justify-between cursor-pointer transition-colors",
                  isExpanded ? "bg-indigo-50/30" : "hover:bg-slate-50/50"
                )}
                onClick={() => setSelectedStudent(isExpanded ? null : student)}
              >
                <div className="flex items-center gap-4 flex-1">
                  <div className={cn(
                    "w-12 h-12 rounded-xl flex items-center justify-center font-bold text-sm transition-colors duration-300",
                    isExpanded ? "bg-indigo-600 text-white" : "bg-slate-100 text-slate-500"
                  )}>
                    {student.name.charAt(0)}
                  </div>
                  <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4 md:items-center">
                    <div>
                      <p className="text-base font-bold text-slate-900">{student.name}</p>
                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{student.studentId}</p>
                    </div>
                    <div className="hidden md:block">
                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest mb-1">Tiến độ</p>
                      <div className="flex items-center gap-2">
                        <div className="flex-1 h-1.5 bg-slate-100 rounded-full overflow-hidden max-w-[120px]">
                          <div className={cn(
                            "h-full rounded-full transition-all duration-500",
                            student.progress > 80 ? "bg-emerald-500" : "bg-indigo-500"
                          )} style={{ width: `${student.progress}%` }}></div>
                        </div>
                        <span className="text-xs font-mono font-bold text-slate-600">{student.progress}%</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="ml-4">
                  {isExpanded ? (
                    <ChevronDown className="w-5 h-5 text-indigo-500" />
                  ) : (
                    <ChevronRight className="w-5 h-5 text-slate-300" />
                  )}
                </div>
              </div>

              {/* Accordion Content */}
              <AnimatePresence>
                {isExpanded && (
                  <motion.div
                    initial={{ height: 0, opacity: 0 }}
                    animate={{ height: "auto", opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }}
                    transition={{ duration: 0.3, ease: "easeInOut" }}
                    className="overflow-hidden"
                  >
                    <div className="p-6 md:p-8 pt-2 border-t border-indigo-50 bg-white grid grid-cols-1 lg:grid-cols-2 gap-8">
                      {/* Left Side: Activity Timeline */}
                      <div>
                        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-widest border-b border-slate-100 pb-3 mb-6 flex items-center gap-2">
                          <History className="w-3.5 h-3.5" />
                          Lịch sử hoạt động học tập
                        </h3>
                        <div className="space-y-6">
                          {MOCK_ACTIVITY.map((activity) => (
                            <div key={activity.id} className="flex gap-4">
                              <div className="flex flex-col items-center">
                                <div className={cn(
                                  "w-2 h-2 rounded-full mt-1.5 ring-4",
                                  activity.type === 'submission' ? 'bg-indigo-500 ring-indigo-50' : 
                                  activity.type === 'quiz' ? 'bg-emerald-400 ring-emerald-50' : 'bg-slate-300 ring-slate-50'
                                )}></div>
                                <div className="w-px flex-1 bg-slate-100 mt-2"></div>
                              </div>
                              <div className="pb-2">
                                <p className="text-[10px] text-slate-400 font-bold uppercase mb-1">{activity.date}, {activity.time}</p>
                                <h4 className="text-sm font-bold text-slate-800">{activity.title}</h4>
                                <div className="mt-2 flex items-center gap-3">
                                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-50 text-slate-500 italic">
                                    {activity.status}
                                  </span>
                                  {activity.type === 'submission' && (
                                    <button className="text-[10px] font-bold text-indigo-500 hover:underline">Xem file nộp &rarr;</button>
                                  )}
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>

                      {/* Right Side: Evaluation Form */}
                      <div className="bg-[#FBFBFA] rounded-xl border border-slate-200 p-6">
                        <div className="flex items-center justify-between mb-6">
                          <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
                            <CheckCircle2 className="w-3.5 h-3.5 text-indigo-500" />
                            Đánh giá & Cho điểm
                          </h3>
                        </div>

                        <form onSubmit={handleSubmitEvaluation} className="space-y-6">
                          <div>
                            <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-3">Điểm số (Thang 10)</label>
                            <div className="flex items-center gap-4">
                              <input 
                                type="number" 
                                min="0" max="10" step="0.5" 
                                placeholder="0.0"
                                value={score}
                                onChange={(e) => setScore(e.target.value)}
                                className="w-24 px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-500 text-lg font-mono font-bold transition-all text-center"
                                required
                              />
                              <div className="flex-1 text-[10px] text-slate-400 font-medium italic leading-tight">
                                Nhập điểm dựa trên kết quả nộp bài và tương tác thực tế của sinh viên.
                              </div>
                            </div>
                          </div>

                          <div>
                            <div className="flex items-center justify-between mb-3">
                              <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest">Phản hồi của giảng viên</label>
                              <span className="text-[9px] font-bold text-red-400 uppercase tracking-tighter">Bắt buộc*</span>
                            </div>
                            <textarea 
                              placeholder="Nhập nhận xét cụ thể về quá trình học tập..."
                              value={feedback}
                              onChange={(e) => setFeedback(e.target.value)}
                              className="w-full h-32 p-4 bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-500 text-xs text-slate-700 leading-relaxed resize-none transition-all placeholder:italic"
                              required
                            ></textarea>
                          </div>

                          <button 
                            type="submit"
                            disabled={isSubmitting || !score || !feedback}
                            className="w-full py-3.5 bg-indigo-600 text-white text-sm font-bold rounded-xl shadow-lg shadow-indigo-100 hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 group disabled:opacity-50"
                          >
                            {isSubmitting ? 'Đang lưu...' : (
                              <>
                                <span>Xác nhận</span>
                                <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                              </>
                            )}
                          </button>
                        </form>
                      </div>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default EvaluationPage;
