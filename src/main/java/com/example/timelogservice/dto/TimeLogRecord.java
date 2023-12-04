package com.example.timelogservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TimeLogRecord(
        Integer id,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        LocalDateTime time
) {

}
