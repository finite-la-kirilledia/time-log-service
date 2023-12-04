package com.example.timelogservice.scheduler;

import com.example.timelogservice.TimeLogServiceApplicationTests;
import com.example.timelogservice.repository.TimeLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public class SaveTimeSchedulerTest extends TimeLogServiceApplicationTests {

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Test
    public void wait10Seconds_ThenValidateNumberOfRecords() throws InterruptedException {
        Thread.sleep(10_000);
        var savedEntities = timeLogRepository.findAll();
        assertEquals(10, savedEntities.size());
    }

    @Test
    public void wait10Seconds_ThenValidateAscendingOrder() throws InterruptedException {
        Thread.sleep(10_000);
        var savedEntities = timeLogRepository.findAll();
        for (int i = 0; i < 9; i++) {
            assertEquals(savedEntities.get(i).getTime().compareTo(savedEntities.get(i + 1).getTime()), -1);

            var diff = ChronoUnit.MILLIS.between(
                    savedEntities.get(i).getTime(),
                    savedEntities.get(i + 1).getTime()
            );
            assertTrue((diff > 800) && (diff < 1200));
        }
    }
}
