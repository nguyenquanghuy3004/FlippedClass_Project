import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { LogIn, Eye, EyeOff, ShieldCheck, Mail, User as UserIcon, Shield } from 'lucide-react';
import { authService } from '../../services/api.js';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  // Google OAuth2 States
  const [showGoogleModal, setShowGoogleModal] = useState(false);
  const [googleStep, setGoogleStep] = useState(1); // 1: Chooser, 2: Loading/Consent
  const [customEmail, setCustomEmail] = useState('captainseal21@gmail.com');
  const [customName, setCustomName] = useState('Captain Seal');
  const [customRole, setCustomRole] = useState('Student');

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const data = await authService.login(email, password);
      login(data.user);
      navigate('/dashboard');
    } catch (err) {
      setError('Thông tin đăng nhập không chính xác hoặc có lỗi xảy ra.');
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignIn = (selectedUser) => {
    setGoogleStep(2);
    setTimeout(() => {
      login(selectedUser);
      setShowGoogleModal(false);
      setGoogleStep(1);
      navigate('/dashboard');
    }, 2000); // 2-second simulation
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-6 relative">
      {/* Form Side */}
      <div className="w-full max-w-md p-8 bg-white rounded-2xl shadow-xl border border-slate-200">
        <div className="text-center mb-8">
          <h2 className="text-2xl font-bold text-slate-900">Chào mừng</h2>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-100 text-red-600 text-sm rounded-lg flex items-center gap-3">
            <span className="w-5 h-5 flex items-center justify-center bg-red-100 rounded-full text-xs font-bold font-mono">!</span>
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1.5 ml-1">Tài khoản</label>
            <div className="relative group">
              <input 
                type="text" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Username hoặc Email" 
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary transition-all text-slate-900"
                required
              />
            </div>
            <p className="text-[10px] text-slate-400 mt-1 ml-1 flex gap-2">
              <span>instructor / password123</span>
            </p>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1.5 ml-1">
              <label className="text-sm font-semibold text-slate-700">Mật khẩu</label>
              <a href="#" className="text-xs font-semibold text-brand-primary hover:underline underline-offset-4">Quên mật khẩu?</a>
            </div>
            <div className="relative group">
              <input 
                type={showPassword ? "text" : "password"} 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••" 
                className="w-full pl-4 pr-11 py-3 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-brand-primary/20 focus:border-brand-primary transition-all text-slate-900"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 focus:outline-none"
              >
                {showPassword ? (
                  <EyeOff className="w-5.5 h-5.5" />
                ) : (
                  <Eye className="w-5.5 h-5.5" />
                )}
              </button>
            </div>
          </div>

          <div className="flex items-center gap-2 mb-2 ml-1">
            <input type="checkbox" id="remember" className="w-4 h-4 rounded border-slate-300 text-brand-primary focus:ring-brand-primary" />
            <label htmlFor="remember" className="text-sm text-slate-600">Ghi nhớ đăng nhập</label>
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className="w-full py-3.5 bg-brand-primary text-white font-bold rounded-xl shadow-lg shadow-blue-200 hover:bg-brand-secondary hover:shadow-xl active:scale-[0.98] transition-all flex items-center justify-center gap-2"
          >
            {loading ? (
              <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
            ) : (
              <>
                <span>Truy cập hệ thống</span>
                <LogIn className="w-5 h-5" />
              </>
            )}
          </button>
        </form>

        <div className="mt-8 pt-8 border-t border-slate-100 flex flex-col items-center gap-4">
          <p className="text-xs text-slate-400 font-medium">Hoặc đăng nhập nhanh với</p>
          <div className="w-full">
            <button 
              type="button"
              onClick={() => setShowGoogleModal(true)}
              className="w-full py-2.5 px-6 border border-slate-200 rounded-xl hover:bg-slate-50 transition-all flex items-center justify-center gap-2.5 font-bold text-sm text-slate-700 cursor-pointer shadow-sm hover:shadow active:scale-[0.99]"
            >
              <img src="https://www.google.com/favicon.ico" className="w-4.5 h-4.5" alt="Google" />
              Google
            </button>
          </div>
        </div>
      </div>

      {/* Google OAuth2 Simulated Screen */}
      {showGoogleModal && (
        <div className="fixed inset-0 bg-slate-50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl border border-slate-200 shadow-2xl max-w-md w-full p-6 md:p-8 space-y-6 animate-in zoom-in-95 duration-200">
            {/* Header branding */}
            <div className="flex flex-col items-center text-center">
              <img src="https://upload.wikimedia.org/wikipedia/commons/c/c1/Google_Chrome_Logo_%282022%29.svg" className="w-8 h-8 mb-2" alt="Google" onError={(e) => { e.target.src = 'https://www.google.com/favicon.ico' }} />
              <h3 className="text-lg font-bold text-slate-800">Đăng nhập tài khoản Google</h3>
              <p className="text-xs text-slate-400 mt-1 font-medium italic">để tiếp tục truy cập lớp học FlippedClass</p>
            </div>

            {googleStep === 1 ? (
              <div className="space-y-4">
                <p className="text-xs text-slate-450 font-bold uppercase tracking-wider mb-2">Chọn tài khoản của bạn</p>
                 
                {/* Option 1: Student */}
                <button 
                  onClick={() => handleGoogleSignIn({
                    id: 10,
                    name: 'Student',
                    email: 'student_google@gmail.com',
                    role: 'Student',
                    token: 'google-oauth2-mock-student-jwt'
                  })}
                  className="w-full border border-slate-200 p-4 rounded-2xl flex items-center gap-3 hover:bg-slate-50 transition-all text-left group cursor-pointer"
                >
                  <div className="w-10 h-10 rounded-full bg-slate-150 border border-slate-200 flex items-center justify-center text-slate-600 font-bold">
                    S
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-bold text-slate-800 group-hover:text-indigo-600 transition-colors">Student</p>
                    <p className="text-xs text-slate-500 font-mono text-ellipsis overflow-hidden">student_google@gmail.com</p>
                  </div>
                </button>
 
                {/* Option 2: Instructor */}
                <button 
                  onClick={() => handleGoogleSignIn({
                    id: 11,
                    name: 'Instructor',
                    email: 'instructor_google@gmail.com',
                    role: 'Instructor',
                    token: 'google-oauth2-mock-instructor-jwt'
                  })}
                  className="w-full border border-slate-200 p-4 rounded-xl flex items-center gap-3 hover:bg-slate-50 transition-all text-left group cursor-pointer"
                >
                  <div className="w-10 h-10 rounded-full bg-slate-150 border border-slate-200 flex items-center justify-center text-slate-600 font-bold">
                    I
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-bold text-slate-800 group-hover:text-brand-primary transition-colors">Instructor</p>
                    <p className="text-xs text-slate-500 font-mono text-ellipsis overflow-hidden">instructor_google@gmail.com</p>
                  </div>
                </button>
              </div>
            ) : (
              <div className="flex flex-col items-center py-8 space-y-4">
                <div className="relative flex items-center justify-center">
                  <div className="w-16 h-16 border-4 border-indigo-600/30 border-t-indigo-650 rounded-full animate-spin"></div>
                  <Shield className="w-6 h-6 text-indigo-650 absolute" />
                </div>
                <div className="text-center space-y-1">
                  <p className="text-sm font-bold text-slate-800">Đang khởi tạo OAuth2 Handshake...</p>
                  <p className="text-xs text-slate-400 font-semibold italic">Mã hóa kết nối, xác thực phạm vi email, profile, OpenID</p>
                </div>
              </div>
            )}

            <div className="flex justify-center pt-2 w-full">
              <button 
                type="button"
                onClick={() => setShowGoogleModal(false)}
                className="w-full py-2.5 bg-slate-100 hover:bg-slate-200 border border-slate-200 text-slate-705 font-bold text-xs rounded-xl shadow-xs transition-colors active:scale-[0.98] text-center"
              >
                Hủy đăng nhập Google
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
