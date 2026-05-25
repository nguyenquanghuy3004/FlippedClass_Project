import React, { useState, useMemo, useEffect } from 'react';
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
  PieChart,
  Sliders,
  Sparkles,
  Award
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from '../../lib/utils.js';

// Mock Data
const MOCK_STUDENTS = [
  { id: 10, name: 'Student', studentId: 'SV2012010', status: 'pending', progress: 85, lastActivity: '2 giờ trước' },
  { id: 1, name: 'Lê Minh Thành', studentId: 'SV2012015', status: 'pending', progress: 85, lastActivity: '2 giờ trước' },
  { id: 2, name: 'Nguyễn Thị Hoa', studentId: 'SV2012042', status: 'evaluated', progress: 98, lastActivity: '5 phút trước' },
  { id: 3, name: 'Trần Văn Dũng', studentId: 'SV2012008', status: 'pending', progress: 62, lastActivity: '1 ngày trước' },
];

const MOCK_ACTIVITY = [
  { id: 1, type: 'submission', title: 'Nộp Assignment 2: Cấu trúc Lesson Plan', date: '21/05/2026', time: '14:30', status: 'Ontime' },
  { id: 2, type: 'quiz', title: 'Hoàn thành Quiz 1: 9/10', date: '19/05/2026', time: '09:15', status: 'Passed' },
  { id: 3, type: 'discussion', title: 'Comment thảo luận Node 2: 3 bài viết', date: '18/05/2026', time: '21:00', status: 'Active' },
];

const EvaluationPage = () => {
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Independent grading criteria weights state (Requirement 9)
  const [crit1Weight, setCrit1Weight] = useState(30); // Hoạt động tự chuẩn bị bài tại nhà
  const [crit2Weight, setCrit2Weight] = useState(40); // Chất lượng slide & Thuyết trình
  const [crit3Weight, setCrit3Weight] = useState(30); // Tư duy phản biện & Góp ý trên lớp

  // Student specific grades
  const [crit1Score, setCrit1Score] = useState(8.0);
  const [crit2Score, setCrit2Score] = useState(8.5);
  const [crit3Score, setCrit3Score] = useState(9.0);
  const [feedback, setFeedback] = useState('');

  // Check weights sum
  const totalWeight = useMemo(() => {
    return Number(crit1Weight) + Number(crit2Weight) + Number(crit3Weight);
  }, [crit1Weight, crit2Weight, crit3Weight]);

  // Handle live weight quick configurations
  const handlePresetRubric = (type) => {
    if (type === 'prep') {
      setCrit1Weight(50);
      setCrit2Weight(30);
      setCrit3Weight(20);
    } else if (type === 'balanced') {
      setCrit1Weight(30);
      setCrit2Weight(40);
      setCrit3Weight(30);
    } else {
      setCrit1Weight(20);
      setCrit2Weight(50);
      setCrit3Weight(30);
    }
  };

  const students = useMemo(() => {
    return MOCK_STUDENTS.filter(s => 
      s.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
      s.studentId.includes(searchTerm)
    );
  }, [searchTerm]);

  const finalWeightedScore = useMemo(() => {
    const raw = (crit1Score * crit1Weight + crit2Score * crit2Weight + crit3Score * crit3Weight) / 100;
    return raw.toFixed(1);
  }, [crit1Score, crit2Score, crit3Score, crit1Weight, crit2Weight, crit3Weight]);

  const handleSubmitEvaluation = (e) => {
    e.preventDefault();
    if (totalWeight !== 100) {
      alert('Lỗi: Tổng trọng số các tiêu chí chấm điểm độc lập phải bằng đúng 100%!');
      return;
    }
    if (!feedback.trim()) {
      alert('Vui lòng viết phản hồi & nhận xét chi tiết.');
      return;
    }
    
    setIsSubmitting(true);
    setTimeout(() => {
      // Package details to save in localStorage for student roadmap view synchronization!
      const valuationDetails = {
        studentId: selectedStudent.id,
        score: finalWeightedScore,
        feedback: feedback,
        criteriaScores: [
          { name: 'Hoạt động chuẩn bị tại nhà (Prep-log)', weight: crit1Weight, score: crit1Score },
          { name: 'Nội dung slide & Thuyết trình', weight: crit2Weight, score: crit2Score },
          { name: 'Tương tác phản biện', weight: crit3Weight, score: crit3Score }
        ],
        evaluatedAt: new Date().toISOString()
      };

      // Save to local for current user integration or custom student IDs (like 10, 12, or default student)
      localStorage.setItem(`student_evaluation_${selectedStudent.id}_3`, JSON.stringify(valuationDetails));
      
      alert(`Đã lưu kết quả đánh giá thành công cho sinh viên ${selectedStudent.name}. Bảng điểm độc lập đã được cập nhật trực tiếp trên lộ trình Roadmap của sinh viên.`);
      
      setIsSubmitting(false);
      setSelectedStudent(null);
    }, 1000);
  };

  const selectStudentToEvaluate = (student) => {
    setSelectedStudent(student);

    if (student) {
      // Initial default sliders based on student profile fallback
      setCrit1Score(8.0);
      setCrit2Score(8.5);
      setCrit3Score(9.0);
      setFeedback('');
    }
  };

  return (
    <div className="h-full flex flex-col gap-6 animate-in fade-in duration-500 pb-12">
      {/* Header with Search and Rubric Config overview */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm p-6 flex flex-col lg:flex-row lg:items-center justify-between gap-6">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-[10px] bg-indigo-50 text-indigo-700 font-bold px-2 py-0.5 rounded-full uppercase">Tiêu chí độc lập</span>
            <span className="text-[10px] text-slate-450 italic">Công thức tích hợp 2026</span>
          </div>
          <h2 className="text-xl md:text-2xl font-black text-slate-900 tracking-tight leading-tight">Chấm điểm & Cấu hình Tiêu chí Độc lập</h2>
          <p className="text-xs text-slate-500 font-medium italic">Xác lập tỷ lệ trọng số rubrics cho việc tự học, chuẩn bị bài và phản biện xã hội.</p>
        </div>

        {/* Dynamic Global Rubric weight configuration panel */}
        <div className="bg-slate-50 border border-slate-200 p-4 rounded-2xl space-y-3 shrink-0 lg:max-w-md w-full">
          <div className="flex items-center justify-between">
            <span className="text-[10.5px] font-bold text-slate-450 uppercase tracking-wider flex items-center gap-1">
              <Sliders className="w-3.5 h-3.5 text-indigo-500" />
              Thiết lập trọng số % bộ rubrics
            </span>
            <span className={cn(
              "text-[10px] font-bold font-mono px-2 py-0.5 rounded-full",
              totalWeight === 100 ? "bg-emerald-100 text-emerald-800" : "bg-red-100 text-red-800"
            )}>
              Tổng: {totalWeight}% {totalWeight === 100 ? '✓' : '⚠️'}
            </span>
          </div>

          <div className="grid grid-cols-3 gap-2 text-[10px] font-bold text-slate-600">
            <div className="space-y-1">
              <span className="block text-slate-400">1. Tự chuẩn bị:</span>
              <div className="flex items-center gap-1">
                <input 
                  type="number" 
                  value={crit1Weight}
                  onChange={(e) => setCrit1Weight(Number(e.target.value))}
                  className="w-12 px-1 py-1 bg-white border border-slate-205 rounded text-center text-indigo-650"
                  min="0" max="100"
                />
                <span>%</span>
              </div>
            </div>
            <div className="space-y-1">
              <span className="block text-slate-400">2. Slide&Thuyết:</span>
              <div className="flex items-center gap-1">
                <input 
                  type="number" 
                  value={crit2Weight}
                  onChange={(e) => setCrit2Weight(Number(e.target.value))}
                  className="w-12 px-1 py-1 bg-white border border-slate-205 rounded text-center text-indigo-650"
                  min="0" max="100"
                />
                <span>%</span>
              </div>
            </div>
            <div className="space-y-1">
              <span className="block text-slate-400">3. Phản biện:</span>
              <div className="flex items-center gap-1">
                <input 
                  type="number" 
                  value={crit3Weight}
                  onChange={(e) => setCrit3Weight(Number(e.target.value))}
                  className="w-12 px-1 py-1 bg-white border border-slate-205 rounded text-center text-indigo-650"
                  min="0" max="100"
                />
                <span>%</span>
              </div>
            </div>
          </div>

          <div className="flex gap-2 pt-1">
            <button onClick={() => handlePresetRubric('balanced')} className="text-[9.5px] font-bold text-indigo-600 hover:underline">Cân bằng (30-40-30)</button>
            <span className="text-slate-300">|</span>
            <button onClick={() => handlePresetRubric('prep')} className="text-[9.5px] font-bold text-emerald-600 hover:underline">Trọng tự học (50-30-20)</button>
            <span className="text-slate-300">|</span>
            <button onClick={() => handlePresetRubric('assign')} className="text-[9.5px] font-bold text-purple-650 hover:underline">Trọng thuyết trình (20-50-30)</button>
          </div>
        </div>
      </div>

      <div className="relative group">
        <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-indigo-500 transition-colors" />
        <input 
          type="text" 
          placeholder="Tìm sinh viên nhanh bằng tên hoặc mã số sinh viên..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-11 pr-4 py-3 bg-white border border-slate-200 shadow-sm rounded-2xl text-xs w-full outline-none focus:ring-2 focus:ring-indigo-100 transition-all font-semibold"
        />
      </div>

      {/* Accordion List of Students */}
      <div className="flex-1 overflow-y-auto space-y-4 pr-1 text-slate-750">
        {students.map((student) => {
          const isExpanded = selectedStudent?.id === student.id;
          
          return (
            <div 
              key={student.id} 
              className={cn(
                "bg-white rounded-3xl border transition-all duration-300 overflow-hidden",
                isExpanded ? "border-indigo-200 shadow-lg" : "border-slate-200 hover:border-indigo-100 hover:shadow-xs"
              )}
            >
              {/* Accordion Header */}
              <div 
                className={cn(
                  "p-5 md:p-6 flex items-center justify-between cursor-pointer transition-all",
                  isExpanded ? "bg-indigo-50/20" : "hover:bg-slate-50/50"
                )}
                onClick={() => selectStudentToEvaluate(isExpanded ? null : student)}
              >
                <div className="flex items-center gap-4 flex-1">
                  <div className={cn(
                    "w-12 h-12 rounded-2xl flex items-center justify-center font-black text-sm transition-all duration-300",
                    isExpanded ? "bg-indigo-600 text-white" : "bg-slate-100 text-slate-550"
                  )}>
                    {student.name.charAt(0)}
                  </div>
                  <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4 md:items-center">
                    <div>
                      <p className="text-sm font-extrabold text-slate-850">{student.name}</p>
                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{student.studentId}</p>
                    </div>
                    <div className="hidden md:block">
                      <p className="text-[9px] text-slate-400 font-bold uppercase tracking-widest mb-1.5 leading-none">Tiến độ lộ trình học tập</p>
                      <div className="flex items-center gap-2">
                        <div className="flex-1 h-2 bg-slate-100 rounded-full overflow-hidden max-w-[150px]">
                          <div className={cn(
                            "h-full rounded-full transition-all duration-500",
                            student.progress > 80 ? "bg-emerald-500" : "bg-indigo-500"
                          )} style={{ width: `${student.progress}%` }}></div>
                        </div>
                        <span className="text-[10.5px] font-mono font-bold text-slate-600">{student.progress}%</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="ml-4">
                  {isExpanded ? (
                    <ChevronDown className="w-5 h-5 text-indigo-500 animate-pulse" />
                  ) : (
                    <ChevronRight className="w-5 h-5 text-slate-350" />
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
                        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-widest border-b border-slate-50 pb-3 mb-6 flex items-center gap-2">
                          <History className="w-3.5 h-3.5" />
                          Lịch sử chuẩn bị sườn học liệu
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
                                <p className="text-[9px] text-slate-400 font-bold uppercase mb-1">{activity.date}, {activity.time}</p>
                                <h4 className="text-xs font-bold text-slate-800 leading-tight">{activity.title}</h4>
                                <div className="mt-2 flex items-center gap-3">
                                  <span className="px-2 py-0.5 rounded text-[9px] font-bold bg-slate-50 text-slate-500 italic">
                                    {activity.status}
                                  </span>
                                  {activity.type === 'submission' && (
                                    <button className="text-[9.5px] font-bold text-indigo-500 hover:underline">Xem file nộp &rarr;</button>
                                  )}
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>

                      {/* Right Side: Evaluation Form / Independent rubrics scales */}
                      <div className="bg-[#FBFBFA] rounded-2xl border border-slate-200 p-6 space-y-6">
                        <div className="flex items-center justify-between">
                          <span className="text-[10px] font-bold text-slate-450 uppercase tracking-widest flex items-center gap-1.5">
                            <Sparkles className="w-4 h-4 text-indigo-500" />
                            Đánh giá chi tiết bằng bảng điểm tiêu chí độc lập
                          </span>
                        </div>

                        {totalWeight !== 100 && (
                          <div className="p-3 bg-red-50 border border-red-100 rounded-xl text-red-600 text-[10.5px] leading-relaxed flex items-center gap-2">
                            <AlertCircle className="w-4 h-4 text-red-500 shrink-0" />
                            <span>⚠️ Lưu ý: Tổng trọng số hiện tại của hệ thống đang bằng <strong className="font-mono">{totalWeight}%</strong> (khác 100%). Hãy cấu hình lại ở bảng trên trước khi bấm lưu.</span>
                          </div>
                        )}

                        <form onSubmit={handleSubmitEvaluation} className="space-y-5">
                          
                          {/* Criterion 1 */}
                          <div className="space-y-2">
                            <div className="flex justify-between text-xs font-bold text-slate-705">
                              <span>1. Hoạt động chuẩn bị bài tại nhà (Trọng số: {crit1Weight}%)</span>
                              <span className="font-mono text-indigo-650">{crit1Score} / 10</span>
                            </div>
                            <input 
                              type="range" 
                              min="0" max="10" step="0.5"
                              value={crit1Score}
                              onChange={(e) => setCrit1Score(Number(e.target.value))}
                              className="w-full accent-indigo-600 cursor-pointer"
                            />
                          </div>

                          {/* Criterion 2 */}
                          <div className="space-y-2">
                            <div className="flex justify-between text-xs font-bold text-slate-705">
                              <span>2. Chất lượng Slide & Thuyết trình chuyên đề (Trọng số: {crit2Weight}%)</span>
                              <span className="font-mono text-indigo-650">{crit2Score} / 10</span>
                            </div>
                            <input 
                              type="range" 
                              min="0" max="10" step="0.5"
                              value={crit2Score}
                              onChange={(e) => setCrit2Score(Number(e.target.value))}
                              className="w-full accent-indigo-600 cursor-pointer"
                            />
                          </div>

                          {/* Criterion 3 */}
                          <div className="space-y-2">
                            <div className="flex justify-between text-xs font-bold text-slate-705">
                              <span>3. Tư duy phản biện & Góp ý trên lớp (Trọng số: {crit3Weight}%)</span>
                              <span className="font-mono text-indigo-650">{crit3Score} / 10</span>
                            </div>
                            <input 
                              type="range" 
                              min="0" max="10" step="0.5"
                              value={crit3Score}
                              onChange={(e) => setCrit3Score(Number(e.target.value))}
                              className="w-full accent-indigo-600 cursor-pointer"
                            />
                          </div>

                          {/* Automatically Calculated Score Dashboard */}
                          <div className="p-4 bg-indigo-50 border border-indigo-100 rounded-2xl text-center space-y-1">
                            <p className="text-[9px] font-bold text-indigo-600 uppercase tracking-widest leading-none">Điểm tổng kết tích hợp (Weighted Score)</p>
                            <p className="text-3xl font-black text-indigo-750 font-mono">{finalWeightedScore}</p>
                            <span className="text-[9px] text-slate-400 italic">Tính toán tự động theo công thức độc lập</span>
                          </div>

                          {/* Detailed Feedback & Comment section */}
                          <div>
                            <div className="flex items-center justify-between mb-2 ml-1">
                              <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest">Phản hồi & Nhận xét chi tiết</label>
                              <span className="text-[9px] font-bold text-slate-400 uppercase tracking-tight">Chi tiết</span>
                            </div>
                            <textarea 
                              placeholder="Nhập nhận xét mang tính xây dựng, định hướng cho sinh viên..."
                              value={feedback}
                              onChange={(e) => setFeedback(e.target.value)}
                              className="w-full h-24 p-3.5 bg-white border border-slate-205 rounded-xl outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-500 text-xs text-slate-700 leading-relaxed font-sans resize-none transition-all placeholder:italic"
                              required
                            ></textarea>
                          </div>

                          <button 
                            type="submit"
                            disabled={isSubmitting || totalWeight !== 100}
                            className="w-full py-3.5 bg-white border border-indigo-200 text-indigo-600 text-xs font-bold rounded-xl shadow-md hover:bg-indigo-50/50 transition-all flex items-center justify-center gap-1.5 group disabled:opacity-50 active:scale-[0.98] cursor-pointer"
                          >
                            {isSubmitting ? 'Đang gửi lưu cấu hình...' : (
                              <>
                                <span>Xác nhận Nộp điểm & Phản hồi</span>
                                <ArrowRight className="w-4 h-4 group-hover:translate-x-1" />
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
