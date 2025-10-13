package com.airflights.integration;

import com.airflights.entity.RestrictedZone;
import com.airflights.repository.RestrictedZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class RestrictedZoneControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestrictedZoneRepository restrictedZoneRepository;

    @BeforeEach
    void setUp() {
        restrictedZoneRepository.deleteAll();
    }

    @Test
    void shouldCreateRestrictedZone() throws Exception {
        String json = """
                {
                    "region": "Military Zone Alpha",
                    "start_time": "2025-12-25T08:00:00",
                    "end_time": "2025-12-25T18:00:00"
                }
                """;

        mockMvc.perform(post("/api/restricted-zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.region", is("Military Zone Alpha")))
                .andExpect(jsonPath("$.start_time", is("2025-12-25T08:00:00")))
                .andExpect(jsonPath("$.end_time", is("2025-12-25T18:00:00")));
    }

    @Test
    void shouldGetAllRestrictedZones() throws Exception {
        RestrictedZone zone = new RestrictedZone();
        zone.setRegion("Military Zone Beta");
        zone.setStartTime(LocalDateTime.of(2025, 12, 26, 9, 0));
        zone.setEndTime(LocalDateTime.of(2025, 12, 26, 19, 0));
        restrictedZoneRepository.save(zone);

        mockMvc.perform(get("/api/restricted-zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].region", is("Military Zone Beta")));
    }

    @Test
    void shouldGetRestrictedZoneById() throws Exception {
        RestrictedZone zone = new RestrictedZone();
        zone.setRegion("Military Zone Gamma");
        zone.setStartTime(LocalDateTime.of(2025, 12, 27, 10, 0));
        zone.setEndTime(LocalDateTime.of(2025, 12, 27, 20, 0));
        RestrictedZone saved = restrictedZoneRepository.save(zone);

        mockMvc.perform(get("/api/restricted-zones/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region", is("Military Zone Gamma")))
                .andExpect(jsonPath("$.start_time", is("2025-12-27T10:00:00")))
                .andExpect(jsonPath("$.end_time", is("2025-12-27T20:00:00")));
    }

    @Test
    void shouldDeleteRestrictedZone() throws Exception {
        RestrictedZone zone = new RestrictedZone();
        zone.setRegion("Military Zone Delta");
        zone.setStartTime(LocalDateTime.of(2025, 12, 28, 11, 0));
        zone.setEndTime(LocalDateTime.of(2025, 12, 28, 21, 0));
        RestrictedZone saved = restrictedZoneRepository.save(zone);

        mockMvc.perform(delete("/api/restricted-zones/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
