import React from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { Bell, HelpCircle } from 'lucide-react';

const Navbar = ({ title }) => {
  const { user } = useAuth();
  
  return (
    <header className="h-14 bg-white border-b border-slate-200 px-8 flex items-center justify-end sticky top-0 z-10">
      <div className="flex items-center gap-6">
        <div className="flex items-center gap-4 text-slate-400">
          <HelpCircle className="w-4 h-4 cursor-pointer hover:text-slate-600 transition-colors" />
          <Bell className="w-4 h-4 cursor-pointer hover:text-slate-600 transition-colors" />
        </div>
        
        <div className="h-6 w-px bg-slate-200" />
        
        <div className="flex items-center gap-3 group cursor-pointer">
          <div className="text-right">
            <p className="text-[10px] font-bold text-slate-400 uppercase tracking-tight leading-none">{user?.role || 'Guest'}</p>
            <p className="text-xs font-bold text-slate-900 mt-1">{user?.name || 'User'}</p>
          </div>
          <div className="w-8 h-8 rounded-full bg-indigo-50 border border-indigo-100 flex items-center justify-center text-indigo-600 font-bold text-xs ring-2 ring-transparent group-hover:ring-indigo-100 transition-all">
            {user?.name?.charAt(0) || 'U'}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
