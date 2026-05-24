package com.example.flippedclass.dto.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapGraphResponse {
    private List<Node> nodes;
    private List<Edge> edges;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Node {
        private String id;
        private String title;
        private String description;
        private Double x;
        private Double y;
        private String type; // Thêm loại (ví dụ: 'lesson')
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Edge {
        private String id;
        private String source;
        private String target;
    }
}
