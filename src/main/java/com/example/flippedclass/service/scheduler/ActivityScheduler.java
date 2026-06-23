package com.example.flippedclass.service.scheduler;

import com.example.flippedclass.entity.ClassroomActivity;
import com.example.flippedclass.enums.ActivityStatus;
import com.example.flippedclass.repository.ClassroomActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityScheduler {

    private final ClassroomActivityRepository activityRepository;

    @Scheduled(cron = "0 0 * * * *") // Runs every hour at the top of the hour
    @Transactional
    public void processActivityLifecycles() {
        log.info("Running Activity Scheduler to process deadlines...");
        LocalDateTime now = LocalDateTime.now();

        // Find activities that are OPEN or IN_PROGRESS but deadline has passed
        // For a more advanced real system, we might need a custom query
        List<ClassroomActivity> activities = activityRepository.findAll();
        
        for (ClassroomActivity activity : activities) {
            if (activity.getStatus() == ActivityStatus.OPEN || activity.getStatus() == ActivityStatus.IN_PROGRESS) {
                if (now.isAfter(activity.getDeadline())) {
                    log.info("Locking activity {} because deadline has passed", activity.getId());
                    activity.setStatus(ActivityStatus.LOCKED);
                    
                    // Logic to Auto-Group orphaned students could be called here
                    if (activity.isAutoGroupEnabled()) {
                        // groupService.autoAssignOrphanStudents(activity.getId());
                    }
                }
            }
        }
        
        activityRepository.saveAll(activities);
    }
}
