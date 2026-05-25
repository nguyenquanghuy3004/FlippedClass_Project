import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  Search, 
  HelpCircle, 
  Trash2, 
  Edit, 
  CheckCircle2, 
  BarChart, 
  Tag, 
  Filter, 
  Sliders, 
  Save 
} from 'lucide-react';

const INITIAL_QUESTIONS = [
  {
    id: 1,
    question: "Mục đích chính của video bài giảng chuẩn bị bài trước lớp (Pre-class Video) trong Flipped Classroom là gì?",
    choices: [
      "Thay thế hoàn toàn vai trò giảng dạy trực tiếp của giảng viên",
      "Giúp sinh viên tự học kiến thức cơ bản (nhận biết, thông hiểu) trước khi lên lớp",
      "Để giảng viên kiểm tra nhanh sự chuyên cần của sinh viên",
      "Làm tài liệu tham khảo không bắt buộc để ôn thi cuối kỳ"
    ],
    correctAnswer: 1, // 0-indexed
    category: "Flipped Learning 101",
    difficulty: "Trung bình"
  },
  {
    id: 2,
    question: "Theo thang đo nhận thức Bloom cải tiến, hoạt động học trực tiếp tại lớp nên ưu tiên tập trung vào các cấp độ nào?",
    choices: [
      "Nhớ (Remember) và Hiểu (Understand)",
      "Vận dụng (Apply), Phân tích (Analyze), Đánh giá (Evaluate), Sáng tạo (Create)",
      "Chỉ tập trung vào cấp độ Nhớ và Viết lý thuyết",
      "Đọc thuộc lòng các định nghĩa trong giáo trình"
    ],
    correctAnswer: 1,
    category: "Sư phạm Bloom",
    difficulty: "Khó"
  },
  {
    id: 3,
    question: "Hành động nào của giảng viên KHÔNG ĐÚNG TINH THẦN đảo ngược trong giờ học trực tiếp trên lớp?",
    choices: [
      "Dùng 80% thời gian để giảng giải lại toàn bộ nội dung video chuẩn bị bài",
      "Tổ chức thảo luận nhóm xử lý các case study tình huống thực tế",
      "Giải đáp các thắc mắc cốt lõi được ghi chép trong nhật ký học tập của sinh viên",
      "Quan sát, điều phối và phản hồi trực tiếp cho các nhóm thảo luận"
    ],
    correctAnswer: 0,
    category: "Kỹ năng điều phối",
    difficulty: "Dễ"
  },
  {
    id: 4,
    question: "Một báo cáo Nhật ký chuẩn bị bài học (Self-study Prep log) đầy đủ của sinh viên nên có nội dung gì?",
    choices: [
      "Chỉ cần điền Tên và MSSV",
      "File ghi âm tóm tắt bài đọc",
      "Thời gian đọc, nội dung then chốt tóm tắt, và các câu hỏi thắc mắc thảo luận",
      "Chụp ảnh màn hình video bài học"
    ],
    correctAnswer: 2,
    category: "Nhật ký chuẩn bị",
    difficulty: "Dễ"
  }
];

const QuestionBankPage = () => {
  const [questions, setQuestions] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('Tất cả');
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState(null);
  const [deleteConfirmId, setDeleteConfirmId] = useState(null);

  // Form State
  const [questionText, setQuestionText] = useState('');
  const [choices, setChoices] = useState(['', '', '', '']);
  const [correctAnswer, setCorrectAnswer] = useState(0);
  const [category, setCategory] = useState('Flipped Learning 101');
  const [difficulty, setDifficulty] = useState('Trung bình');

  useEffect(() => {
    const loaded = localStorage.getItem('question_bank');
    if (loaded) {
      setQuestions(JSON.parse(loaded));
    } else {
      setQuestions(INITIAL_QUESTIONS);
      localStorage.setItem('question_bank', JSON.stringify(INITIAL_QUESTIONS));
    }
  }, []);

  const saveToLocalStorage = (newQuestions) => {
    setQuestions(newQuestions);
    localStorage.setItem('question_bank', JSON.stringify(newQuestions));
  };

  const handleOpenAdd = () => {
    setEditingQuestion(null);
    setQuestionText('');
    setChoices(['', '', '', '']);
    setCorrectAnswer(0);
    setCategory('Flipped Learning 101');
    setDifficulty('Trung bình');
    setShowAddModal(true);
  };

  const handleOpenEdit = (q) => {
    setEditingQuestion(q);
    setQuestionText(q.question);
    setChoices([...q.choices]);
    setCorrectAnswer(q.correctAnswer);
    setCategory(q.category);
    setDifficulty(q.difficulty);
    setShowAddModal(true);
  };

  const handleDelete = (id) => {
    const filtered = questions.filter(q => q.id !== id);
    saveToLocalStorage(filtered);
  };

  const handleChoiceChange = (index, value) => {
    const updated = [...choices];
    updated[index] = value;
    setChoices(updated);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!questionText || choices.some(c => !c)) {
      alert('Vui lòng điền đầy đủ câu hỏi và tất cả 4 đáp án lựa chọn.');
      return;
    }

    if (editingQuestion) {
      // Edit
      const updated = questions.map(q => {
        if (q.id === editingQuestion.id) {
          return {
            ...q,
            question: questionText,
            choices,
            correctAnswer,
            category,
            difficulty
          };
        }
        return q;
      });
      saveToLocalStorage(updated);
    } else {
      // Add
      const newQ = {
        id: questions.length > 0 ? Math.max(...questions.map(q => q.id)) + 1 : 1,
        question: questionText,
        choices,
        correctAnswer: parseInt(correctAnswer),
        category,
        difficulty
      };
      saveToLocalStorage([...questions, newQ]);
    }

    setShowAddModal(false);
  };

  const categories = ['Tất cả', ...new Set(questions.map(q => q.category))];

  const filteredQuestions = questions.filter(q => {
    const matchesSearch = q.question.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          q.category.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'Tất cả' || q.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="h-full flex flex-col gap-6 animate-in fade-in duration-500 pb-12">
      {/* Header Widget */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col md:flex-row items-center justify-between gap-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <HelpCircle className="w-6 h-6 text-indigo-600" />
            Ngân hàng Câu hỏi trắc nghiệm
          </h2>
          <p className="text-sm text-slate-500 italic font-medium mt-1">Quản lý kho tư liệu câu hỏi phục vụ chấm bài tự động và thiết lập bài kiểm tra</p>
        </div>

        <button 
          onClick={handleOpenAdd}
          className="flex items-center gap-2 px-5 py-3 bg-indigo-600 text-white rounded-xl text-sm font-bold shadow-lg shadow-indigo-100 hover:bg-indigo-700 transition-all active:scale-[0.98]"
        >
          <Plus className="w-4 h-4" />
          <span>Thêm câu hỏi mới</span>
        </button>
      </div>

      {/* Query Bar */}
      <div className="bg-white rounded-2xl border border-slate-200 p-4 md:p-6 shadow-sm space-y-4">
        <div className="flex flex-col md:flex-row gap-4 items-center">
          <div className="relative flex-1 w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input 
              type="text"
              placeholder="Tìm câu hỏi..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 font-medium text-slate-800 transition-all"
            />
          </div>

          <div className="flex items-center gap-3 w-full md:w-auto overflow-x-auto py-1">
            <Filter className="w-4 h-4 text-slate-400 flex-shrink-0" />
            <div className="flex gap-2">
              {categories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`px-3 py-1.5 rounded-full text-xs font-bold whitespace-nowrap transition-all border ${
                    selectedCategory === cat 
                      ? 'bg-indigo-50 border-indigo-200 text-indigo-600' 
                      : 'bg-white border-slate-200 text-slate-500 hover:border-slate-350 hover:bg-slate-50'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Questions List */}
      <div className="space-y-4">
        {filteredQuestions.map((q, idx) => (
          <div key={q.id} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm relative overflow-hidden group hover:border-indigo-100 transition-all">
            <div className="flex flex-col md:flex-row items-start justify-between gap-4 border-b border-slate-50 pb-4 mb-4">
              <div className="flex items-start gap-3">
                <span className="w-6 h-6 flex-shrink-0 bg-indigo-50 text-indigo-600 rounded-lg flex items-center justify-center font-bold text-xs font-mono mt-0.5">
                  {idx + 1}
                </span>
                <div>
                  <h3 className="text-sm font-bold text-slate-850 leading-relaxed pr-8">{q.question}</h3>
                  <div className="flex flex-wrap items-center gap-3 mt-2">
                    <span className="flex items-center gap-1 bg-slate-50 border border-slate-200 text-slate-550 text-[10px] font-semibold px-2 py-0.5 rounded">
                      <Tag className="w-3 h-3 text-slate-400" />
                      {q.category}
                    </span>
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                      q.difficulty === 'Dễ' ? 'bg-emerald-50 text-emerald-600 border border-emerald-200' :
                      q.difficulty === 'Trung bình' ? 'bg-amber-50 text-amber-600 border border-amber-200' :
                      'bg-red-50 text-red-600 border border-red-200'
                    }`}>
                      {q.difficulty}
                    </span>
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2">
                {deleteConfirmId === q.id ? (
                  <div className="flex items-center gap-1.5 bg-red-50 border border-red-105 p-1.5 rounded-xl animate-in fade-in duration-200">
                    <span className="text-[10px] font-bold text-red-700">Xóa?</span>
                    <button 
                      type="button"
                      onClick={() => {
                        handleDelete(q.id);
                        setDeleteConfirmId(null);
                      }}
                      className="px-2 py-1 bg-white border border-red-200 text-red-600 rounded-lg text-[9px] font-bold hover:bg-red-50 cursor-pointer transition-colors active:scale-95"
                    >
                      Có
                    </button>
                    <button 
                      type="button"
                      onClick={() => setDeleteConfirmId(null)}
                      className="px-2 py-1 bg-white border border-slate-200 text-slate-600 rounded-lg text-[9px] font-bold hover:bg-slate-50 cursor-pointer"
                    >
                      Hủy
                    </button>
                  </div>
                ) : (
                  <>
                    <button 
                      type="button"
                      onClick={() => handleOpenEdit(q)}
                      className="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50/50 rounded-xl transition-colors border border-transparent hover:border-indigo-100 cursor-pointer"
                      title="Chỉnh sửa câu hỏi"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button 
                      type="button"
                      onClick={() => setDeleteConfirmId(q.id)}
                      className="p-2 text-slate-400 hover:text-red-650 hover:bg-red-50/50 rounded-xl transition-colors border border-transparent hover:border-red-100 cursor-pointer"
                      title="Xóa câu hỏi"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </>
                )}
              </div>
            </div>

            {/* Answer Choices Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pl-9">
              {q.choices.map((choice, cIdx) => {
                const isCorrect = cIdx === q.correctAnswer;
                return (
                  <div 
                    key={cIdx} 
                    className={`p-3 rounded-xl border text-xs font-semibold flex items-center justify-between gap-3 ${
                      isCorrect 
                        ? 'bg-emerald-50/40 border-emerald-200 text-emerald-800' 
                        : 'bg-slate-50/30 border-slate-100 text-slate-600'
                    }`}
                  >
                    <span className="leading-relaxed">
                      <strong className="font-mono text-[10px] uppercase font-bold mr-1">{String.fromCharCode(65 + cIdx)}.</strong> {choice}
                    </span>
                    {isCorrect && <CheckCircle2 className="w-4.5 h-4.5 text-emerald-500 flex-shrink-0" />}
                  </div>
                );
              })}
            </div>
          </div>
        ))}

        {filteredQuestions.length === 0 && (
          <div className="bg-white p-12 text-center border border-slate-200 rounded-2xl italic text-slate-400 text-sm">
            Không tìm thấy câu hỏi nào thỏa mãn bộ lọc.
          </div>
        )}
      </div>

      {/* Add / Edit Question Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl border border-slate-200 shadow-2xl max-w-2xl w-full p-6 md:p-8 space-y-6 max-h-[90vh] overflow-y-auto animate-in zoom-in duration-200">
            <h3 className="text-lg font-bold text-slate-900 border-b border-slate-100 pb-3">
              {editingQuestion ? 'Chỉnh sửa câu hỏi' : 'Tạo câu hỏi trắc nghiệm mới'}
            </h3>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Nội dung câu hỏi</label>
                <textarea 
                  rows="3"
                  value={questionText}
                  onChange={(e) => setQuestionText(e.target.value)}
                  className="w-full p-4 bg-slate-50 border border-slate-250 rounded-xl text-sm text-slate-800 focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none transition-all placeholder:italic leading-relaxed resize-none"
                  placeholder="Nhập câu hỏi đề kiểm tra..."
                  required
                />
              </div>

              {/* Four choices input */}
              <div className="space-y-4">
                <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest">Các đáp án lựa chọn</label>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {choices.map((choice, cIdx) => (
                    <div key={cIdx} className="space-y-1">
                      <label className="text-[10px] uppercase font-bold text-slate-400 flex items-center gap-1 ml-1 leading-none">
                        <input 
                          type="radio" 
                          name="correctAnswer" 
                          checked={correctAnswer === cIdx}
                          onChange={() => setCorrectAnswer(cIdx)}
                          className="w-3.5 h-3.5 accent-indigo-500 cursor-pointer"
                        />
                        <span>Đáp án {String.fromCharCode(65 + cIdx)} (Chọn nếu Đúng)</span>
                      </label>
                      <input 
                        type="text"
                        value={choice}
                        onChange={(e) => handleChoiceChange(cIdx, e.target.value)}
                        className={`w-full px-4 py-2.5 bg-slate-50 border rounded-xl text-xs focus:ring-2 focus:ring-indigo-100 focus:bg-white outline-none transition-all font-semibold text-slate-700 ${
                          correctAnswer === cIdx ? 'border-indigo-400' : 'border-slate-200'
                        }`}
                        placeholder={`Mô tả câu trả lời ${String.fromCharCode(65 + cIdx)}`}
                        required
                      />
                    </div>
                  ))}
                </div>
              </div>

              {/* Classification */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2">
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Chủ đề / Category</label>
                  <input 
                    type="text"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    className="w-full px-4 py-3 bg-slate-50 border border-slate-220 rounded-xl text-xs font-semibold focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none transition-all"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Độ khó</label>
                  <select 
                    value={difficulty}
                    onChange={(e) => setDifficulty(e.target.value)}
                    className="w-full px-4 py-3 bg-slate-50 border border-slate-220 rounded-xl text-xs font-semibold focus:ring-2 focus:ring-indigo-100 focus:bg-white focus:border-indigo-400 outline-none transition-all"
                  >
                    <option value="Dễ">Dễ</option>
                    <option value="Trung bình">Trung bình</option>
                    <option value="Khó">Khó</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
                <button 
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-5 py-2.5 bg-slate-100 hover:bg-slate-205 text-slate-600 rounded-xl text-xs font-bold transition-all"
                >
                  Hủy
                </button>
                <button 
                  type="submit"
                  className="flex items-center gap-2 px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold shadow-lg shadow-indigo-100 transition-all active:scale-[0.98]"
                >
                  <Save className="w-4 h-4" />
                  <span>{editingQuestion ? 'Cập nhật' : 'Thêm mới'}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default QuestionBankPage;
