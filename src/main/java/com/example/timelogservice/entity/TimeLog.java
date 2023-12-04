package com.example.timelogservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_log")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    public TimeLog(LocalDateTime time) {
        this.time = time;
    }
}
