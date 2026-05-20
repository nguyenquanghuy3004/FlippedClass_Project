import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { 
  BookOpen, 
  Users, 
  GitFork, 
  CheckSquare, 
  TrendingUp, 
  Award, 
  Layers, 
  HelpCircle, 
  CornerDownRight, 
  VolumeX, 
  MessageSquare,
  ArrowRight,
  Sparkles,
  ShieldAlert,
  ClipboardList,
  Eye,
  Rocket
} from 'lucide-react';

const Home = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('instructor'); // 'instructor' or 'student'

  const handleStart = () => {
    if (user) {
      navigate('/dashboard');
    } else {
      navigate('/login');
    }
  };

  const instructorFeatures = [
    {
      icon: <Layers className="w-6 h-6 text-indigo-600" />,
      title: "Quản lý môn học & Lớp học",
      desc: "Dễ dàng khởi tạo môn học, quản lý danh sách lớp. Thêm sinh viên nhanh chóng qua email, mã mời QR, hoặc liên kết tham gia trực tiếp."
    },
    {
      icon: <GitFork className="w-6 h-6 text-violet-600" />,
      title: "Xây dựng Lộ trình (Learning Path)",
      desc: "Thiết kế bài giảng dạng sơ đồ Node rẽ nhánh trực quan. Chủ động kiểm soát lộ trình bằng cách bật/tắt các node học và đính kèm học liệu tương ứng."
    },
    {
      icon: <CheckSquare className="w-6 h-6 text-blue-600" />,
      title: "Bài tập đa dạng & Phân loại linh hoạt",
      desc: "Xây dựng câu hỏi trắc nghiệm, bài luận hoặc thực hành bài tập lớn. Phân loại rõ ràng: câu hỏi tự học tại nhà, ôn tập trên lớp, hoặc bài kiểm tra."
    },
    {
      icon: <Users className="w-6 h-6 text-emerald-600" />,
      title: "Hệ thống Review & Phản biện",
      desc: "Tổ chức các buổi bảo vệ dự án (Milestones). Sử dụng ngân hàng câu hỏi (Question Bank) chuyên biệt để đặt câu hỏi phản biện trực tiếp cho từng nhóm."
    },
    {
      icon: <TrendingUp className="w-6 h-6 text-amber-600" />,
      title: "Chấm điểm & Thống kê tiến độ",
      desc: "Hệ thống hóa các đầu điểm chấm kèm nhận xét chi tiết. Xem biểu đồ thống kê trực quan về tiến trình học tập, tỷ lệ hoàn bài của cả lớp học."
    }
  ];

  const studentFeatures = [
    {
      icon: <BookOpen className="w-6 h-6 text-indigo-600" />,
      title: "Học tập chủ động tại nhà",
      desc: "Truy cập kho bài giảng, video clip chuẩn bị trước của giảng viên. Tự học lý thuyết theo tiến trình cá nhân và đánh dấu hoàn thành bài học."
    },
    {
      icon: <ClipboardList className="w-6 h-6 text-violet-600" />,
      title: "Mệt mài luyện tập & Thực hành",
      desc: "Thực hiện các nhiệm vụ tự học dưới dạng câu trả lời ngắn, trắc nghiệm nhanh hoặc nộp file đồ án đúng hạn ngay trên nền tảng điện toán."
    },
    {
      icon: <MessageSquare className="w-6 h-6 text-blue-600" />,
      title: "Tương tác & Hoạt động trên lớp",
      desc: "Tham gia giải đáp thắc mắc, trò chơi định hướng tư duy trực tiếp cùng giảng viên và thảo luận nhóm sâu thay vì chỉ nghe thuyết trình thụ động."
    },
    {
      icon: <Rocket className="w-6 h-6 text-emerald-600" />,
      title: "Báo cáo tiến trình dự án",
      desc: "Tham gia dự án theo sự dẫn dắt của cố vấn, nộp báo cáo Milestone định kỳ và theo dõi trực quan các cột mốc thời gian nhiệm vụ sắp tới."
    },
    {
      icon: <Award className="w-6 h-6 text-amber-600" />,
      title: "Nhận góp ý & Điểm số đa chiều",
      desc: "Nhận đánh giá chi tiết từ giảng viên và các bạn bè phản biện môn học. Tra cứu lịch sử kết quả học tập và nhận xét khách quan bất cứ lúc nào."
    }
  ];

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 font-sans flex flex-col selection:bg-indigo-500 selection:text-white">
      {/* Navigation Header */}
      <header className="sticky top-0 z-40 bg-white/85 backdrop-blur-md border-b border-slate-100 px-6 py-4 transition-all">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center space-x-3 cursor-pointer" onClick={() => navigate('/')}>
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-indigo-400 flex items-center justify-center shadow-md shadow-indigo-200">
              <BookOpen className="w-6 h-6 text-white" />
            </div>
            <div>
              <span className="text-xl font-extrabold bg-gradient-to-r from-indigo-700 to-indigo-500 bg-clip-text text-transparent">
                FlippedClass
              </span>
              <span className="text-[10px] font-semibold text-indigo-600 bg-indigo-50 px-1.5 py-0.5 rounded ml-2 uppercase tracking-wide">
                Lớp học đảo ngược
              </span>
            </div>
          </div>

          <div className="flex items-center space-x-3">
            {user ? (
              <>
                <span className="text-sm text-slate-600 hidden md:inline">
                  Chào, <strong className="text-indigo-600">{user.name}</strong>
                </span>
                <button 
                  onClick={() => navigate('/dashboard')}
                  className="px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm transition-all duration-200 shadow-lg shadow-indigo-100 flex items-center gap-1.5"
                >
                  Vào Dashboard <ArrowRight className="w-4 h-4" />
                </button>
              </>
            ) : (
              <>
                <button 
                  onClick={() => navigate('/login')}
                  className="px-5 py-2.5 rounded-xl text-slate-600 hover:text-indigo-600 font-medium text-sm transition-colors"
                >
                  Đăng nhập
                </button>
                <button 
                  onClick={() => {}}
                  className="px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm transition-all duration-200 shadow-lg shadow-indigo-100"
                >
                  Đăng ký
                </button>
              </>
            )}
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="px-6 pt-16 pb-20 bg-gradient-to-b from-indigo-50/50 via-white to-slate-50 relative overflow-hidden">
        {/* Background decorations */}
        <div className="absolute top-20 right-[-10%] w-[500px] h-[500px] bg-indigo-200/20 rounded-full filter blur-3xl -z-10 animate-pulse"></div>
        <div className="absolute bottom-10 left-[-10%] w-[400px] h-[400px] bg-violet-200/20 rounded-full filter blur-3xl -z-10"></div>

        <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          <div className="lg:col-span-7 space-y-6 text-center lg:text-left">
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-indigo-50 text-indigo-700 text-xs font-semibold border border-indigo-100">
              <Sparkles className="w-3.5 h-3.5" /> Mô hình Giáo dục Tiên tiến thế kỷ 21
            </div>
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black text-slate-900 tracking-tight leading-tight">
              Tối ưu khả năng tiếp thu với <br className="hidden sm:inline" />
              <span className="bg-gradient-to-r from-indigo-600 via-indigo-500 to-violet-600 bg-clip-text text-transparent">Lớp Học Đảo Ngược</span>
            </h1>
            <p className="text-lg text-slate-600 max-w-2xl mx-auto lg:mx-0 leading-relaxed font-light">
              Cách mạng hóa trải nghiệm giảng dạy và học tập. Sinh viên <strong>chủ động nghiên cứu bài học trước tại nhà</strong> qua tài liệu chuẩn hoá, dành trọn vẹn <strong>thời gian trên lớp để thực hành, thảo luận</strong> và đột phá dự án cùng giảng viên.
            </p>
            <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-4">
              <button 
                onClick={handleStart}
                className="w-full sm:w-auto px-8 py-4 rounded-2xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-base transition-all duration-200 shadow-xl shadow-indigo-100 flex items-center justify-center gap-2"
              >
                Bắt đầu trải nghiệm <ArrowRight className="w-5 h-5" />
              </button>
            </div>
          </div>

          {/* Visual Showcase (Styled Mockup of Flipped Concept) */}
          <div className="lg:col-span-5 relative">
            <div className="bg-white rounded-3xl p-6 shadow-2xl border border-slate-100 relative z-10">
              {/* Concept chart */}
              <div className="space-y-4">
                <div className="flex items-center justify-between border-b border-slate-50 pb-3 mb-2">
                  <span className="font-bold text-sm text-slate-700">Quy trình Flipped Classroom</span>
                </div>
                
                {/* At home */}
                <div className="p-4 rounded-2xl bg-indigo-50/50 border border-indigo-100/50">
                  <h4 className="font-bold text-sm text-slate-800 flex items-center gap-2">
                    <span className="w-5 h-5 rounded-md bg-indigo-100 text-indigo-600 flex items-center justify-center text-xs">A</span>
                    Tự học lý thuyết chủ động
                  </h4>
                  <ul className="mt-2 text-xs text-slate-500 space-y-1 ml-7 list-disc">
                    <li>Xem slide, video clip trực quan của giảng viên</li>
                    <li>Làm câu hỏi quick-test tự đánh giá ban đầu</li>
                  </ul>
                </div>

                {/* Transition Line */}
                <div className="flex justify-center my-[-8px]">
                  <div className="py-2 px-1 border-l-2 border-dashed border-indigo-300"></div>
                </div>

                {/* In class */}
                <div className="p-4 rounded-2xl bg-violet-50/50 border border-violet-100/50">
                  <h4 className="font-bold text-sm text-slate-800 flex items-center gap-2">
                    <span className="w-5 h-5 rounded-md bg-violet-100 text-violet-600 flex items-center justify-center text-xs">B</span>
                    Thực hành chuyên sâu & Thảo luận
                  </h4>
                  <ul className="mt-2 text-xs text-slate-500 space-y-1 ml-7 list-disc">
                    <li>Bảo vệ dự án, phản biện nhóm đa chiều</li>
                    <li>Giải đáp trực tiếp thắc mắc khó cùng Mentor</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Explanation Section */}
      <section id="what-is-flipped" className="py-20 px-6 bg-white border-y border-slate-100">
        <div className="max-w-4xl mx-auto text-center space-y-6">
          <h2 className="text-3xl font-bold text-slate-900 tracking-tight">
            Lớp học Đảo ngược hoạt động thế nào?
          </h2>
          <p className="text-slate-600 leading-relaxed font-light text-base max-w-3xl mx-auto">
            Trái ngược với giáo dục truyền thống nơi học sinh nghe giảng mệt mỏi ở trường và loay hoay làm bài tập về nhà một mình, 
            <strong> Flipped Classroom</strong> chuyển dịch việc truyền thụ kiến thức cơ bản ra ngoài quỹ thời gian lên lớp (thông qua học liệu online số hóa). 
            Thời gian tương tác quý giá ở giảng đường sẽ được tối ưu cho các hoạt động thảo luận nâng cao, nghiên cứu case study, 
            phản biện bảo vệ cột mốc dự án (Milestones) và nhận hỗ trợ sát sườn nhất từ đội ngũ Giảng viên.
          </p>
        </div>
      </section>

      {/* Feature Role Tabs Section */}
      <section className="py-20 px-6 bg-slate-50">
        <div className="max-w-7xl mx-auto space-y-12">
          <div className="text-center space-y-4">
            <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
              Khám Phá Các Tính Năng Cốt Lõi
            </h2>
            <p className="text-slate-500 max-w-2xl mx-auto font-light">
              Hệ thống được thiết kế hoàn thiện tối ưu cho cả trải nghiệm của Giảng viên tổ chức lớp học và Sinh viên chủ động học tập.
            </p>

            {/* Tabs for choosing roles */}
            <div className="inline-flex p-1 bg-slate-200/70 border border-slate-300/20 rounded-2xl mt-4">
              <button
                onClick={() => setActiveTab('instructor')}
                className={`px-6 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                  activeTab === 'instructor' 
                    ? 'bg-white text-indigo-600 shadow-sm' 
                    : 'text-slate-600 hover:text-slate-800'
                }`}
              >
                Cho Giảng viên / Trợ giảng
              </button>
              <button
                onClick={() => setActiveTab('student')}
                className={`px-6 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                  activeTab === 'student' 
                    ? 'bg-white text-indigo-600 shadow-sm' 
                    : 'text-slate-600 hover:text-slate-800'
                }`}
              >
                Cho Học sinh / Sinh viên
              </button>
            </div>
          </div>

          {/* Features Presentation list */}
          <div className="max-w-4xl mx-auto">
            <div className="grid grid-cols-1 md:grid-cols-1 gap-6">
              {(activeTab === 'instructor' ? instructorFeatures : studentFeatures).map((feat, idx) => (
                <div 
                  key={idx}
                  className="bg-white p-6 rounded-2xl border border-slate-100 flex flex-col sm:flex-row items-start gap-4 hover:shadow-lg transition-all duration-250 hover:border-slate-200"
                >
                  <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100 shrink-0">
                    {feat.icon}
                  </div>
                  <div className="space-y-1.5">
                    <h3 className="text-lg font-bold text-slate-900">{feat.title}</h3>
                    <p className="text-sm text-slate-500 leading-relaxed font-light">{feat.desc}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Call to action */}
      <section className="bg-slate-900 text-white py-16 px-6 relative overflow-hidden">
        <div className="absolute top-[-50%] left-[-10%] w-[350px] h-[350px] bg-indigo-700/30 rounded-full filter blur-3xl"></div>
        <div className="absolute bottom-[-50%] right-[-10%] w-[350px] h-[350px] bg-violet-700/30 rounded-full filter blur-3xl"></div>

        <div className="max-w-4xl mx-auto text-center space-y-6 relative z-10">
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight">
            Sẵn sàng nâng tầm phương pháp học mới?
          </h2>
          <p className="text-indigo-200 font-light max-w-xl mx-auto text-base">
            Gia nhập hệ thống Flipped Classroom ngay hôm nay. Trải nghiệm phương pháp học tập kết quả cao, hiện đại và bám sát dự án thực chiến.
          </p>
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-3">
            <button 
              onClick={() => navigate('/login')}
              className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-white text-slate-900 font-bold hover:bg-slate-100 transition-colors shadow-lg"
            >
              Đăng nhập ngay
            </button>
            <button 
              onClick={() => {}}
              className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-indigo-600 text-white font-bold hover:bg-indigo-700 border border-indigo-500/30 transition-colors shadow-lg"
            >
              Đăng ký tài khoản
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-slate-950 text-slate-500 py-12 px-6 mt-auto border-t border-slate-900">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center text-white">
              <BookOpen className="w-4 h-4" />
            </div>
            <span className="font-bold text-white text-base">FlippedClass</span>
          </div>
          <div className="text-xs font-light text-slate-600 text-center md:text-right">
            &copy; 2026 Flipped Classroom Management System.
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;
