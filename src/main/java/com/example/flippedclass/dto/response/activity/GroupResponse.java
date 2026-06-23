package com.example.flippedclass.dto.response.activity;

import com.example.flippedclass.enums.GroupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String groupName;
    private GroupStatus status;
    private Integer currentMembers;
    private Integer maxMembers;
}
