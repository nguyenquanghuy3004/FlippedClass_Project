package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.UserActivityLogDto;
import com.example.flippedclass.entity.User;
import com.example.flippedclass.entity.UserActivityLog;
import com.example.flippedclass.repository.UserActivityLogRepository;
import com.example.flippedclass.repository.UserRepository;
import com.example.flippedclass.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    // K-V: UserId -> List of Emitters (for multiple admin tabs watching the same user)
    private final Map<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void logActivity(Long userId, String actionType, String description) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return;

            UserActivityLog logEntry = UserActivityLog.builder()
                    .user(user)
                    .actionType(actionType)
                    .description(description)
                    .build();

            logEntry = activityLogRepository.save(logEntry);

            UserActivityLogDto dto = mapToDto(logEntry);
            
            // Push real-time event to admins watching this user
            pushEventToSubscribers(userId, dto);
        } catch (Exception e) {
            log.error("Failed to log user activity", e);
        }
    }

    private void pushEventToSubscribers(Long userId, UserActivityLogDto dto) {
        List<SseEmitter> emitters = userEmitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("activity").data(dto));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
        if (emitters.isEmpty()) {
            userEmitters.remove(userId);
        } else {
            userEmitters.put(userId, emitters);
        }
    }

    @Override
    public List<UserActivityLogDto> getRecentActivities(Long userId) {
        return activityLogRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public SseEmitter subscribeToUserActivity(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1 hour timeout
        
        List<SseEmitter> emitters = userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        return emitter;
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        }
    }

    private UserActivityLogDto mapToDto(UserActivityLog log) {
        return UserActivityLogDto.builder()
                .id(log.getId())
                .actionType(log.getActionType())
                .description(log.getDescription())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
