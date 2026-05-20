import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { LogIn, Eye, EyeOff } from 'lucide-react';
import { authService } from '../../services/api.js';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
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

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-6">
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
            <p className="text-xs text-slate-400">Hoặc đăng nhập nhanh với</p>
            <div className="w-full">
              <button className="w-full py-2.5 px-6 border border-slate-200 rounded-xl hover:bg-slate-50 transition-colors flex items-center justify-center gap-2 font-medium text-sm text-slate-700">
                <img src="https://www.google.com/favicon.ico" className="w-4 h-4 opacity-70" alt="Google" />
                Google
              </button>
            </div>
          </div>
        </div>
      </div>
  );
};

export default Login;
