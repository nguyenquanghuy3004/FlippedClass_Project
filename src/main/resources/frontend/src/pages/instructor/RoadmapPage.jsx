import React, { useState, useEffect, useCallback } from 'react';
import ReactFlow, { 
  addEdge, 
  Background, 
  Controls, 
  MiniMap,
  Handle,
  Position
} from 'reactflow';
import 'reactflow/dist/style.css';
import { 
  Plus, 
  Lock, 
  Unlock,
  BookOpen,
  HelpCircle,
  FileText,
  Flag,
  MoreVertical,
  CheckCircle2,
  Clock,
  ChevronRight,
  ArrowRight,
  Save,
  Trash2,
  Sparkles,
  Sliders,
  AlertCircle
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext.jsx';
import { cn } from '../../lib/utils.js';

// Custom Node Component for ReactFlow rendering
const RoadmapNode = ({ data, selected }) => {
  const Icon = data.type === 'learning' ? BookOpen : 
               data.type === 'quiz' ? HelpCircle :
               data.type === 'assignment' ? FileText : Flag;

  return (
    <div className="relative group">
      <Handle type="target" position={Position.Top} className="!bg-slate-300 w-3 h-3 border-2 border-white" />
      <div className={cn(
        "w-64 p-4 shadow-lg border-2 transition-all rounded-2xl bg-white",
        data.locked ? "border-slate-200 bg-slate-50/80 saturate-50" : "border-slate-150",
        data.completed ? "border-emerald-300 bg-emerald-50/20" : "",
        selected ? "border-indigo-600 ring-4 ring-indigo-100 shadow-2xl scale-[1.03]" : "",
        "hover:border-indigo-500 hover:shadow-xl hover:scale-[1.02] duration-200"
      )}>
        <div className="flex items-start justify-between mb-2">
          <div className={cn(
            "p-2 rounded-xl",
            data.completed ? 'bg-emerald-50 text-emerald-600' :
            data.type === 'learning' ? 'bg-blue-50 text-blue-600' : 
            data.type === 'quiz' ? 'bg-amber-50 text-amber-600' :
            data.type === 'assignment' ? 'bg-indigo-50 text-indigo-600' : 'bg-purple-50 text-purple-600'
          )}>
            <Icon className="w-5 h-5" />
          </div>
          
          <div className="flex items-center gap-1">
            {data.completed ? (
              <span className="text-[9px] bg-emerald-100 text-emerald-700 font-bold px-1.5 py-0.5 rounded-full font-mono uppercase">Xong</span>
            ) : data.locked ? (
              <Lock className="w-3.5 h-3.5 text-slate-400" />
            ) : (
              <Unlock className="w-3.5 h-3.5 text-indigo-500" />
            )}
          </div>
        </div>
        
        <h4 className="text-sm font-bold text-slate-900 mb-1 line-clamp-1">{data.label}</h4>
        <p className="text-[10px] text-slate-550 line-clamp-2 italic leading-normal">{data.description}</p>
        
        <div className="mt-3 pt-2.5 border-t border-slate-100 flex items-center justify-between">
          <span className="text-[9px] font-bold text-slate-400 uppercase tracking-widest">{data.type}</span>
          {data.quizScore && <span className="text-[10px] font-mono font-bold text-indigo-600">Điểm: {data.quizScore}</span>}
          {data.hours && <span className="text-[9px] text-slate-450 font-bold uppercase">{data.hours}h chuẩn bị</span>}
        </div>
      </div>
      <Handle type="source" position={Position.Bottom} className="!bg-slate-300 w-3 h-3 border-2 border-white" />
    </div>
  );
};

const nodeTypes = {
  roadmap: RoadmapNode,
};

const INITIAL_NODES = [
  { 
    id: '1', 
    type: 'roadmap', 
    data: { 
      label: 'Tuần 1: Giới thiệu Flipped Classroom', 
      type: 'learning',
      description: 'Lịch sử mô hình lớp học đảo ngược và vai trò cốt lõi của người tự học.',
      locked: false,
      completed: true,
      hours: 3,
      content: 'Hãy xem kỹ slide giới thiệu về Bloom Taxonomy và Flipped Classroom 101. Nhớ tóm tắt bài học vào Nhật ký chuẩn bị bài trước khi lên giảng đường trực tiếp.'
    }, 
    position: { x: 300, y: 50 } 
  },
  { 
    id: '2', 
    type: 'roadmap', 
    data: { 
      label: 'Quiz 1: Triết lý lớp học đảo ngược', 
      type: 'quiz',
      description: 'Kiểm tra nhanh kiến thức Bloom\'s Taxonomy từ video lý thuyết.',
      locked: false,
      completed: false,
      hours: 1,
      content: 'Bài kiểm tra trắc nghiệm 3 câu tự động lấy dữ liệu từ Ngân hàng câu hỏi. Điểm 7/10 trở lên đạt chuẩn, chấm tự động đổi màu trạng thái node.'
    }, 
    position: { x: 300, y: 240 } 
  },
  { 
    id: '3', 
    type: 'roadmap', 
    data: { 
      label: 'Assignment: Xây dựng Video Pre-class', 
      type: 'assignment',
      description: 'Sử dụng công cụ Edpuzzle hoặc Loom để chuẩn bị bài giảng đảo ngược.',
      locked: true,
      completed: false,
      hours: 6,
      content: 'Nhóm nghiên cứu tự quay video thuyết trình 5 phút lý thuyết, lồng 3 câu hỏi pop-up tương tác. Xem bảng Tiêu chí Chấm điểm độc lập bên dưới trước khi nộp.'
    }, 
    position: { x: 300, y: 430 } 
  },
  { 
    id: '4', 
    type: 'roadmap', 
    data: { 
      label: 'Milestone 2: Đề tài thuyết trình trực tiếp', 
      type: 'milestone',
      description: 'Bảo vệ bài giảng Flipped, phản biện và trả lời trực tiếp trước hội đồng.',
      locked: true,
      completed: false,
      hours: 4,
      content: 'Bảo vệ thành quả. Giảng viên cùng các thành viên lớp đặt câu hỏi phản biện chuyên sâu. Ghi nhận và chấm điểm nộp tổng quan.'
    }, 
    position: { x: 300, y: 620 } 
  },
];

const INITIAL_EDGES = [
  { id: 'e1-2', source: '1', target: '2', animated: true, style: { strokeWidth: 2, stroke: '#6366f1' } },
  { id: 'e2-3', source: '2', target: '3', style: { strokeWidth: 2, stroke: '#cbd5e1' } },
  { id: 'e3-4', source: '3', target: '4', style: { strokeWidth: 2, stroke: '#cbd5e1' } },
];

const RoadmapPage = () => {
  const { user } = useAuth();
  const isInstructor = user?.role === 'Instructor';

  // Roadmap list state
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  const [selectedNode, setSelectedNode] = useState(null);

  // Editing state (Instructor only)
  const [label, setLabel] = useState('');
  const [type, setType] = useState('learning');
  const [description, setDescription] = useState('');
  const [locked, setLocked] = useState(false);
  const [hours, setHours] = useState('2');
  const [content, setContent] = useState('');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  // Student interactions states
  const [prepTime, setPrepTime] = useState('1.5');
  const [prepSummary, setPrepSummary] = useState('');
  const [prepQuestions, setPrepQuestions] = useState('');
  const [prepConfidence, setPrepConfidence] = useState(4);
  const [prepSubmitted, setPrepSubmitted] = useState(false);

  // Student Quiz states
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const [quizFinished, setQuizFinished] = useState(false);
  const [autoGradedScore, setAutoGradedScore] = useState(0);
  const [quizQuestions, setQuizQuestions] = useState([]);

  // Assignment states
  const [assSubmited, setAssSubmitted] = useState(false);
  const [assText, setAssText] = useState('');
  const [hasGrade, setHasGrade] = useState(false);
  const [gradeDetails, setGradeDetails] = useState(null);

  // Load roadmap data
  useEffect(() => {
    const savedNodes = localStorage.getItem('roadmap_nodes');
    const savedEdges = localStorage.getItem('roadmap_edges');
    if (savedNodes && savedEdges) {
      setNodes(JSON.parse(savedNodes));
      setEdges(JSON.parse(savedEdges));
    } else {
      setNodes(INITIAL_NODES);
      setEdges(INITIAL_EDGES);
      localStorage.setItem('roadmap_nodes', JSON.stringify(INITIAL_NODES));
      localStorage.setItem('roadmap_edges', JSON.stringify(INITIAL_EDGES));
    }
  }, []);

  const saveRoadmap = (updatedNodes, updatedEdges) => {
    setNodes(updatedNodes);
    setEdges(updatedEdges);
    localStorage.setItem('roadmap_nodes', JSON.stringify(updatedNodes));
    localStorage.setItem('roadmap_edges', JSON.stringify(updatedEdges));
  };

  // Node Selection Action
  const handleNodeClick = (event, node) => {
    setSelectedNode(node);
    setShowDeleteConfirm(false);
    
    // Set form fields
    setLabel(node.data.label);
    setType(node.data.type);
    setDescription(node.data.description);
    setLocked(node.data.locked);
    setHours(node.data.hours || '2');
    setContent(node.data.content || '');

    // Reset student activity states
    setPrepSubmitted(false);
    setSelectedAnswers({});
    setQuizFinished(false);
    setAutoGradedScore(0);
    setAssSubmitted(false);
    setAssText('');
    setHasGrade(false);
    setGradeDetails(null);

    // If it is a quiz, load matching questions from Question Bank
    if (node.data.type === 'quiz') {
      const qBank = localStorage.getItem('question_bank');
      if (qBank) {
        setQuizQuestions(JSON.parse(qBank).slice(0, 3)); // pick first 3
      } else {
        // Fallback
        setQuizQuestions([
          {
            id: 1,
            question: "Mục đích chính của video bài giảng chuẩn bị bài trước lớp (Pre-class Video) là gì?",
            choices: [
              "Thay thế hoàn toàn vai trò giảng dạy trực tiếp của giảng viên",
              "Giúp sinh viên tự học lý thuyết nền tảng (nhận biết, thông hiểu) trước khi lên lớp",
              "Để giảng viên kiểm tra nhanh sự chuyên cần của sinh viên",
              "Làm tài liệu tham khảo không bắt buộc để ôn thi cuối kỳ"
            ],
            correctAnswer: 1
          },
          {
            id: 2,
            question: "Theo thang đo nhận thức Bloom cải tiến, hoạt động học trực tiếp tại lớp nên ưu tiên tập trung vào cấp độ nào?",
            choices: [
              "Nhớ (Remember) và Hiểu (Understand)",
              "Vận dụng (Apply), Phân tích (Analyze), Đánh giá (Evaluate), Sáng tạo (Create)",
              "Chỉ học lý thuyết viết giấy",
              "Đọc thuộc các định nghĩa"
            ],
            correctAnswer: 1
          }
        ]);
      }
    }

    // Checking if assignment already graded by instructor
    if (node.data.type === 'assignment') {
      const savedGrade = localStorage.getItem(`student_evaluation_${user.id}_${node.id}`);
      if (savedGrade) {
        setHasGrade(true);
        setGradeDetails(JSON.parse(savedGrade));
      }
    }
  };

  // Connect Edges
  const onConnect = useCallback((params) => {
    setEdges((eds) => {
      const updated = addEdge({ ...params, animated: true, style: { strokeWidth: 2, stroke: '#6366f1' } }, eds);
      localStorage.setItem('roadmap_edges', JSON.stringify(updated));
      return updated;
    });
  }, []);

  // Creator: Add new node
  const handleAddNode = () => {
    const newId = (nodes.length > 0 ? Math.max(...nodes.map(n => parseInt(n.id) || 0)) + 1 : 1).toString();
    const newNode = {
      id: newId,
      type: 'roadmap',
      data: {
        label: 'Tên node học liệu mới...',
        type: 'learning',
        description: 'Mô tả tóm tắt mục tiêu bài học hoặc thử thách...',
        locked: true,
        completed: false,
        hours: 2,
        content: 'Nội dung hướng dẫn ôn tập chi tiết của giáo viên.'
      },
      position: { x: 300, y: nodes.length > 0 ? Math.max(...nodes.map(n => n.position.y)) + 180 : 100 }
    };
    const updated = [...nodes, newNode];
    saveRoadmap(updated, edges);
    setSelectedNode(newNode);
    setLabel(newNode.data.label);
    setType(newNode.data.type);
    setDescription(newNode.data.description);
    setLocked(newNode.data.locked);
    setHours('2');
    setContent(newNode.data.content);
  };

  // Creator: Edit node details
  const handleUpdateNode = (e) => {
    e.preventDefault();
    if (!selectedNode) return;

    const updated = nodes.map(n => {
      if (n.id === selectedNode.id) {
        return {
          ...n,
          data: {
            ...n.data,
            label,
            type,
            description,
            locked,
            hours: parseInt(hours),
            content
          }
        };
      }
      return n;
    });
    saveRoadmap(updated, edges);
    alert('Đã cập nhật cấu trúc node lộ trình thành công!');
    setSelectedNode(null);
  };

  // Creator: Delete node
  const handleDeleteNode = () => {
    if (!selectedNode) return;
    const updatedNodes = nodes.filter(n => n.id !== selectedNode.id);
    const updatedEdges = edges.filter(e => e.source !== selectedNode.id && e.target !== selectedNode.id);
    saveRoadmap(updatedNodes, updatedEdges);
    setSelectedNode(null);
  };

  // Student: Submit pre-class self-study log (Prep Log)
  const handleSubmitPrepLog = (e) => {
    e.preventDefault();
    if (!prepSummary) {
      alert('Vui lòng ghi tóm tắt bài học.');
      return;
    }

    // Save student prep log locally
    const prepLog = {
      userId: user.id,
      userName: user.name,
      nodeId: selectedNode.id,
      nodeLabel: selectedNode.data.label,
      hoursSpent: prepTime,
      summary: prepSummary,
      questions: prepQuestions,
      confidence: prepConfidence,
      submittedAt: new Date().toISOString()
    };
    
    // Store in general queue
    const queue = localStorage.getItem('student_prep_logs') ? JSON.parse(localStorage.getItem('student_prep_logs')) : [];
    queue.push(prepLog);
    localStorage.setItem('student_prep_logs', JSON.stringify(queue));

    // Update node state to COMPLETED
    const updatedNodes = nodes.map(n => {
      if (n.id === selectedNode.id) {
        return { ...n, data: { ...n.data, completed: true } };
      }
      // Unlock next node
      if (n.id === '2') {
        return { ...n, data: { ...n.data, locked: false } };
      }
      return n;
    });
    saveRoadmap(updatedNodes, edges);
    setPrepSubmitted(true);
  };

  // Student: Choose answers in Quiz
  const handleAnswerSelect = (qId, choiceIdx) => {
    setSelectedAnswers(prev => ({
      ...prev,
      [qId]: choiceIdx
    }));
  };

  // Student: Automatic Grading execution
  const handleQuizSubmit = () => {
    if (Object.keys(selectedAnswers).length < quizQuestions.length) {
      alert('Vui lòng trả lời đầy đủ tất cả câu hỏi trước khi chấm điểm.');
      return;
    }

    // Calculate score
    let correctCount = 0;
    quizQuestions.forEach(q => {
      if (selectedAnswers[q.id] === q.correctAnswer) {
        correctCount++;
      }
    });

    const finalScore = ((correctCount / quizQuestions.length) * 10).toFixed(1);
    setAutoGradedScore(finalScore);
    setQuizFinished(true);

    const isPassed = parseFloat(finalScore) >= 7.0;

    // Update node state
    const updatedNodes = nodes.map(n => {
      if (n.id === selectedNode.id) {
        return { 
          ...n, 
          data: { 
            ...n.data, 
            completed: isPassed, 
            quizScore: finalScore 
          } 
        };
      }
      // Unlock assignment node if passed
      if (isPassed && n.id === '3') {
        return { ...n, data: { ...n.data, locked: false } };
      }
      return n;
    });
    saveRoadmap(updatedNodes, edges);
  };

  // Student: Submit assignment files
  const handleAssignmentSubmit = (e) => {
    e.preventDefault();
    if (!assText) {
      alert('Vui lòng viết lời giải hoặc link bài làm.');
      return;
    }

    // Save simulation in submissions general index
    const submission = {
      userId: user.id,
      userName: user.name,
      studentId: user.studentId || 'SV2012015',
      nodeId: selectedNode.id,
      nodeLabel: selectedNode.data.label,
      solutionText: assText,
      status: 'pending',
      submittedAt: new Date().toISOString()
    };

    const subs = localStorage.getItem('student_assignment_submissions') ? JSON.parse(localStorage.getItem('student_assignment_submissions')) : [];
    subs.push(submission);
    localStorage.setItem('student_assignment_submissions', JSON.stringify(subs));

    // Update state to completed
    const updatedNodes = nodes.map(n => {
      if (n.id === selectedNode.id) {
        return { ...n, data: { ...n.data, completed: true } };
      }
      // Unlock Milestone node
      if (n.id === '4') {
        return { ...n, data: { ...n.data, locked: false } };
      }
      return n;
    });
    saveRoadmap(updatedNodes, edges);
    setAssSubmitted(true);
  };

  return (
    <div className="h-full min-h-[680px] grid grid-cols-1 lg:grid-cols-3 gap-6 overflow-hidden bg-[#FBFBFA]">
      
      {/* LEFT SECTION: FLOW ROADMAP CANVAS */}
      <div className="lg:col-span-2 flex flex-col bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden relative">
        <div className="p-5 border-b border-slate-100 flex items-center justify-between z-10 bg-white/95 backdrop-blur-sm">
          <div>
            <h2 className="font-extrabold text-slate-900 flex items-center gap-2 text-base md:text-lg">
              <Sparkles className="w-5 h-5 text-indigo-500" />
              Sơ đồ Lộ trình Học tập (Roadmap)
            </h2>
            <p className="text-xs text-slate-450 italic font-medium leading-normal mt-0.5">Môn học: Kỹ năng làm việc nhóm đại học đảo ngược (FC-Course)</p>
          </div>

          {isInstructor && (
            <button 
              onClick={handleAddNode}
              className="flex items-center gap-1.5 px-4 py-2 bg-indigo-600 text-white rounded-xl text-xs font-bold hover:bg-indigo-700 transition-all shadow-md shadow-indigo-100 active:scale-95 cursor-pointer"
            >
              <Plus className="w-4 h-4" /> Thêm node
            </button>
          )}
        </div>

        {/* REACTFLOW ELEMENT CANVAS */}
        <div className="flex-1 min-h-[450px] relative bg-slate-50/40">
          <ReactFlow
            nodes={nodes}
            edges={edges}
            nodeTypes={nodeTypes}
            onNodeClick={handleNodeClick}
            onConnect={isInstructor ? onConnect : undefined}
            fitView
          >
            <Background color="#cbd5e1" gap={24} size={1} />
            <Controls className="!shadow-md !rounded-xl !border-slate-200 overflow-hidden" />
            <MiniMap 
              nodeColor={(n) => {
                if (n.data.type === 'learning') return '#3b82f6';
                if (n.data.type === 'quiz') return '#f59e0b';
                if (n.data.type === 'assignment') return '#10b981';
                return '#a855f7';
              }}
              maskColor="rgba(241, 245, 249, 0.5)"
              style={{ borderRadius: '16px', border: '1px solid #e2e8f0' }}
            />
          </ReactFlow>

          {/* ReactFlow canvas */}
        </div>
      </div>

      {/* RIGHT SECTION: DETAIL ACTION PANEL & SUBMISSION DRAWER */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm flex flex-col overflow-y-auto max-h-[85vh]">
        {!selectedNode ? (
          <div className="flex-1 flex flex-col items-center justify-center text-center p-6 space-y-4">
            <div className="p-4 bg-slate-50 rounded-full border border-slate-100">
              <BookOpen className="w-8 h-8 text-slate-400" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-slate-800">Chưa chọn nội dung học</h3>
              <p className="text-xs text-slate-500 italic mt-1 leading-relaxed max-w-xs">{isInstructor ? 'Nhấp chuột vào bất cứ biểu tượng Node nào trên sơ đồ thiết kế bài giảng để biên tập thông tin chuyên đề.' : 'Nhấp chuột chọn Node đang Mở khóa (Unlock) trên sơ đồ của bạn để tiến hành tự học, nộp bài, hoặc làm trắc nghiệm.'}</p>
            </div>
          </div>
        ) : (
          /* CONCRETE SELECTED NODE OPERATION PANEL */
          <div className="space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div>
                <span className="px-2 py-0.5 bg-indigo-50 border border-indigo-100 text-indigo-700 font-bold text-[9px] rounded uppercase font-mono tracking-wider">{selectedNode.data.type}</span>
                <h3 className="text-base font-black text-slate-900 leading-tight mt-1">{selectedNode.data.label}</h3>
              </div>
              <button 
                onClick={() => setSelectedNode(null)}
                className="text-xs text-slate-400 hover:text-slate-600 font-bold"
              >
                Đóng
              </button>
            </div>

            {selectedNode.data.locked && !isInstructor ? (
              <div className="p-5 border border-red-100 bg-red-50/50 rounded-2xl flex gap-3 text-red-750">
                <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-500 mt-0.5" />
                <div className="space-y-1">
                  <h4 className="text-xs font-bold font-sans">Bài học học phần đang bị khóa</h4>
                  <p className="text-[11px] leading-relaxed italic text-red-600">Bạn phải vượt qua bài trắc nghiệm hoặc chuẩn bị bài học (prep-log) ở node trước đó để tự động mở khóa học phần hiện tại.</p>
                </div>
              </div>
            ) : (
              /* PANEL CONTAINER WITH FORMS BASED ON ROLE */
              isInstructor ? (
                /* INSTRUCTOR ROADMAP BUILDER FORM */
                <form onSubmit={handleUpdateNode} className="space-y-5">
                  <h4 className="text-xs font-bold text-slate-400 uppercase tracking-widest flex items-center gap-2">
                    <Sliders className="w-4 h-4 text-indigo-500" />
                    Thiết lập thuộc tính Node
                  </h4>

                  <div>
                    <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Tên Node bài giảng</label>
                    <input 
                      type="text" 
                      value={label}
                      onChange={(e) => setLabel(e.target.value)}
                      className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:border-indigo-400 text-xs font-semibold text-slate-800"
                      required
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Loại node</label>
                      <select 
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:border-indigo-400 text-xs font-semibold text-slate-700"
                      >
                        <option value="learning">Học tài liệu (Learning)</option>
                        <option value="quiz">Trắc nghiệm (Quiz)</option>
                        <option value="assignment">Nộp bài tập (Assignment)</option>
                        <option value="milestone">Hội bảo vệ (Milestone)</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Giờ chuẩn bị (h)</label>
                      <input 
                        type="number" 
                        value={hours}
                        onChange={(e) => setHours(e.target.value)}
                        className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:border-indigo-400 text-xs font-semibold font-mono text-center text-slate-850"
                        min="1" max="100"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Mô tả tóm tắt</label>
                    <textarea 
                      rows="2"
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:border-indigo-400 text-xs text-slate-755 resize-none leading-relaxed"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Nội dung học tập tích hợp / Hướng dẫn</label>
                    <textarea 
                      rows="4"
                      value={content}
                      onChange={(e) => setContent(e.target.value)}
                      className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:bg-white focus:border-indigo-400 text-xs text-slate-755 resize-none leading-relaxed"
                    />
                  </div>

                  <div className="flex items-center gap-2 px-1 mb-2">
                    <input 
                      type="checkbox" 
                      id="nodeLocked"
                      checked={locked}
                      onChange={(e) => setLocked(e.target.checked)}
                      className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 accent-indigo-500"
                    />
                    <label htmlFor="nodeLocked" className="text-xs font-bold text-slate-600">Khóa node hãm lực (Sinh viên nhận từ từ)</label>
                  </div>

                  <div className="flex justify-between gap-3 pt-4 border-t border-slate-100 items-center">
                    {showDeleteConfirm ? (
                      <div className="flex items-center gap-2 bg-red-50 border border-red-100 p-2.5 rounded-xl w-full justify-between animate-in fade-in duration-200">
                        <span className="text-[10.5px] font-bold text-red-700">Xác nhận xóa node này?</span>
                        <div className="flex gap-2">
                          <button
                            type="button"
                            onClick={() => {
                              handleDeleteNode();
                              setShowDeleteConfirm(false);
                            }}
                            className="px-2.5 py-1.5 bg-white border border-red-200 text-red-600 hover:bg-red-50 rounded-lg text-[10px] font-bold cursor-pointer transition-colors active:scale-95"
                          >
                            Đồng ý
                          </button>
                          <button
                            type="button"
                            onClick={() => setShowDeleteConfirm(false)}
                            className="px-2.5 py-1.5 bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 rounded-lg text-[10px] font-bold cursor-pointer transition-colors"
                          >
                            Hủy
                          </button>
                        </div>
                      </div>
                    ) : (
                      <>
                        <button 
                          type="button"
                          onClick={() => setShowDeleteConfirm(true)}
                          className="px-4 py-2.5 bg-red-50 text-red-600 border border-red-100 hover:bg-red-100/50 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 active:scale-95 cursor-pointer"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                          Xóa node
                        </button>

                        <button 
                          type="submit"
                          className="px-5 py-2.5 bg-white border border-indigo-200 text-indigo-600 hover:bg-indigo-50 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 active:scale-95 shadow-sm cursor-pointer"
                        >
                          <Save className="w-3.5 h-3.5" />
                          Lưu và Cập nhật
                        </button>
                      </>
                    )}
                  </div>
                </form>
              ) : (
                /* STUDENT INTERACTION AREA WRAPPER */
                <div className="space-y-6">
                  {/* General node details guidelines */}
                  <div className="p-4 border border-indigo-100 bg-indigo-50/10 rounded-2xl space-y-2">
                    <h4 className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                      <BookOpen className="w-4 h-4 text-indigo-500" />
                      Học liệu & Hướng dẫn sinh viên
                    </h4>
                    <p className="text-xs text-slate-600 leading-relaxed font-light">{selectedNode.data.content || 'Đọc tài liệu lý thuyết nền để chuẩn bị thảo luận hoạt động nhóm tiếp theo trên giảng đường.'}</p>
                    <div className="flex justify-between pt-1 text-[9px] text-slate-400 font-bold uppercase tracking-tight">
                      <span>Cột mốc dự tính</span>
                      <span>{selectedNode.data.hours || '2'} Giờ tự thuyết</span>
                    </div>
                  </div>

                  {/* FORM RENDER BASED ON NODE TYPE FOR STUDENT */}
                  {selectedNode.data.type === 'learning' && (
                    /* STUDENT SELF STUDY LOG SUBMISSION (PREP LOG) (Requirement 8) */
                    <div className="space-y-4">
                      {prepSubmitted || selectedNode.data.completed ? (
                        <div className="p-5 bg-emerald-50 border border-emerald-150 rounded-2xl text-emerald-800 space-y-3">
                          <div className="flex items-center gap-2">
                            <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                            <h4 className="text-xs font-black font-sans uppercase">Đã gửi Nhật ký chuẩn bị bài học!</h4>
                          </div>
                          <p className="text-[11px] leading-relaxed italic text-emerald-705">Thông tin tóm tắt và câu hỏi thắc mắc của bạn đã được ghi lại thành công. Giảng viên sẽ đọc chúng để hiệu chỉnh nội dung thiết thực trên giảng đường.</p>
                        </div>
                      ) : (
                        <form onSubmit={handleSubmitPrepLog} className="space-y-4 pt-1">
                          <h4 className="text-xs font-bold text-slate-450 uppercase tracking-widest border-b border-slate-50 pb-2">Nộp nhật ký chuẩn bị bài học (PREP-LOG)</h4>
                          
                          <div className="grid grid-cols-2 gap-4">
                            <div>
                              <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1 ml-0.5">Số giờ học chuẩn bị</label>
                              <input 
                                type="number" 
                                min="0.5" max="24" step="0.5"
                                value={prepTime}
                                onChange={(e) => setPrepTime(e.target.value)}
                                className="w-full px-3 py-2 bg-slate-50 border border-slate-205 rounded-xl outline-none focus:bg-white text-xs font-bold text-center text-slate-705 font-mono"
                                required
                              />
                            </div>
                            <div>
                              <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1 ml-0.5">Mức độ tự tin (1-5)</label>
                              <input 
                                type="number" 
                                min="1" max="5"
                                value={prepConfidence}
                                onChange={(e) => setPrepConfidence(parseInt(e.target.value))}
                                className="w-full px-3 py-2 bg-slate-50 border border-slate-205 rounded-xl outline-none focus:bg-white text-xs font-bold text-center text-slate-705 font-mono"
                                required
                              />
                            </div>
                          </div>

                          <div>
                            <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1 ml-0.5">Nội dung then chốt tóm tắt</label>
                            <textarea 
                              placeholder="Trình bày ít nhất 2 dòng kiến thức cốt lõi bạn thu gom được qua video/bài đọc..."
                              rows="3"
                              value={prepSummary}
                              onChange={(e) => setPrepSummary(e.target.value)}
                              className="w-full px-4 py-2.5 bg-slate-50 border border-slate-205 rounded-xl text-xs text-slate-750 placeholder:italic leading-relaxed resize-none outline-none focus:bg-white focus:border-indigo-400"
                              required
                            />
                          </div>

                          <div>
                            <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1 ml-0.5">Câu hỏi thắc mắc / Đề xuất thảo luận</label>
                            <textarea 
                              placeholder="Có góc kiến thức nào còn mơ hồ bạn mong giảng viên chỉ bảo trực tiếp trên lớp?"
                              rows="2"
                              value={prepQuestions}
                              onChange={(e) => setPrepQuestions(e.target.value)}
                              className="w-full px-4 py-2.5 bg-slate-50 border border-slate-205 rounded-xl text-xs text-slate-750 placeholder:italic leading-relaxed resize-none outline-none focus:bg-white focus:border-indigo-400"
                            />
                          </div>

                          <button 
                            type="submit"
                            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs shadow shadow-indigo-100 flex items-center justify-center gap-1.5 active:scale-97"
                          >
                            <span>Gửi Nhật ký Chuẩn bị bài</span>
                            <ArrowRight className="w-4 h-4" />
                          </button>
                        </form>
                      )}
                    </div>
                  )}

                  {selectedNode.data.type === 'quiz' && (
                    /* STUDENT QUIZ TAKER WITH AUTO GRADING (Requirement 5 & 6) */
                    <div className="space-y-4">
                      {quizFinished || selectedNode.data.completed ? (
                        <div className="p-5 bg-indigo-50 border border-indigo-150 rounded-2xl space-y-4">
                          <div className="flex items-center gap-2">
                            <CheckCircle2 className="w-5 h-5 text-indigo-600" />
                            <h4 className="text-xs font-black font-sans uppercase">Đã hoàn thành kiểm tra trắc nghiệm!</h4>
                          </div>
                          
                          <div className="text-center p-4 bg-white/60 rounded-xl border border-indigo-100 space-y-1">
                            <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider leading-none">Chấm điểm Tự động (Auto Grade)</p>
                            <p className="text-3xl font-black text-indigo-700 font-mono">{autoGradedScore || selectedNode.data.quizScore || '10.0'} / 10.0</p>
                            <p className="text-[10px] text-emerald-600 font-bold uppercase leading-none mt-1">ĐẠT CHUẨN • ĐỒNG BỘ ROADMAP</p>
                          </div>
                        </div>
                      ) : (
                        <div className="space-y-5">
                          <div className="flex items-center justify-between border-b border-slate-50 pb-2">
                            <h4 className="text-xs font-bold text-slate-450 uppercase tracking-widest">Kiểm tra Trắc nghiệm Chuẩn hóa</h4>
                            <span className="text-[10px] font-mono text-slate-400 font-bold">Số câu: {quizQuestions.length}</span>
                          </div>

                          <div className="space-y-5 max-h-[380px] overflow-y-auto pr-1">
                            {quizQuestions.map((q, idx) => (
                              <div key={q.id} className="space-y-2 p-3 bg-slate-50 rounded-xl border border-slate-100">
                                <p className="text-xs font-bold text-slate-800 leading-relaxed">
                                  <span className="text-indigo-600 mr-1 font-mono font-black">{idx + 1}.</span> {q.question}
                                </p>
                                <div className="space-y-1.5 pl-3">
                                  {q.choices.map((choice, cIdx) => (
                                    <label key={cIdx} className="flex items-start gap-2 text-[10.5px] font-semibold text-slate-605 cursor-pointer hover:text-slate-900 group">
                                      <input 
                                        type="radio"
                                        name={`q_${q.id}`}
                                        checked={selectedAnswers[q.id] === cIdx}
                                        onChange={() => handleAnswerSelect(q.id, cIdx)}
                                        className="w-3.5 h-3.5 accent-indigo-600 shrink-0 mt-0.5"
                                      />
                                      <span>{choice}</span>
                                    </label>
                                  ))}
                                </div>
                              </div>
                            ))}
                          </div>

                          <button 
                            onClick={handleQuizSubmit}
                            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs shadow shadow-indigo-100 flex items-center justify-center gap-1.5 active:scale-97"
                          >
                            <span>Nộp bài & Chấm tự động</span>
                            <ArrowRight className="w-4 h-4" />
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {selectedNode.data.type === 'assignment' && (
                    /* STUDENT ASSIGNMENT SUBMISSION (Requirement 9 & 10) */
                    <div className="space-y-4">
                      {assSubmited || selectedNode.data.completed ? (
                        <div className="space-y-4">
                          <div className="p-5 bg-emerald-50 border border-emerald-150 rounded-2xl text-emerald-850 space-y-2">
                            <div className="flex items-center gap-2">
                              <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                              <h4 className="text-xs font-black font-sans uppercase">Bài giải của bạn đã gửi đi!</h4>
                            </div>
                            <p className="text-[11px] leading-relaxed italic text-emerald-705">Bài nộp đang chờ được rà soát. Trạng thái node lộ trình đã được đổi sang màu hoàn thiện tạm thời.</p>
                          </div>

                          {/* Render custom rubrics grade if already graded */}
                          {hasGrade ? (
                            <div className="bg-[#FBFBFA] border border-indigo-200 rounded-2xl p-5 space-y-4">
                              <div className="flex items-center justify-between">
                                <h4 className="text-[11px] font-bold text-indigo-700 uppercase tracking-widest">Đánh giá từ giảng viên (Weighted Score)</h4>
                                <span className="px-2 py-0.5 bg-indigo-600 text-white font-mono font-black text-xs rounded shadow-sm">{gradeDetails.score} / 10.0</span>
                              </div>

                              {/* Criteria weights visualization */}
                              <div className="space-y-2 pt-1">
                                <p className="text-[9px] font-bold text-slate-400 uppercase tracking-widest leading-none">Bảng điểm tiêu chí chấm độc lập</p>
                                <div className="space-y-2 divide-y divide-slate-100">
                                  {gradeDetails?.criteriaScores?.map((crit, cIdx) => (
                                    <div key={cIdx} className="justify-between items-center text-[10px] font-semibold text-slate-650 pt-2 flex">
                                      <div>
                                        <span className="text-slate-850 font-bold block leading-snug">{crit.name}</span>
                                        <span className="text-[9px] text-slate-400">Trọng số: {crit.weight}%</span>
                                      </div>
                                      <span className="font-mono font-bold text-indigo-600">Điểm: {crit.score}/10</span>
                                    </div>
                                  ))}
                                </div>
                              </div>

                              <div className="pt-3 border-t border-slate-100 space-y-1.5">
                                <h5 className="text-[9px] font-bold text-indigo-600 uppercase tracking-widest leading-none">Phản hồi & Nhận xét chi tiết</h5>
                                <p className="text-[11px] leading-relaxed text-slate-600 italic bg-white p-3 rounded-xl border border-slate-100">"{gradeDetails.feedback}"</p>
                              </div>
                            </div>
                          ) : (
                            <div className="bg-slate-50 border border-slate-200 p-4 rounded-xl text-center italic text-slate-400 text-xs">
                              Giảng viên hiện đang rà soát bài thuyết trình và slide của nhóm bạn. Điểm số sẽ được đồng bộ ngay khi hoàn tất.
                            </div>
                          )}
                        </div>
                      ) : (
                        <form onSubmit={handleAssignmentSubmit} className="space-y-4 pt-1">
                          <h4 className="text-xs font-bold text-slate-450 uppercase tracking-widest border-b border-slate-50 pb-2 font-sans">Nộp bài tập chuẩn bị dự án</h4>

                          {/* Present Rubrics structure */}
                          <div className="bg-slate-50 border border-slate-200 rounded-xl p-3.5 space-y-2.5">
                            <p className="text-[9px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 leading-none">Bảng tiêu chí thẩm định (Rubrics)</p>
                            <div className="space-y-1.5 text-[10.5px] font-semibold text-slate-600">
                              <div className="flex justify-between">
                                <span className="text-slate-700">• Độ hoàn thiện Slide</span>
                                <span>Trọng số: 30%</span>
                              </div>
                              <div className="flex justify-between">
                                <span className="text-slate-700">• Chất lượng thuyết trình video</span>
                                <span>Trọng số: 40%</span>
                              </div>
                              <div className="flex justify-between">
                                <span className="text-slate-700">• Bản tóm tắt/Phản biện</span>
                                <span>Trọng số: 30%</span>
                              </div>
                            </div>
                          </div>

                          <div>
                            <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-0.5">Lời giải / Link bài viết hoặc Slide Github</label>
                            <textarea 
                              placeholder="Trình bày giải trình, link slide hoặc link youtube video Edpuzzle của nhóm bạn..."
                              rows="4"
                              value={assText}
                              onChange={(e) => setAssText(e.target.value)}
                              className="w-full p-4 bg-slate-50 border border-slate-205 rounded-xl text-xs text-slate-750 placeholder:italic leading-relaxed resize-none outline-none focus:bg-white focus:border-indigo-400"
                              required
                            />
                          </div>

                          <button 
                            type="submit"
                            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs shadow shadow-indigo-100 flex items-center justify-center gap-1.5 active:scale-97"
                          >
                            <span>Nộp bài giải Milestone</span>
                            <ArrowRight className="w-4 h-4" />
                          </button>
                        </form>
                      )}
                    </div>
                  )}

                  {selectedNode.data.type === 'milestone' && (
                    <div className="bg-purple-50/10 border border-purple-100 p-5 rounded-2xl text-slate-655 space-y-3">
                      <h4 className="text-xs font-bold text-purple-750 uppercase tracking-wider flex items-center gap-1.5">
                        <Flag className="w-5 h-5 text-purple-600" />
                        Trình Hội đồng phản biện cuối kỳ
                      </h4>
                      <p className="text-xs leading-relaxed font-light text-slate-500">Mục này được chấm điểm thông qua hoạt động vấn đáp trực tiếp 1-1 trên lớp học đảo ngược. Giảng viên phụ trách dùng bộ câu hỏi từ Question Bank kiểm thử trình độ thực tế.</p>
                      <span className="text-[10px] text-purple-600 font-bold block bg-purple-50 px-2 py-0.5 rounded border border-purple-100 max-w-fit font-mono">Bảo vệ Trực tiếp</span>
                    </div>
                  )}
                </div>
              )
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default RoadmapPage;
