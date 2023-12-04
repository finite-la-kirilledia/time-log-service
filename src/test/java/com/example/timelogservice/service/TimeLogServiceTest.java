package com.example.timelogservice.service;

import com.example.timelogservice.TimeLogServiceApplicationTests;
import com.example.timelogservice.entity.TimeLog;
import com.example.timelogservice.repository.TimeLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(value = "test-gen-disable")
public class TimeLogServiceTest extends TimeLogServiceApplicationTests {

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Autowired
    private TimeLogService timeLogService;

    @BeforeEach
    public void beforeEach() {
        timeLogRepository.deleteAll();
    }

    @Test
    public void createTimeLog_successfully() {
        var testTime = LocalDateTime.now();

        var result = timeLogService.createTimeLog(testTime);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testTime, result.getTime());

        var savedResult = timeLogRepository.findById(result.getId());
        assertNotNull(savedResult.get());
        assertNotNull(savedResult.get().getId());
        assertEquals(
                testTime.truncatedTo(ChronoUnit.MILLIS),
                savedResult.get().getTime().truncatedTo(ChronoUnit.MILLIS)
        );
    }

    @Test
    public void generate10Records_ThenValidate() {
        var arrSize = 10;
        generateTestData(arrSize);

        var result = timeLogService.getAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getTotalElements());

        var entitiesFromDbMap = timeLogRepository.findAll().stream()
                .collect(Collectors.toMap(TimeLog::getId, Function.identity()));
        assertEquals(10, entitiesFromDbMap.size());
        result.forEach(timeLogRecord -> {
            TimeLog entityFromDb = entitiesFromDbMap.get(timeLogRecord.id());
            assertEquals(
                    entityFromDb.getTime().truncatedTo(ChronoUnit.MILLIS),
                    timeLogRecord.time().truncatedTo(ChronoUnit.MILLIS)
            );
        });
    }

    @Test
    public void generate100Records_ThenValidate() {
        var arrSize = 100;
        generateTestData(arrSize);

        var result = timeLogService.getAll(PageRequest.of(0, 10));
        assertEquals(10, result.getTotalPages());
        assertEquals(100, result.getTotalElements());

        var entitiesFromDbMap = timeLogRepository.findAll().stream()
                .collect(Collectors.toMap(TimeLog::getId, Function.identity()));
        assertEquals(100, entitiesFromDbMap.size());
        result.forEach(timeLogRecord -> {
            TimeLog entityFromDb = entitiesFromDbMap.get(timeLogRecord.id());
            assertEquals(
                    entityFromDb.getTime().truncatedTo(ChronoUnit.MILLIS),
                    timeLogRecord.time().truncatedTo(ChronoUnit.MILLIS)
            );
        });
    }

    private void generateTestData(Integer size) {
        LocalDateTime dateTime = LocalDateTime.now();
        for (int i = 0; i < size; i++) {
            timeLogRepository.save(new TimeLog(dateTime));
            dateTime = dateTime.plusSeconds(1);
        }
    }
}
