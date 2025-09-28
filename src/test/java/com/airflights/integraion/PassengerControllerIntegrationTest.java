package com.airflights.integration;

import com.airflights.entity.Passenger;
import com.airflights.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PassengerControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scheduler_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    void setUp() {
        passengerRepository.deleteAll();
    }

    @Test
    void shouldCreatePassenger() throws Exception {
        String json = """
                {
                    "name": "John Doe",
                    "email": "john@example.com"
                }
                """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void shouldGetAllPassengers() throws Exception {
        Passenger p = new Passenger();
        p.setName("Alice");
        p.setEmail("alice@example.com");
        passengerRepository.save(p);

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is("alice@example.com")));
    }

    @Test
    void shouldDeletePassenger() throws Exception {
        Passenger p = new Passenger();
        p.setName("Bob");
        p.setEmail("bob@example.com");
        Passenger saved = passengerRepository.save(p);

        mockMvc.perform(delete("/api/passengers/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
