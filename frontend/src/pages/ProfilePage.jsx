import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import { 
  User, 
  Mail, 
  Phone, 
  Calendar, 
  Camera, 
  Save, 
  GraduationCap, 
  Compass, 
  CheckCircle2 
} from 'lucide-react';

const TRACKS = [
  'Full-stack Web Developer',
  'AI & Machine Learning Engineer',
  'Software Architect',
  'Data Analyst / Scientist',
  'Flipped Learning Specialist'
];

const ProfilePage = () => {
  const { user, login } = useAuth();
  const [profileData, setProfileData] = useState({
    name: '',
    email: '',
    studentId: '',
    phone: '',
    dob: '',
    bio: '',
    targetTrack: '',
    gpaTarget: '',
    hoursTarget: '',
    role: ''
  });
  const [isEditing, setIsEditing] = useState(false);
  const [isSaved, setIsSaved] = useState(false);

  useEffect(() => {
    if (user) {
      // Load from localStorage or fallback
      const savedProfile = localStorage.getItem(`profile_data_${user.id}`);
      if (savedProfile) {
        setProfileData(JSON.parse(savedProfile));
      } else {
        setProfileData({
          name: user.name || '',
          email: user.email || 'student@flippedclassroom.org',
          studentId: user.role === 'Student' ? 'SV2012015' : 'GV-IND102',
          phone: '0987654321',
          dob: '2004-05-15',
          bio: 'Đam mê khám phá mô hình học tập Flipped Classroom và công nghệ phần mềm mới.',
          targetTrack: TRACKS[0],
          gpaTarget: '3.6',
          hoursTarget: '12',
          role: user.role || 'Student'
        });
      }
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSave = (e) => {
    e.preventDefault();
    if (!user) return;

    // Save to localized profiles
    localStorage.setItem(`profile_data_${user.id}`, JSON.stringify(profileData));
    
    // Also update current session user info if the name has changed
    const updatedUser = {
      ...user,
      name: profileData.name,
      email: profileData.email
    };
    login(updatedUser);

    setIsSaved(true);
    setIsEditing(false);
    setTimeout(() => setIsSaved(false), 3000);
  };

  return (
    <div className="space-y-8 animate-in fade-in duration-500 max-w-5xl mx-auto pb-12">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-indigo-500 to-slate-700 rounded-3xl p-8 md:p-12 text-white shadow-lg relative overflow-hidden">
        <div className="absolute inset-0 bg-grid-white/10 opacity-30"></div>
        <div className="relative flex flex-col md:flex-row items-center gap-6">
          <div className="relative group">
            <div className="w-24 h-24 md:w-32 md:h-32 rounded-full border-4 border-white/20 bg-slate-100 flex items-center justify-center font-bold text-3xl md:text-5xl text-indigo-600 shadow-md">
              {profileData.name ? profileData.name.charAt(0).toUpperCase() : 'U'}
            </div>
            <button className="absolute bottom-1 right-1 bg-white hover:bg-slate-50 text-slate-800 p-2 rounded-full shadow-md border border-slate-100 transition-all">
              <Camera className="w-4 h-4 text-indigo-600" />
            </button>
          </div>
          
          <div className="text-center md:text-left flex-1 space-y-2">
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-3">
              <h2 className="text-2xl md:text-3xl font-extrabold tracking-tight">{profileData.name}</h2>
              <span className="px-3 py-1 bg-white/20 backdrop-blur-md border border-white/30 rounded-full text-xs font-bold uppercase tracking-wider">
                {profileData.role === 'Student' ? 'Sinh viên' : 'Giảng viên'}
              </span>
            </div>
            <p className="text-white/80 font-medium max-w-xl text-sm italic">{profileData.bio || 'Chưa viết lời giới thiệu cho bản thân'}</p>
            {profileData.role === 'Student' && (
              <div className="pt-2 flex flex-wrap gap-4 text-xs font-semibold justify-center md:justify-start text-indigo-100">
                <span className="bg-indigo-600/30 px-3 py-1.5 rounded-xl border border-indigo-400/20 flex items-center gap-1.5">
                  <Compass className="w-3.5 h-3.5" /> Lộ trình: {profileData.targetTrack}
                </span>
                <span className="bg-slate-600/30 px-3 py-1.5 rounded-xl border border-slate-400/20 flex items-center gap-1.5">
                  <GraduationCap className="w-3.5 h-3.5" /> MSSV: {profileData.studentId}
                </span>
              </div>
            )}
          </div>
        </div>
      </div>

      {isSaved && (
        <div className="p-4 bg-emerald-50 border border-emerald-100 text-emerald-700 text-sm font-semibold rounded-2xl flex items-center gap-3 animate-bounce">
          <CheckCircle2 className="w-5 h-5 text-emerald-500" />
          <span>Hồ sơ đã được lưu trữ và cập nhật thành công lên hệ thống!</span>
        </div>
      )}

      <div className="bg-white p-6 md:p-8 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4 mb-6">
              <h3 className="text-base font-bold text-slate-900 tracking-tight flex items-center gap-2">
                <User className="w-5 h-5 text-indigo-600" />
                Thông tin cá nhân
              </h3>
              <button 
                type="button"
                onClick={() => setIsEditing(!isEditing)}
                className={`px-4 py-2 border rounded-xl text-xs font-bold transition-all ${
                  isEditing 
                    ? 'border-slate-300 bg-slate-50 text-slate-600 hover:bg-slate-100' 
                    : 'border-indigo-200 text-indigo-600 hover:bg-indigo-50/50'
                }`}
              >
                {isEditing ? 'Hủy chỉnh sửa' : 'Chỉnh sửa'}
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Họ & Tên</label>
                  <input 
                    type="text"
                    name="name"
                    value={profileData.name}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-850 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none disabled:opacity-75 transition-all font-medium"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Địa chỉ Email</label>
                  <div className="relative">
                    <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                    <input 
                      type="email"
                      name="email"
                      value={profileData.email}
                      onChange={handleChange}
                      disabled={!isEditing}
                      className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-850 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none disabled:opacity-75 transition-all font-medium"
                      required
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">MSSV / Mã Giảng viên</label>
                  <input 
                    type="text"
                    name="studentId"
                    value={profileData.studentId}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-800 disabled:opacity-75 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Số điện thoại</label>
                  <div className="relative">
                    <Phone className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                    <input 
                      type="tel"
                      name="phone"
                      value={profileData.phone}
                      onChange={handleChange}
                      disabled={!isEditing}
                      className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-850 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none disabled:opacity-75 transition-all font-medium"
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Ngày sinh</label>
                  <div className="relative">
                    <Calendar className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                    <input 
                      type="date"
                      name="dob"
                      value={profileData.dob}
                      onChange={handleChange}
                      disabled={!isEditing}
                      className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-850 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none disabled:opacity-75 transition-all font-medium"
                    />
                  </div>
                </div>
                {profileData.role === 'Student' && (
                  <div>
                    <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Lộ trình Mục tiêu</label>
                    <select
                      name="targetTrack"
                      value={profileData.targetTrack}
                      onChange={handleChange}
                      disabled={!isEditing}
                      className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-850 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none disabled:opacity-75 transition-all font-medium"
                    >
                      {TRACKS.map(t => (
                        <option key={t} value={t}>{t}</option>
                      ))}
                    </select>
                  </div>
                )}
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Lời giới thiệu / Bio</label>
                <textarea 
                  name="bio"
                  value={profileData.bio}
                  onChange={handleChange}
                  disabled={!isEditing}
                  rows="4"
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-850 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none disabled:opacity-75 transition-all resize-none placeholder:italic"
                  placeholder="Hãy kể một chút về mục tiêu học tập của bạn..."
                ></textarea>
              </div>

              {profileData.role === 'Student' && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4 border-t border-slate-100">
                  <div>
                    <label className="block text-xs font-bold text-slate-400 tracking-widest uppercase mb-1">Mục tiêu điểm số (GPA)</label>
                    <div className="flex items-center gap-3">
                      <input 
                        type="number" 
                        name="gpaTarget"
                        min="0" max="4" step="0.1" 
                        value={profileData.gpaTarget}
                        onChange={handleChange}
                        disabled={!isEditing}
                        className="w-24 px-3 py-2 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-100 focus:bg-white outline-none font-mono text-center font-bold"
                      />
                      <span className="text-xs text-slate-450 font-medium italic">Thang điểm 4.0</span>
                    </div>
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-slate-400 tracking-widest uppercase mb-1">Số giờ tự học tối thiểu/tuần</label>
                    <div className="flex items-center gap-3">
                      <input 
                        type="number" 
                        name="hoursTarget"
                        min="1" max="100" 
                        value={profileData.hoursTarget}
                        onChange={handleChange}
                        disabled={!isEditing}
                        className="w-24 px-3 py-2 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-100 focus:bg-white outline-none font-mono text-center font-bold"
                      />
                      <span className="text-xs text-slate-450 font-medium italic">Tiếng / tuần</span>
                    </div>
                  </div>
                </div>
              )}

              {isEditing && (
                <div className="pt-4 flex justify-end">
                  <button 
                    type="submit"
                    className="flex items-center gap-2 px-6 py-3 bg-indigo-600 text-white rounded-xl text-sm font-bold shadow-lg shadow-indigo-100 hover:bg-indigo-700 active:scale-[0.98] transition-all cursor-pointer"
                  >
                    <Save className="w-4 h-4" />
                    Lưu hồ sơ
                  </button>
                </div>
              )}
            </form>
          </div>
      </div>
  );
};

export default ProfilePage;
