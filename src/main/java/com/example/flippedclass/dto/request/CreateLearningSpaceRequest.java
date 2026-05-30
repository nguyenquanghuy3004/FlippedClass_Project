package com.example.flippedclass.dto.request;

import enums.VisibilityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLearningSpaceRequest {

    @Size(min = 3, max = 100, message = "Tên Learning Space phải từ 3 đến 100 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @NotNull(message = "Phải chọn Visibility")
    private VisibilityType visibility;
}
