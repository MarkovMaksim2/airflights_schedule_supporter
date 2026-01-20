package com.airflights.restrictedzone.application.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestrictedZoneDto {
    private Long id;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
