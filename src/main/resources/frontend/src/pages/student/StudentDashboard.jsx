import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { 
  School, 
  Plus, 
  CheckCircle2, 
  Clock, 
  Award, 
  TrendingUp, 
  ChevronRight, 
  FileText, 
  Calendar, 
  BookOpen, 
  ShieldAlert, 
  HelpCircle,
  Hash,
  Sparkles,
  Search
} from 'lucide-react';
import { cn } from '../../lib/utils.js';

const PRESET_CLASSES = [
  {
    id: 'c1',
    code: 'ABC12345',
    name: 'Lớp học Flipped Classroom 101',
    description: 'Giới thiệu về mô hình lớp học đảo ngược cơ bản và thang năng lực Bloom.',
    instructor: 'instructor',
    category: 'Chuyên ngành',
    documents: 4,
    schedule: 'Thứ Ba hàng tuần, 08:00 - 11:30'
  },
  {
    id: 'c2',
    code: 'CS202',
    name: 'Cấu trúc dữ liệu & Giải thuật đảo ngược',
    description: 'Học lý thuyết cây, đồ thị qua slide/video trước lớp và giải thuật tối ưu tại lớp.',
    instructor: 'Vũ Giảng Viên Google',
    category: 'Cơ sở ngành',
    documents: 8,
    schedule: 'Thứ Năm hàng tuần, 13:30 - 17:00'
  },
  {
    id: 'c3',
    code: 'IT4010',
    name: 'Phát triển ứng dụng Web nâng cao',
    description: 'Tự nghiên cứu kiến thức React/Vite/TypeScript và thảo luận thiết kế hệ thống bento.',
    instructor: 'Lê Giảng Viên',
    category: 'Chuyên ngành',
    documents: 12,
    schedule: 'Thứ Bảy hàng tuần, 08:00 - 11:30'
  }
];

const StudentDashboard = () => {
  const { user } = useAuth();
  
  // Dashboard & persistence state
  const [joinedClasses, setJoinedClasses] = useState([]);
  const [inviteCode, setInviteCode] = useState('');
  const [joinSuccess, setJoinSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [activeTab, setActiveTab] = useState('classes'); // 'classes' or 'stats'
  const [profileData, setProfileData] = useState({ gpaTarget: '3.6', hoursTarget: '12' });
  const [leaveConfirmId, setLeaveConfirmId] = useState(null);

  useEffect(() => {
    if (user) {
      // Load joined classes of this student from localStorage
      const savedJoined = localStorage.getItem(`joined_classes_${user.id}`);
      if (savedJoined) {
        setJoinedClasses(JSON.parse(savedJoined));
      } else {
        // Default join the first class for onboarding
        const initial = [PRESET_CLASSES[0]];
        setJoinedClasses(initial);
        localStorage.setItem(`joined_classes_${user.id}`, JSON.stringify(initial));
      }

      // Load profile target gpa for statistic visualization
      const savedProfile = localStorage.getItem(`profile_data_${user.id}`);
      if (savedProfile) {
        setProfileData(JSON.parse(savedProfile));
      }
    }
  }, [user]);

  const handleJoinClass = (e) => {
    e.preventDefault();
    setErrorMsg('');
    setJoinSuccess(false);

    const code = inviteCode.trim().toUpperCase();
    if (!code) return;

    // Check if already joined
    if (joinedClasses.some(c => c.code === code)) {
      setErrorMsg('Bạn đã tham gia lớp học này rồi.');
      return;
    }

    // Check presets
    const foundClass = PRESET_CLASSES.find(c => c.code === code);
    if (foundClass) {
      const updated = [...joinedClasses, foundClass];
      setJoinedClasses(updated);
      localStorage.setItem(`joined_classes_${user.id}`, JSON.stringify(updated));
      setInviteCode('');
      setJoinSuccess(true);
      setTimeout(() => setJoinSuccess(false), 4000);
    } else {
      // Dynamic generate class mock mapping so any code will join successfully but with dynamic names!
      const generatedClass = {
        id: `c_dyn_${Date.now()}`,
        code: code,
        name: `Lớp học chuyên đề mã ${code}`,
        description: 'Lớp học chuyên biệt tự sinh thông qua mã mời tùy chỉnh của sinh viên.',
        instructor: 'Giảng viên Bản đồ',
        category: 'Tự chọn',
        documents: 2,
        schedule: 'Thứ Hai hàng tuần, 09:00 - 11:30'
      };
      const updated = [...joinedClasses, generatedClass];
      setJoinedClasses(updated);
      localStorage.setItem(`joined_classes_${user.id}`, JSON.stringify(updated));
      setInviteCode('');
      setJoinSuccess(true);
      setTimeout(() => setJoinSuccess(false), 4000);
    }
  };

  const handleLeaveClass = (id) => {
    const updated = joinedClasses.filter(c => c.id !== id);
    setJoinedClasses(updated);
    localStorage.setItem(`joined_classes_${user.id}`, JSON.stringify(updated));
  };

  // Pre-class quiz performance indicators
  const quizScores = [9.0, 8.5, 10.0, 7.5];
  const averageQuiz = (quizScores.reduce((a, b) => a + b, 0) / quizScores.length).toFixed(1);
  const prepLogsCount = 6;
  const currentGPA = 3.5;

  return (
    <div className="space-y-8 animate-in fade-in duration-500 pb-12">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden bg-white p-8 md:p-10 rounded-3xl border border-slate-200 shadow-sm flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="absolute right-0 top-0 bottom-0 w-1/3 bg-radial from-indigo-50/40 via-transparent to-transparent -z-10"></div>
        <div>
          <h2 className="text-2xl md:text-3xl font-extrabold text-slate-900 tracking-tight">Chào mừng quay lại, {user?.name || 'Sinh viên'}</h2>
          <p className="text-slate-500 mt-1 font-medium text-sm md:text-base italic">Hôm nay bạn có <strong className="text-indigo-600">2 nhiệm vụ chuẩn bị học tập (prep-logs)</strong> đang chờ hoàn thành.</p>
        </div>

        {/* Action Tabs in Header */}
        <div className="flex gap-2 p-1 bg-slate-100 rounded-2xl w-full md:w-auto">
          <button 
            onClick={() => setActiveTab('classes')}
            className={cn(
              "flex-1 md:flex-initial px-5 py-2 rounded-xl text-xs font-bold transition-all",
              activeTab === 'classes' ? "bg-white text-slate-900 shadow-sm border border-slate-200" : "text-slate-550 hover:text-slate-800"
            )}
          >
            Lớp học của tôi
          </button>
          <button 
            onClick={() => setActiveTab('stats')}
            className={cn(
              "flex-1 md:flex-initial px-5 py-2 rounded-xl text-xs font-bold transition-all",
              activeTab === 'stats' ? "bg-white text-slate-900 shadow-sm border border-slate-200" : "text-slate-550 hover:text-slate-800"
            )}
          >
            Thống kê học thuật
          </button>
        </div>
      </div>

      {joinSuccess && (
        <div className="p-4 bg-emerald-50 border border-emerald-150 text-emerald-700 text-sm font-semibold rounded-2xl flex items-center gap-3 animate-in fade-in duration-300">
          <CheckCircle2 className="w-5 h-5 text-emerald-500 flex-shrink-0" />
          <span>Bạn đã tham gia lớp học thành công! Lộ trình Roadmap tương ứng đã được đồng bộ hóa.</span>
        </div>
      )}

      {errorMsg && (
        <div className="p-4 bg-red-50 border border-red-100 text-red-600 text-sm font-semibold rounded-2xl flex items-center gap-3 animate-head-shake">
          <ShieldAlert className="w-5 h-5 text-red-500 flex-shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      {activeTab === 'classes' ? (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main List of Classes */}
          <div className="lg:col-span-2 space-y-6">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-widest flex items-center gap-2 border-b border-slate-100 pb-2">
              <School className="w-4 h-4 text-indigo-500" />
              Danh sách lớp học
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {joinedClasses.map((item) => (
                <div key={item.id} className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm hover:shadow-md hover:border-indigo-150 transition-all flex flex-col justify-between group">
                  <div>
                    <div className="flex justify-between items-start mb-4">
                      <span className="px-2.5 py-0.8 bg-indigo-50 border border-indigo-100 text-indigo-600 rounded-lg text-[10px] font-bold tracking-wider font-mono uppercase">
                        {item.code}
                      </span>
                      {leaveConfirmId === item.id ? (
                        <div className="flex items-center gap-1.5 bg-red-50 border border-red-105 p-1.5 rounded-xl animate-in fade-in duration-200">
                          <span className="text-[10px] font-bold text-red-700">Rời?</span>
                          <button
                            type="button"
                            onClick={() => {
                              handleLeaveClass(item.id);
                              setLeaveConfirmId(null);
                            }}
                            className="px-2 py-1 bg-white border border-red-200 text-red-600 rounded-lg text-[9px] font-bold hover:bg-red-50 cursor-pointer transition-colors active:scale-95"
                          >
                            Có
                          </button>
                          <button
                            type="button"
                            onClick={() => setLeaveConfirmId(null)}
                            className="px-2 py-1 bg-white border border-slate-200 text-slate-600 rounded-lg text-[9px] font-bold hover:bg-slate-50 cursor-pointer"
                          >
                            Hủy
                          </button>
                        </div>
                      ) : (
                        <button 
                          type="button"
                          onClick={() => setLeaveConfirmId(item.id)}
                          className="text-[10px] text-slate-440 hover:text-red-605 font-bold uppercase tracking-tight cursor-pointer"
                        >
                          Rời lớp
                        </button>
                      )}
                    </div>

                    <h4 className="text-base font-bold text-slate-850 group-hover:text-indigo-605 transition-all leading-snug">{item.name}</h4>
                    <p className="text-xs text-slate-500 mt-2 leading-relaxed italic line-clamp-2">{item.description}</p>
                  </div>

                  <div className="mt-6 pt-4 border-t border-slate-100 space-y-3">
                    <div className="flex items-center justify-between text-[11px] font-semibold text-slate-600">
                      <span className="text-slate-450 font-normal">Giảng viên phụ trách:</span>
                      <span className="text-slate-800 font-bold">{item.instructor}</span>
                    </div>
                    <div className="flex items-center justify-between text-[11px] font-semibold text-slate-600">
                      <span className="text-slate-450 font-normal">Lịch học trực tiếp:</span>
                      <span className="text-indigo-600 font-bold text-right">{item.schedule}</span>
                    </div>
                  </div>
                </div>
              ))}

              {joinedClasses.length === 0 && (
                <div className="col-span-full bg-white border border-dashed border-slate-250 rounded-2xl p-12 text-center text-slate-450 italic text-sm">
                  Bạn chưa tham gia lớp học nào. Hãy sử dụng bảng Mã mời bên phải để tham gia lớp học ngay!
                </div>
              )}
            </div>

            {/* Preparation Timetable logs */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
              <h3 className="text-xs font-bold text-slate-450 tracking-widest uppercase mb-4 flex items-center gap-2 border-b border-slate-50 pb-2">
                <BookOpen className="w-4 h-4 text-emerald-500" />
                Lịch tương tác & chuẩn bị học tập
              </h3>
              <div className="divide-y divide-slate-100">
                <div className="py-3 flex items-center justify-between text-xs font-semibold text-slate-705">
                  <div className="flex items-center gap-3">
                    <span className="w-2.5 h-2.5 rounded-full bg-indigo-500"></span>
                    <span>Nghiên cứu tài liệu: <strong className="text-slate-900">Tuần 1: Flipped Classroom</strong></span>
                  </div>
                  <span className="text-indigo-600">Chuẩn bị xong 100%</span>
                </div>
                <div className="py-3 flex items-center justify-between text-xs font-semibold text-slate-705">
                  <div className="flex items-center gap-3">
                    <span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span>
                    <span>Hoàn thành Trắc nghiệm: <strong className="text-slate-900">Quiz 1: Triết lý Bloom</strong></span>
                  </div>
                  <span className="text-amber-600 font-bold">Chờ thi thử</span>
                </div>
                <div className="py-3 flex items-center justify-between text-xs font-semibold text-slate-705">
                  <div className="flex items-center gap-3">
                    <span className="w-2.5 h-2.5 rounded-full bg-slate-350"></span>
                    <span>Soạn slide bài tập lớn: <strong className="text-slate-900">Thiết kế bài giảng Edpuzzle</strong></span>
                  </div>
                  <span className="text-slate-400">Thời hạn: còn 3 ngày</span>
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar - Join Class Panel */}
          <div className="space-y-6">
            <div className="bg-indigo-600 text-white rounded-2xl p-6 shadow-md shadow-indigo-100 space-y-4">
              <div className="flex items-center gap-2">
                <h3 className="font-bold tracking-tight text-[15px]">Tham gia phòng học mới</h3>
              </div>
              <p className="text-xs text-indigo-100 leading-relaxed font-light">Mô hình Flipped Classroom vận hành khép kín qua mã bảo mật. Nhập mã mời để tự động kích hoạt lộ trình học tập của bạn.</p>
              
              <form onSubmit={handleJoinClass} className="space-y-3 pt-2">
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-xs font-mono font-bold text-slate-400">#</span>
                  <input 
                    type="text"
                    value={inviteCode}
                    onChange={(e) => setInviteCode(e.target.value)}
                    placeholder="MĂ_MỜI (ví dụ: CS202, IT4010)"
                    className="w-full pl-7 pr-3 py-3 bg-white border border-transparent rounded-xl text-xs font-bold text-slate-800 outline-none focus:ring-2 focus:ring-amber-300 placeholder:text-slate-350 uppercase transition-all"
                    required
                  />
                </div>
                <button 
                  type="submit"
                  className="w-full py-3 bg-amber-400 hover:bg-amber-300 text-indigo-950 font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition-all active:scale-[0.98]"
                >
                  <Plus className="w-4 h-4" />
                  Kích hoạt và Vào học
                </button>
              </form>

              <div className="text-[10px] bg-indigo-700/50 p-2.5 rounded-xl border border-indigo-500/20 text-indigo-200">
                <p className="font-bold uppercase tracking-widest text-[9px] mb-1 text-indigo-100">Gợi ý mã mời thử nghiệm:</p>
                <div className="space-y-1 font-mono text-[9px] list-disc pl-1">
                  <div>• <strong className="text-amber-300">ABC12345</strong> (Lớp học 101)</div>
                  <div>• <strong className="text-amber-300">CS202</strong> (Lớp Thuật toán)</div>
                  <div>• <strong className="text-amber-300">IT4010</strong> (Lớp Web nâng cao)</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        /* THỐNG KÊ HỌC THUẬT NÂNG CAO TAB */
        <div className="space-y-8 animate-in fade-in duration-300">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Metric Card 1: GPA Target Gauge */}
            <div className="bg-white border border-slate-200 p-6 rounded-2xl shadow-sm space-y-4">
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest italic leading-none">Mục tiêu Điểm số (GPA)</p>
              <div className="flex items-center gap-6">
                {/* Simulated circle dial in SVG */}
                <div className="relative w-16 h-16">
                  <svg className="w-full h-full -rotate-90" viewBox="0 0 36 36">
                    <path className="text-slate-100" strokeWidth="3" stroke="currentColor" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <path className="text-indigo-600" strokeDasharray={`${(currentGPA / 4.0) * 100}, 100`} strokeWidth="3.5" stroke="currentColor" fill="none" strokeLinecap="round" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                  </svg>
                  <div className="absolute inset-0 flex items-center justify-center text-xs font-mono font-bold text-indigo-600">87%</div>
                </div>
                <div>
                  <h4 className="text-2xl font-black text-slate-900 font-mono">{currentGPA} / 4.0</h4>
                  <p className="text-xs text-slate-500 font-medium">Mục tiêu cá nhân: <strong>{profileData.gpaTarget || '3.6'}</strong></p>
                </div>
              </div>
            </div>

            {/* Metric Card 2: Self-study hours */}
            <div className="bg-white border border-slate-200 p-6 rounded-2xl shadow-sm space-y-4">
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest italic leading-none">Thời gian tự học tuần này</p>
              <div className="flex items-center gap-6">
                <div className="relative w-16 h-16">
                  <svg className="w-full h-full -rotate-90" viewBox="0 0 36 36">
                    <path className="text-slate-100" strokeWidth="3" stroke="currentColor" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <path className="text-emerald-500" strokeDasharray="66, 100" strokeWidth="3.5" stroke="currentColor" fill="none" strokeLinecap="round" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                  </svg>
                  <div className="absolute inset-0 flex items-center justify-center text-xs font-mono font-bold text-emerald-600">66%</div>
                </div>
                <div>
                  <h4 className="text-2xl font-black text-slate-900 font-mono">8 / {profileData.hoursTarget || '12'} giờ</h4>
                  <p className="text-xs text-slate-500 font-medium">Còn thiếu 4 giờ để đạt mục tiêu</p>
                </div>
              </div>
            </div>

            {/* Metric Card 3: Quiz/Test grades */}
            <div className="bg-white border border-slate-200 p-6 rounded-2xl shadow-sm space-y-4">
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest italic leading-none">Trung bình Trắc nghiệm tự chấm</p>
              <div className="flex items-center gap-6">
                <div className="relative w-16 h-16">
                  <svg className="w-full h-full -rotate-90" viewBox="0 0 36 36">
                    <path className="text-slate-100" strokeWidth="3" stroke="currentColor" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <path className="text-purple-500" strokeDasharray="88, 100" strokeWidth="3.5" stroke="currentColor" fill="none" strokeLinecap="round" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                  </svg>
                  <div className="absolute inset-0 flex items-center justify-center text-xs font-mono font-bold text-purple-600">88%</div>
                </div>
                <div>
                  <h4 className="text-2xl font-black text-slate-900 font-mono">{averageQuiz} / 10.0</h4>
                  <p className="text-xs text-slate-500 font-medium">Đã làm <strong className="text-purple-600">4 quizzes</strong></p>
                </div>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Visual Skill Matrix (Custom SVG Bento Shape) */}
            <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
              <h3 className="text-xs font-bold text-slate-450 uppercase tracking-widest mb-6 border-b border-slate-55 pb-2">Bản đồ năng lực học tập đảo ngược (Bloom taxonomy)</h3>
              
              <div className="space-y-4">
                {[
                  { skill: 'Nhận diện & Ghi nhớ (Tự học)', score: 92, count: 'Đọc video/tài liệu đúng chuẩn', color: 'bg-indigo-500' },
                  { skill: 'Thông hiểu lý thuyết (Làm trắc nghiệm)', score: 88, count: 'Tự động kiểm tra câu hỏi', color: 'bg-purple-500' },
                  { skill: 'Vận dụng (Làm bài tập / Homework)', score: 75, count: 'Xây dựng slide và Lesson Plan', color: 'bg-blue-500' },
                  { skill: 'Phân tích & Phản biện (Hoạt động trên lớp)', score: 80, count: 'Tranh luận nhóm trên lớp', color: 'bg-emerald-500' },
                ].map((s, i) => (
                  <div key={i} className="space-y-1.5">
                    <div className="flex items-center justify-between text-xs font-bold text-slate-705">
                      <span>{s.skill}</span>
                      <span className="font-mono">{s.score}%</span>
                    </div>
                    <div className="w-full h-2 bg-slate-50/50 border border-slate-100 rounded-full overflow-hidden">
                      <div className={cn("h-full rounded-full transition-all duration-700", s.color)} style={{ width: `${s.score}%` }}></div>
                    </div>
                    <div className="text-[10px] text-slate-400 font-medium italic">{s.count}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Study progression timeline */}
            <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
              <h3 className="text-xs font-bold text-slate-450 uppercase tracking-widest mb-4 border-b border-slate-55 pb-2">Nhật ký điểm số & Phản hồi gần đây</h3>
              <div className="space-y-4 max-h-[320px] overflow-y-auto pr-2">
                {[
                  { item: 'Nộp Assignment 2: Cấu trúc Lesson Plan', score: '8.5 / 10', details: 'Nộp đúng hạn. Giảng viên đánh giá slide thuyết trình đẹp, súc tích, tóm lược tốt.', date: '3 ngày trước' },
                  { item: 'Quiz 2: Bloom Cognitive Levels', score: '10 / 10', details: 'Chấm điểm tự động bởi hệ thống. Đúng 10/10 câu hỏi.', date: '1 tuần trước' },
                  { item: 'Quiz 1: Kiến thức nền tảng Flipped Classroom', score: '9.0 / 10', details: 'Chấm điểm tự động bởi hệ thống. Sai câu số 6.', date: '2 tuần trước' },
                ].map((log, idx) => (
                  <div key={idx} className="p-4 border border-slate-100 rounded-xl bg-slate-50/30 space-y-2">
                    <div className="flex justify-between items-start">
                      <h4 className="text-xs font-bold text-slate-800 leading-tight">{log.item}</h4>
                      <span className="px-2 py-0.5 bg-indigo-50 border border-indigo-100 text-indigo-600 rounded text-[9.5px] font-mono font-bold whitespace-nowrap">{log.score}</span>
                    </div>
                    <p className="text-[10.5px] text-slate-500 leading-relaxed italic">{log.details}</p>
                    <div className="flex justify-between text-[9px] text-slate-400 font-bold uppercase tracking-tight">
                      <span>Theo dõi học lý thuyết</span>
                      <span>{log.date}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentDashboard;
