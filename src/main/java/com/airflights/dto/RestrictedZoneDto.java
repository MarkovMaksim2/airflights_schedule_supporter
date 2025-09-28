package com.airflights.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RestrictedZoneDto {
    private Long id;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
