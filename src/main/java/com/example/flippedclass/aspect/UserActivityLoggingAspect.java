package com.example.flippedclass.aspect;

import com.example.flippedclass.annotation.LogUserActivity;
import com.example.flippedclass.service.UserActivityService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityLoggingAspect {

    private final UserActivityService userActivityService;

    @AfterReturning("@annotation(logUserActivity)")
    public void logActivityAfterMethod(JoinPoint joinPoint, LogUserActivity logUserActivity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                userActivityService.logActivity(userDetails.getId(), logUserActivity.actionType(), logUserActivity.description());
            }
        } catch (Exception e) {
            log.error("Failed to execute AOP logging", e);
        }
    }
}
