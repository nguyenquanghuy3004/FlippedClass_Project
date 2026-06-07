package com.example.flippedclass.dto.mentor;

import com.example.flippedclass.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long memberId;
    private Long userId;
    private String fullName;
    private String email;
    private MemberRole role;
    private LocalDateTime joinedAt;
}
