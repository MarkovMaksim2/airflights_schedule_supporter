package com.airport.integration;

import com.airflights.airport.entity.Airport;
import com.airflights.airport.repository.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

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
                .andExpect(status().isCreated());
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
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllAirports() throws Exception {
        Airport airport = new Airport();
        airport.setCode("LAX");
        airport.setCity("Los Angeles");
        airport.setName("Los Angeles International Airport");
        airportRepository.save(airport);

        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAirportById() throws Exception {
        Airport airport = new Airport();
        airport.setCode("JFK");
        airport.setCity("New York");
        airport.setName("John F. Kennedy International Airport");
        Airport saved = airportRepository.save(airport);

        mockMvc.perform(get("/api/airports/" + saved.getId()))
                .andExpect(status().isOk());
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
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteAirport() throws Exception {
        Airport airport = new Airport();
        airport.setCode("DFW");
        airport.setCity("Dallas");
        airport.setName("Dallas/Fort Worth International Airport");
        Airport saved = airportRepository.save(airport);

        mockMvc.perform(delete("/api/airports/" + saved.getId()))
                .andExpect(status().isOk());
    }
}
