package com.example.timelogservice.service;

import com.example.timelogservice.dto.TimeLogRecord;
import com.example.timelogservice.entity.TimeLog;
import com.example.timelogservice.repository.TimeLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;

    @Transactional
    public TimeLog createTimeLog(LocalDateTime time) {
        TimeLog timeLog = new TimeLog(time);
        return timeLogRepository.save(timeLog);
    }

    public Page<TimeLogRecord> getAll(Pageable pageable) {
        return timeLogRepository
                .findAll(pageable)
                .map(timeLog -> new TimeLogRecord(timeLog.getId(), timeLog.getTime()));
    }
}
