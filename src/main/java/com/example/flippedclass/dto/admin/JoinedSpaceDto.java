package com.example.flippedclass.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinedSpaceDto {
    private Long id;
    private String name;
    private String role;
    private LocalDateTime joinedAt;
}
