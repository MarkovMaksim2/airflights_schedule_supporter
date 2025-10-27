package com.airflights.flight.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestrictedZoneDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
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
