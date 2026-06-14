package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.StudentProgressDTO;
import com.example.flippedclass.entity.LearningNode;
import com.example.flippedclass.entity.NodeProgress;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.enums.ProgressStatus;
import com.example.flippedclass.repository.LearningNodeRepository;
import com.example.flippedclass.repository.NodeProgressRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.NodeProgressService;
import com.example.flippedclass.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NodeProgressServiceImpl implements NodeProgressService {

    @Autowired
    private NodeProgressRepository nodeProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningNodeRepository learningNodeRepository;

    @Override
    @Transactional
    public NodeProgress getOrCreateProgress (Long studentId, Long nodeId) {

        return nodeProgressRepository.findByStudentIdAndLearningNodeId(studentId, nodeId).orElseGet(() -> {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            LearningNode node = learningNodeRepository.findById(nodeId)
                    .orElseThrow(() -> new IllegalArgumentException("Lesson node found"));

            NodeProgress newProgress = NodeProgress.builder()
                    .student(student)
                    .learningNode(node)
                    .status(ProgressStatus.NOT_STARTED).build();

            return nodeProgressRepository.save(newProgress);
        });
    }
        @Override
                @Transactional
        public NodeProgress updateToInProgress(Long studentId, Long nodeId){
            NodeProgress progress = getOrCreateProgress(studentId,nodeId);
            if(progress.getStatus() == ProgressStatus.NOT_STARTED) {
                progress.setStatus(ProgressStatus.IN_PROGRESS);
                progress = nodeProgressRepository.save(progress);

        }
            return progress;
    }

    @Override
    @Transactional
    public NodeProgress updateToComplete(Long studentId, Long nodeId){
        NodeProgress progress = getOrCreateProgress(studentId,nodeId);

        if(progress.getStatus() != ProgressStatus.COMPLETED){
            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());

            progress = nodeProgressRepository.save(progress);
        }
        return  progress;
    }
    @Override
    @Transactional
    public NodeProgress getProgress(Long studentId, Long nodeId){
        return nodeProgressRepository.findByStudentIdAndLearningNodeId(studentId,nodeId).orElseGet(null);
    }

    @Override
    public StudentProgressDTO calculateProgress(Long studentId, Long spaceId){
        long totalNodes = learningNodeRepository.countTotalNodesBySpaceId(spaceId);

        if(totalNodes == 0){
            return  StudentProgressDTO.builder()
                    .totalNodes(0L)
                    .completeNodes(0L)
                    .progressPercenteage(0.0).build();
        }
        long completedNodes = nodeProgressRepository.countCompletedNodesByStudentAndASpace(studentId, spaceId);
            double percentage = ((double) completedNodes / totalNodes ) * 100.0;


            percentage = Math.round(percentage * 100.0) / 100.0;

            return StudentProgressDTO.builder()
                    .totalNodes(totalNodes)
                    .completeNodes(completedNodes)
                    .progressPercenteage(percentage).build();
        }
}
