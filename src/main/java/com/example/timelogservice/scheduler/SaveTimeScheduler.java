package com.example.timelogservice.scheduler;

import com.example.timelogservice.entity.TimeLog;
import com.example.timelogservice.service.TimeLogService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "time-generate", name = "enable")
public class SaveTimeScheduler {

    private final TimeLogService timeLogService;
    private final TaskScheduler taskScheduler;
    private final Queue<LocalDateTime> cache = new ConcurrentLinkedQueue<LocalDateTime>();

    @Value("${db-reconnect-interval}")
    private long dbReconnectInterval;

    @PostConstruct
    public void saveTimeToDb() {
        taskScheduler.schedule(saveTimeToDbTask(), getTimeSinceNow(1000));
    }

    @Scheduled(fixedRate = 1000)
    @Async("addToCacheThreadPool")
    public void saveTimeToCache() {
        var time = LocalDateTime.now();
        log.info("CACHE: Saving time to cache {}", time);
        cache.add(time);
        log.info("CACHE: Successfully saved time to cache {}", time);
    }

    private Runnable saveTimeToDbTask() {
        return () -> {
            while (!cache.isEmpty()) {
                try {
                    LocalDateTime time = cache.peek();
                    log.info("DB: Saving time to db {}", time);
                    TimeLog timeLog = timeLogService.createTimeLog(time);
                    log.info("DB: Successfully saved time to db {}", timeLog);
                    cache.poll();
                } catch (Exception e) {
                    log.error("Error while saving to db from cache");
                    taskScheduler.schedule(saveTimeToDbTask(), getTimeSinceNow(dbReconnectInterval));
                    return;
                }
            }
            taskScheduler.schedule(saveTimeToDbTask(), getTimeSinceNow(1000));
        };
    }

    private Instant getTimeSinceNow(long millisSinceNow) {
        return new Date(System.currentTimeMillis() + millisSinceNow).toInstant();
    }
}
