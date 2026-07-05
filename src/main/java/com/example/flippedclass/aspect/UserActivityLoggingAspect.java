package com.example.flippedclass.aspect;

import com.example.flippedclass.annotation.LogUserActivity;
import com.example.flippedclass.service.UserActivityService;
import com.example.flippedclass.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityLoggingAspect {

    private final UserActivityService userActivityService;

    private final ExpressionParser parser = new SpelExpressionParser();

    @AfterReturning(pointcut = "@annotation(logUserActivity)", returning = "result")
    public void logActivityAfterMethod(JoinPoint joinPoint, LogUserActivity logUserActivity, Object result) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                
                String description = logUserActivity.description();
                if (description.startsWith("'") || description.contains("#")) {
                    StandardEvaluationContext context = new StandardEvaluationContext();
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    String[] parameterNames = signature.getParameterNames();
                    Object[] args = joinPoint.getArgs();
                    if (parameterNames != null) {
                        for (int i = 0; i < parameterNames.length; i++) {
                            context.setVariable(parameterNames[i], args[i]);
                        }
                    }
                    context.setVariable("result", result);
                    try {
                        description = parser.parseExpression(description).getValue(context, String.class);
                    } catch (Exception e) {
                        log.warn("Failed to parse SpEL in log description: {}", description);
                    }
                }

                userActivityService.logActivity(userDetails.getId(), logUserActivity.actionType(), description);
            }
        } catch (Exception e) {
            log.error("Failed to execute AOP logging", e);
        }
    }
}
