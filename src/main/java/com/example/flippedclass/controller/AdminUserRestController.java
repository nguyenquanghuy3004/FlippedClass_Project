package com.example.flippedclass.controller;

import com.example.flippedclass.annotation.LogUserActivity;
import com.example.flippedclass.dto.admin.UserAdminDto;
import com.example.flippedclass.dto.admin.UserDetailDto;
import com.example.flippedclass.enums.RoleName;
import com.example.flippedclass.enums.UserStatus;
import com.example.flippedclass.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import com.example.flippedclass.dto.admin.AdminUserCreateDto;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserRestController {
    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<UserAdminDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RoleName role,
            @RequestParam(required = false) UserStatus status) {
        
        return ResponseEntity.ok(adminUserService.getUsers(page, size, keyword, role, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDto> getUserDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserDetail(id));
    }

    @PostMapping
    public ResponseEntity<UserAdminDto> createUser(@RequestBody AdminUserCreateDto dto) {
        return ResponseEntity.ok(adminUserService.createUser(dto));
    }

    @PostMapping("/import")
    public ResponseEntity<java.util.Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(adminUserService.importUsersFromExcel(file));
    }

    @LogUserActivity(actionType = "LOCK_USER", description = "'Đã khóa tài khoản user có ID: ' + #id")
    @PostMapping("/{id}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        adminUserService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @LogUserActivity(actionType = "UNLOCK_USER", description = "'Đã mở khóa tài khoản user có ID: ' + #id")
    @PostMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        adminUserService.unlockUser(id);
        return ResponseEntity.ok().build();
    }

    @LogUserActivity(actionType = "RESET_PASSWORD", description = "'Đã reset mật khẩu cho user có ID: ' + #id")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        adminUserService.resetUserPassword(id, newPassword);
        return ResponseEntity.ok(java.util.Map.of("message", "Success"));
    }

}
