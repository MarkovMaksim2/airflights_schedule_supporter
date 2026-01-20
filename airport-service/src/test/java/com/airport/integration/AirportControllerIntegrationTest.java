package com.airport.integration;

import com.airflights.airport.infrastructure.persistence.entity.AirportEntity;
import com.airflights.airport.infrastructure.persistence.repository.JpaAirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class AirportControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private JpaAirportRepository airportRepository;

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

        MvcResult result = mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
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

        MvcResult created = mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(created))
                .andExpect(status().isCreated());

        MvcResult duplicate = mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(duplicate))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllAirports() throws Exception {
        AirportEntity airport = new AirportEntity();
        airport.setCode("LAX");
        airport.setCity("Los Angeles");
        airport.setName("Los Angeles International Airport");
        airportRepository.save(airport);

        MvcResult result = mockMvc.perform(get("/api/airports"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAirportById() throws Exception {
        AirportEntity airport = new AirportEntity();
        airport.setCode("JFK");
        airport.setCity("New York");
        airport.setName("John F. Kennedy International Airport");
        AirportEntity saved = airportRepository.save(airport);

        MvcResult result = mockMvc.perform(get("/api/airports/" + saved.getId()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateAirport() throws Exception {
        AirportEntity airport = new AirportEntity();
        airport.setCode("ORD");
        airport.setCity("Chicago");
        airport.setName("O'Hare International Airport");
        AirportEntity saved = airportRepository.save(airport);

        String json = """
                {
                    "code": "ORD",
                    "city": "Chicago Updated",
                    "name": "O'Hare International Airport Updated"
                }
                """;

        MvcResult result = mockMvc.perform(put("/api/airports/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteAirport() throws Exception {
        AirportEntity airport = new AirportEntity();
        airport.setCode("DFW");
        airport.setCity("Dallas");
        airport.setName("Dallas/Fort Worth International Airport");
        AirportEntity saved = airportRepository.save(airport);

        MvcResult result = mockMvc.perform(delete("/api/airports/" + saved.getId()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk());
    }
}
