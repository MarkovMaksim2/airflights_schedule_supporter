package com.airflights.restrictedzone.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestrictedZoneResponse {
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
