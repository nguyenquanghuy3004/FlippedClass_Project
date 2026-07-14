package com.example.flippedclass.service;

import com.example.flippedclass.dto.admin.UserAdminDto;
import com.example.flippedclass.dto.admin.UserDetailDto;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.example.flippedclass.dto.admin.AdminUserCreateDto;

import java.util.List;

public interface AdminUserService {
    
    Page<UserAdminDto> getUsers(int page, int size, String keyword, RoleName role, UserStatus status);
    
    UserDetailDto getUserDetail(Long userId);
    
    void lockUser(Long userId);
    
    void unlockUser(Long userId);
    
    void resetUserPassword(Long userId, String newPassword);

    UserAdminDto createUser(AdminUserCreateDto dto);

    java.util.Map<String, Object> importUsersFromExcel(MultipartFile file);
}
