package com.example.flippedclass.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {
    private UserAdminDto user;
    private int spacesOwned;
    private int spacesJoined;
}
