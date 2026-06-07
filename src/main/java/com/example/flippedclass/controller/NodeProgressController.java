package com.example.flippedclass.controller;


import com.example.flippedclass.dto.response.RoadmapGraphResponse;
import com.example.flippedclass.entity.NodeProgress;
import com.example.flippedclass.service.NodeProgressService;
import com.example.flippedclass.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-node/{nodeId}/progress")
@RequiredArgsConstructor
public class NodeProgressController {

    private NodeProgressService nodeProgressService;

    @GetMapping
    public ResponseEntity<NodeProgress> getProgress(@PathVariable Long nodeId, @RequestParam Long studentId){
        NodeProgress progress = nodeProgressService.getOrCreateProgress(studentId, nodeId);

        return  ResponseEntity.ok(progress);
    }

    @PutMapping("/start")
    public  ResponseEntity<NodeProgress> startlearning(@PathVariable Long nodeId, @RequestParam Long studentId){
        NodeProgress progress = nodeProgressService.updateToInProgress(studentId, nodeId);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/complete")
    public  ResponseEntity<NodeProgress> completeLearning(@PathVariable Long nodeId, @RequestParam Long studentId){
        NodeProgress progress = nodeProgressService.updateToComplete(studentId, nodeId);

        return ResponseEntity.ok(progress);
    }

}
