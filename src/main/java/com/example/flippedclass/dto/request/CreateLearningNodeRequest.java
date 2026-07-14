package com.example.flippedclass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateLearningNodeRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;
    
    private String description;
    
    @NotNull(message = "ID Lộ trình học (Learning Path ID) không được để trống")
    private Long learningPathId;
    
    @NotBlank(message = "Loại bài học (Node Type) không được để trống")
    private String nodeType;
    
    private String content;
    private String starterCode;
    private String solutionCode;
    private Long prerequisiteNodeId;
    
    @Min(value = 1, message = "Thứ tự hiển thị phải từ 1 trở lên")
    private Integer displayOrder;
    
    @Min(value = 1, message = "Thời lượng học dự kiến ít nhất phải là 1 phút")
    @Max(value = 1440, message = "Thời lượng học không được quá 24 tiếng (1440 phút)")
    private Integer estimatedMinutes;
}

