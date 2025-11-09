package com.airflights.integration;

import com.airflights.entity.Airline;
import com.airflights.repository.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class AirlineControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AirlineRepository airlineRepository;

    @BeforeEach
    void setUp() {
        airlineRepository.deleteAll();
    }

    @Test
    void shouldCreateAirline() throws Exception {
        String json = """
                {
                    "name": "Sky Airlines",
                    "contact_email": "contact@skyairlines.com"
                }
                """;

        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Sky Airlines")))
                .andExpect(jsonPath("$.contact_email", is("contact@skyairlines.com")));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateAirline() throws Exception {
        String json = """
                {
                    "name": "Sky Airlines",
                    "contact_email": "contact@skyairlines.com"
                }
                """;
        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contact_email", is("contact@skyairlines.com")));

        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void shouldGetAllAirlines() throws Exception {
        Airline airline = new Airline();
        airline.setName("Ocean Airways");
        airline.setContactEmail("info@oceanairways.com");
        airlineRepository.save(airline);

        mockMvc.perform(get("/api/airlines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Ocean Airways")));
    }

    @Test
    void shouldGetAirlineById() throws Exception {
        Airline airline = new Airline();
        airline.setName("Mountain Airlines");
        airline.setContactEmail("info@mountainairlines.com");
        Airline saved = airlineRepository.save(airline);

        mockMvc.perform(get("/api/airlines/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mountain Airlines")))
                .andExpect(jsonPath("$.contact_email", is("info@mountainairlines.com")));
    }

    @Test
    void shouldUpdateAirline() throws Exception {
        Airline airline = new Airline();
        airline.setName("Desert Airways");
        airline.setContactEmail("old@desertairways.com");
        Airline saved = airlineRepository.save(airline);

        String json = """
                {
                    "name": "Desert Airways Updated",
                    "contact_email": "new@desertairways.com"
                }
                """;

        mockMvc.perform(put("/api/airlines/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Desert Airways Updated")))
                .andExpect(jsonPath("$.contact_email", is("new@desertairways.com")));
    }

    @Test
    void shouldDeleteAirline() throws Exception {
        Airline airline = new Airline();
        airline.setName("Forest Airlines");
        airline.setContactEmail("info@forestairlines.com");
        Airline saved = airlineRepository.save(airline);

        mockMvc.perform(delete("/api/airlines/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
