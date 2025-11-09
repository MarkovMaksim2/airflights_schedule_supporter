package com.airflights.integration;

import com.airflights.entity.Airport;
import com.airflights.repository.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class AirportControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AirportRepository airportRepository;

    @BeforeEach
    void setUp() {
        airportRepository.deleteAll();
    }

    @Test
    void shouldCreateAirport() throws Exception {
        String json = """
                {
                    "code": "SFO",
                    "city": "San Francisco",
                    "name": "San Francisco International Airport"
                }
                """;

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("SFO")))
                .andExpect(jsonPath("$.city", is("San Francisco")))
                .andExpect(jsonPath("$.name", is("San Francisco International Airport")));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateAirport() throws Exception {
        String json = """
                {
                    "code": "SFO",
                    "city": "San Francisco",
                    "name": "San Francisco International Airport"
                }
                """;

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("SFO")));

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void shouldGetAllAirports() throws Exception {
        Airport airport = new Airport();
        airport.setCode("LAX");
        airport.setCity("Los Angeles");
        airport.setName("Los Angeles International Airport");
        airportRepository.save(airport);

        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code", is("LAX")));
    }

    @Test
    void shouldGetAirportById() throws Exception {
        Airport airport = new Airport();
        airport.setCode("JFK");
        airport.setCity("New York");
        airport.setName("John F. Kennedy International Airport");
        Airport saved = airportRepository.save(airport);

        mockMvc.perform(get("/api/airports/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("JFK")))
                .andExpect(jsonPath("$.city", is("New York")))
                .andExpect(jsonPath("$.name", is("John F. Kennedy International Airport")));
    }

    @Test
    void shouldUpdateAirport() throws Exception {
        Airport airport = new Airport();
        airport.setCode("ORD");
        airport.setCity("Chicago");
        airport.setName("O'Hare International Airport");
        Airport saved = airportRepository.save(airport);

        String json = """
                {
                    "code": "ORD",
                    "city": "Chicago Updated",
                    "name": "O'Hare International Airport Updated"
                }
                """;

        mockMvc.perform(put("/api/airports/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("ORD")))
                .andExpect(jsonPath("$.city", is("Chicago Updated")))
                .andExpect(jsonPath("$.name", is("O'Hare International Airport Updated")));
    }

    @Test
    void shouldDeleteAirport() throws Exception {
        Airport airport = new Airport();
        airport.setCode("DFW");
        airport.setCity("Dallas");
        airport.setName("Dallas/Fort Worth International Airport");
        Airport saved = airportRepository.save(airport);

        mockMvc.perform(delete("/api/airports/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
