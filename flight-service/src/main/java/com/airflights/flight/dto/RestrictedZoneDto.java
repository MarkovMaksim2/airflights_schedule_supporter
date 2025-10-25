package com.airflights.flight.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestrictedZoneDto {
    @JsonAlias("id")
    @JsonProperty("id")
    private Long id;

    @JsonAlias("region")
    @JsonProperty("region")
    private String region;

    @JsonAlias("start_time")
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonAlias("end_time")
    @JsonProperty("end_time")
    private LocalDateTime endTime;
}
