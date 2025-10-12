package com.airflights.integration;

import com.airflights.entity.Airline;
import com.airflights.repository.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AirlineControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("scheduler_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AirlineRepository airlineRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        airlineRepository.deleteAll();
    }

    @Test
    void shouldCreateAirline() throws Exception {
        String json = """
                {
                    "name": "Sky Airlines",
                    "contactEmail": "contact@skyairlines.com"
                }
                """;

        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Sky Airlines")))
                .andExpect(jsonPath("$.contactEmail", is("contact@skyairlines.com")));
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
                .andExpect(jsonPath("$.contactEmail", is("info@mountainairlines.com")));
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
                    "contactEmail": "new@desertairways.com"
                }
                """;

        mockMvc.perform(put("/api/airlines/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Desert Airways Updated")))
                .andExpect(jsonPath("$.contactEmail", is("new@desertairways.com")));
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
