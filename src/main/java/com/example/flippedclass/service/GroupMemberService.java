package com.example.flippedclass.service;

import com.example.flippedclass.dto.response.activity.GroupMemberResponse;

public interface GroupMemberService {
    GroupMemberResponse joinGroup(Long activityId, Long groupId, Long userId);
    void leaveGroup(Long activityId, Long groupId, Long userId);
}
