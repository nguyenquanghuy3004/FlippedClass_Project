package com.example.flippedclass.service;

public interface StudyGroupMemberService {
    void joinGroup(Long activityId, Long groupId, Long currentUserId, String inviteCode);
    void leaveGroup(Long groupId, Long currentUserId);
    void transferLeader(Long groupId, Long currentUserId, Long newLeaderId);
    void addMember(Long groupId, Long currentUserId, Long studentId);
}
