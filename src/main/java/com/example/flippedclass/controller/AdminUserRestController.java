package com.example.flippedclass.controller;

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

    @PostMapping("/{id}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        adminUserService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        adminUserService.unlockUser(id);
        return ResponseEntity.ok().build();
    }

}
