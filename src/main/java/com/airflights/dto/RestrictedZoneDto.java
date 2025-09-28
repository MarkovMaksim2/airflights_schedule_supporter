package com.airflights.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RestrictedZoneDto {
    private Long id;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
