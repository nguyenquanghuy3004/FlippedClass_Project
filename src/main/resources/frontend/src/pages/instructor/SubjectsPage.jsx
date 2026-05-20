import React, { useState, useEffect } from 'react';
import { 
  Search, 
  Filter, 
  BookOpen, 
  FileText, 
  Download, 
  MoreVertical, 
  Plus, 
  ChevronRight,
  Clock,
  User,
  Tag
} from 'lucide-react';
import { cn } from '../../lib/utils.js';
import { learningSpaceService } from '../../services/api.js';

const CATEGORIES = ['Tất cả', 'Cơ sở ngành', 'Chuyên ngành', 'Đại cương', 'Tự chọn'];

const SubjectsPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('Tất cả');
  const [subjects, setSubjects] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSubjects = async () => {
      try {
        const data = await learningSpaceService.getAll();
        // Transform backend learning spaces into the UI model
        const transformed = data.map(s => ({
          id: s.id,
          code: s.inviteCode,
          name: s.name,
          category: 'Chuyên ngành', // Default for now
          instructor: s.ownerUsername,
          semester: 'Học kỳ hiện tại',
          documents: 0,
          description: s.description || 'Chưa có mô tả.',
          tags: [s.visibility]
        }));
        setSubjects(transformed);
      } catch (err) {
        console.error("Failed to fetch subjects", err);
      } finally {
        setLoading(false);
      }
    };

    fetchSubjects();
  }, []);

  const filteredSubjects = subjects.filter(subject => {
    const matchesSearch = 
      subject.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
      subject.code.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'Tất cả' || subject.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="h-full flex flex-col gap-6 animate-in fade-in duration-500">
      {/* Header Section */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div>
            <h2 className="text-2xl font-bold text-slate-900 tracking-tight">Quản lý Môn học</h2>
            <p className="text-sm text-slate-500 font-medium italic mt-1">Tra cứu thông tin và tài liệu học trình</p>
          </div>
          
          <div className="flex flex-col sm:flex-row gap-4">
            {/* Search */}
            <div className="relative group">
              <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-indigo-500 transition-colors" />
              <input 
                type="text" 
                placeholder="Tìm mã môn, tên môn..." 
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm w-full sm:w-64 outline-none focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-300 transition-all font-medium"
              />
            </div>
            
            {/* Add Button */}
            <button className="flex items-center justify-center gap-2 px-6 py-2.5 bg-indigo-600 text-white rounded-xl text-sm font-bold shadow-lg shadow-indigo-100 hover:bg-indigo-700 transition-all">
              <Plus className="w-4 h-4" />
              <span>Thêm môn học</span>
            </button>
          </div>
        </div>

        {/* Filter Chips */}
        <div className="flex flex-wrap gap-2 mt-6 pt-6 border-t border-slate-100">
          <div className="flex items-center gap-2 mr-2 text-slate-400">
            <Filter className="w-3.5 h-3.5" />
            <span className="text-[10px] font-bold uppercase tracking-widest">Lọc theo:</span>
          </div>
          {CATEGORIES.map((category) => (
            <button
              key={category}
              onClick={() => setSelectedCategory(category)}
              className={cn(
                "px-4 py-1.5 rounded-full text-xs font-bold transition-all border",
                selectedCategory === category 
                  ? "bg-indigo-50 border-indigo-200 text-indigo-600" 
                  : "bg-white border-slate-200 text-slate-500 hover:border-slate-300 hover:bg-slate-50"
              )}
            >
              {category}
            </button>
          ))}
        </div>
      </div>

      {/* Subjects Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-2 gap-6 pb-6">
        {filteredSubjects.map((subject) => (
          <div 
            key={subject.id} 
            className="group bg-white rounded-2xl border border-slate-200 shadow-sm hover:shadow-md hover:border-indigo-100 transition-all overflow-hidden flex flex-col"
          >
            <div className="p-6 flex-1">
              <div className="flex justify-between items-start mb-4">
                <div className="px-2 py-1 bg-indigo-50 text-indigo-600 rounded text-[10px] font-bold tracking-wider">
                  {subject.code}
                </div>
                <button className="p-1.5 text-slate-300 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-all">
                  <MoreVertical className="w-4 h-4" />
                </button>
              </div>
              
              <h3 className="text-lg font-bold text-slate-900 group-hover:text-indigo-600 transition-colors mb-2">
                {subject.name}
              </h3>
              
              <p className="text-xs text-slate-500 leading-relaxed line-clamp-2 mb-6 italic">
                {subject.description}
              </p>

              <div className="grid grid-cols-2 gap-4">
                <div className="flex items-center gap-2">
                  <div className="p-2 bg-slate-50 rounded-lg">
                    <User className="w-3.5 h-3.5 text-slate-400" />
                  </div>
                  <div>
                    <p className="text-[9px] font-bold text-slate-400 uppercase tracking-tighter">Giảng viên</p>
                    <p className="text-xs font-bold text-slate-700">{subject.instructor}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <div className="p-2 bg-slate-50 rounded-lg">
                    <Tag className="w-3.5 h-3.5 text-slate-400" />
                  </div>
                  <div>
                    <p className="text-[9px] font-bold text-slate-400 uppercase tracking-tighter">Loại môn</p>
                    <p className="text-xs font-bold text-slate-700">{subject.category}</p>
                  </div>
                </div>
              </div>

              <div className="mt-6 flex flex-wrap gap-1.5">
                {subject.tags.map(tag => (
                  <span key={tag} className="px-2 py-0.5 bg-slate-50 text-slate-400 border border-slate-100 rounded text-[9px] font-bold uppercase tracking-tight">
                    #{tag}
                  </span>
                ))}
              </div>
            </div>

            {/* Bottom Actions */}
            <div className="px-6 py-4 bg-[#FBFBFA] border-t border-slate-100 flex items-center justify-between">
              <div className="flex items-center gap-2 text-slate-500">
                <FileText className="w-3.5 h-3.5" />
                <span className="text-xs font-bold">{subject.documents} tài liệu</span>
              </div>
              <button className="flex items-center gap-1.5 text-xs font-bold text-indigo-600 hover:text-indigo-700 transition-colors">
                <span>Chi tiết môn học</span>
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        ))}

        {filteredSubjects.length === 0 && (
          <div className="col-span-full py-20 flex flex-col items-center justify-center text-slate-400">
            <BookOpen className="w-12 h-12 mb-4 opacity-20" />
            <p className="text-sm font-medium italic">Không tìm thấy môn học nào phù hợp</p>
          </div>
        )}
      </div>

      {/* Featured Documents Section - Sticky Style */}
      <div className="mt-4">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-6 border-b border-slate-100 pb-2">Tài liệu mới nhất</h3>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 pb-10">
          {[1,2,3].map(i => (
            <div key={i} className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between group hover:border-indigo-100 transition-all">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-indigo-50 text-indigo-600 rounded-lg flex items-center justify-center font-bold text-sm">
                  PDF
                </div>
                <div>
                  <p className="text-sm font-bold text-slate-800 line-clamp-1">Hướng dẫn thực hành Chapter {i}</p>
                  <div className="flex items-center gap-2 mt-0.5">
                    <Clock className="w-3 h-3 text-slate-300" />
                    <span className="text-[10px] text-slate-400 font-medium">Hôm qua, 14:20</span>
                  </div>
                </div>
              </div>
              <button className="p-2 text-slate-300 hover:text-indigo-600 transition-colors">
                <Download className="w-4 h-4" />
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default SubjectsPage;
