package com.airflights.integration;

import com.airflights.entity.Passenger;
import com.airflights.repository.PassengerRepository;
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

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        passengerRepository.deleteAll();
    }

    @Test
    void shouldCreatePassenger() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "passportNumber": "A1234567"
                }
                """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void shouldGetAllPassengers() throws Exception {
        Passenger p = new Passenger();
        p.setFirstName("Alice");
        p.setLastName("Smith");
        p.setEmail("alice@example.com");
        p.setPassportNumber("W1234567");
        passengerRepository.save(p);

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email", is("alice@example.com")));
    }

    @Test
    void shouldDeletePassenger() throws Exception {
        Passenger p = new Passenger();
        p.setFirstName("Bob");
        p.setLastName("Brown");
        p.setEmail("bob@example.com");
        p.setPassportNumber("M1234567");
        Passenger saved = passengerRepository.save(p);

        mockMvc.perform(delete("/api/passengers/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
