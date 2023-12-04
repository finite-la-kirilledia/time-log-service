package com.example.timelogservice.rest;

import com.example.timelogservice.dto.TimeLogRecord;
import com.example.timelogservice.service.TimeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/time-log")
@RequiredArgsConstructor
public class TimeLogController {

    private final TimeLogService timeLogService;

    @GetMapping
    public Page<TimeLogRecord> getAll(Pageable pageable) {
        return timeLogService.getAll(pageable);
    }
}
