import React, { useState, useCallback } from 'react';
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
  MoreVertical
} from 'lucide-react';
import { cn } from '../../lib/utils.js';

// Custom Node Component
const RoadmapNode = ({ data, id }) => {
  const Icon = data.type === 'learning' ? BookOpen : 
               data.type === 'quiz' ? HelpCircle :
               data.type === 'assignment' ? FileText : Flag;

  return (
    <div className="relative group">
      <Handle type="target" position={Position.Top} className="!bg-slate-300 w-3 h-3 border-2 border-white" />
      <div className={cn(
        "roadmap-node w-64 shadow-lg border-2 transition-all",
        data.locked ? "bg-slate-50 border-slate-200" : "bg-white border-slate-100",
        "hover:border-brand-primary"
      )}>
        <div className="flex items-start justify-between mb-2">
          <div className={cn(
            "p-2 rounded-lg",
            data.type === 'learning' ? 'bg-blue-50 text-blue-600' : 
            data.type === 'quiz' ? 'bg-amber-50 text-amber-600' :
            data.type === 'assignment' ? 'bg-emerald-50 text-emerald-600' : 'bg-purple-50 text-purple-600'
          )}>
            <Icon className="w-5 h-5" />
          </div>
          <button className="p-1 hover:bg-slate-100 rounded-md transition-colors opacity-0 group-hover:opacity-100">
            <MoreVertical className="w-4 h-4 text-slate-400" />
          </button>
        </div>
        
        <h4 className="text-sm font-bold text-slate-900 mb-1">{data.label}</h4>
        <p className="text-[10px] text-slate-500 line-clamp-2 italic leading-relaxed">{data.description}</p>
        
        <div className="mt-3 pt-3 border-t border-slate-50 flex items-center justify-between">
          <span className="text-[9px] font-bold text-slate-400 uppercase tracking-widest">{data.type}</span>
          <div className="flex gap-2">
             {data.locked ? <Lock className="w-3 h-3 text-slate-400" /> : <Unlock className="w-3 h-3 text-emerald-500" />}
          </div>
        </div>
      </div>
      <Handle type="source" position={Position.Bottom} className="!bg-slate-300 w-3 h-3 border-2 border-white" />
    </div>
  );
};

const nodeTypes = {
  roadmap: RoadmapNode,
};

const initialNodes = [
  { 
    id: '1', 
    type: 'roadmap', 
    data: { 
      label: 'Tuần 1: Giới thiệu Flipped Classroom', 
      type: 'learning',
      description: 'Lịch sử mô hình lớp học đảo ngược và vai trò của người dạy.',
      locked: false 
    }, 
    position: { x: 400, y: 50 } 
  },
  { 
    id: '2', 
    type: 'roadmap', 
    data: { 
      label: 'Quiz 1: Triết lý Giáo dục', 
      type: 'quiz',
      description: 'Kiểm tra nhanh kiến thức Bloom\'s Taxonomy.',
      locked: false 
    }, 
    position: { x: 400, y: 250 } 
  },
  { 
    id: '3', 
    type: 'roadmap', 
    data: { 
      label: 'Assignment: Xây dựng Video Pre-class', 
      type: 'assignment',
      description: 'Sử dụng công cụ Edpuzzle hoặc Loom để chuẩn bị bài giảng.',
      locked: true 
    }, 
    position: { x: 400, y: 450 } 
  },
  { 
    id: '4', 
    type: 'roadmap', 
    data: { 
      label: 'Milestone 1: Đề tài Nhóm', 
      type: 'milestone',
      description: 'Submit proposal đề tài cho dự án cuối kỳ.',
      locked: true 
    }, 
    position: { x: 400, y: 650 } 
  },
];

const initialEdges = [
  { id: 'e1-2', source: '1', target: '2', animated: true, style: { strokeWidth: 2, stroke: '#64748b' } },
  { id: 'e2-3', source: '2', target: '3', style: { strokeWidth: 2, stroke: '#cbd5e1' } },
  { id: 'e3-4', source: '3', target: '4', style: { strokeWidth: 2, stroke: '#cbd5e1' } },
];

const RoadmapPage = () => {
  const [nodes, setNodes] = useState(initialNodes);
  const [edges, setEdges] = useState(initialEdges);

  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), []);

  return (
    <div className="h-full min-h-[600px] flex flex-col bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden relative">
      {/* Toolbar */}
      <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-white z-10">
        <div className="flex items-center gap-6">
          <h2 className="font-bold text-slate-800">Cấu trúc Roadmap: <span className="font-medium text-slate-400">Thiết kế bài giảng đại học (FC-Design)</span></h2>
          <div className="flex gap-2">
            <button className="flex items-center gap-1.5 px-3 py-1.5 bg-brand-primary/10 text-brand-primary rounded-lg text-xs font-bold hover:bg-brand-primary/20 transition-all">
              <Plus className="w-4 h-4" /> Thêm Node
            </button>
          </div>
        </div>
        
        <div className="flex items-center gap-3">
        </div>
      </div>

      {/* Editor Canvas */}
      <div className="flex-1 relative bg-slate-50/50">
        <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={nodeTypes}
          onConnect={onConnect}
          fitView
        >
          <Background color="#cbd5e1" gap={20} size={1} />
          <Controls className="!shadow-xl !rounded-xl !border-slate-200 overflow-hidden" />
          <MiniMap 
            nodeColor={(n) => {
              if (n.data.type === 'learning') return '#3b82f6';
              if (n.data.type === 'quiz') return '#f59e0b';
              if (n.data.type === 'assignment') return '#10b981';
              return '#a855f7';
            }}
            maskColor="rgba(241, 245, 249, 0.7)"
            style={{ borderRadius: '16px' }}
          />
        </ReactFlow>
        
        {/* Legend */}
        <div className="absolute top-4 right-4 p-4 bg-white/80 backdrop-blur-md rounded-2xl border border-slate-200 shadow-xl z-10 space-y-3">
           <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-2 italic">Chú thích loại node</p>
           <div className="space-y-2">
             {[
               { label: 'Tài liệu học tập', color: 'bg-blue-500' },
               { label: 'Kiểm tra Quiz', color: 'bg-amber-500' },
               { label: 'Bài tập nộp file', color: 'bg-emerald-500' },
               { label: 'Milestone quan trọng', color: 'bg-purple-500' },
             ].map(item => (
               <div key={item.label} className="flex items-center gap-3">
                 <div className={cn("w-2 h-2 rounded-full", item.color)}></div>
                 <span className="text-[10px] font-semibold text-slate-600 uppercase tracking-tight">{item.label}</span>
               </div>
             ))}
           </div>
        </div>
      </div>
    </div>
  );
};

export default RoadmapPage;
