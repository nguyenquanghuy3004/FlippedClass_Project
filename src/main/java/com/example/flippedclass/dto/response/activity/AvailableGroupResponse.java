package com.example.flippedclass.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableGroupResponse {
    private Long id;
    private String groupName;
    private Integer currentMembers;
    private Integer maxMembers;
}
