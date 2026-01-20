package com.airflights.restrictedzone.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestrictedZone {
    private Long id;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
