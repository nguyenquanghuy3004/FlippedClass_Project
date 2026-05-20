import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  BookOpen,
  School, 
  Map, 
  ClipboardList, 
  GraduationCap, 
  Users, 
  Settings, 
  LogOut,
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext.jsx';
import { cn } from '../../lib/utils.js';

const Sidebar = () => {
  const { user, logout } = useAuth();
  
  const menuItems = {
    Instructor: [
      { id: 'dashboard', label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
      { id: 'subjects', label: 'Môn học', path: '/subjects', icon: BookOpen },
      { id: 'roadmaps', label: 'Roadmaps', path: '/roadmaps', icon: Map },
      { id: 'evaluation', label: 'Đánh giá học viên', path: '/evaluation', icon: GraduationCap },
    ],
    Student: [],
    Admin: []
  };

  const currentMenu = menuItems[user?.role] || [];

  return (
    <aside className="w-64 bg-[#F7F7F5] border-r border-slate-200 h-screen flex flex-col sticky top-0">
      <div className="p-6 flex items-center gap-2">
        <span className="font-bold tracking-tight text-slate-900 text-lg">Flipped Classroom</span>
      </div>

      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        <div className="text-[10px] uppercase tracking-wider text-slate-400 font-bold px-3 mb-4">
          {user?.role} Menu
        </div>
        
        {currentMenu.map((item) => (
          <NavLink
            key={item.id}
            to={item.path}
            className={({ isActive }) => cn(
              "flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-all group",
              isActive 
                ? "bg-white text-slate-900 shadow-sm ring-1 ring-slate-200" 
                : "text-slate-600 hover:bg-white hover:text-slate-900"
            )}
          >
            <item.icon className={cn(
              "w-4 h-4 mr-1 flex-shrink-0",
              "group-aria-[current=page]:text-indigo-500"
            )} />
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-slate-200">
        <div className="flex items-center gap-3 px-1 py-1 mb-4">
          <div className="w-8 h-8 rounded-full bg-slate-300 flex items-center justify-center font-semibold text-slate-600 text-xs">
            {user?.name?.charAt(0)}
          </div>
          <div className="flex-1 overflow-hidden">
            <p className="text-xs font-semibold text-slate-900 truncate">{user?.name}</p>
            <p className="text-[10px] text-slate-500 truncate">{user?.role}</p>
          </div>
        </div>
        
        <button 
          onClick={logout}
          className="flex items-center gap-3 w-full px-3 py-2 text-xs font-medium text-slate-500 hover:text-red-600 hover:bg-white rounded-md border border-transparent hover:border-slate-200 transition-all"
        >
          <LogOut className="w-4 h-4" />
          <span>Đăng xuất</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
