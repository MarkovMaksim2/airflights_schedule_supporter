package com.airflights.integration;

import com.airflights.entity.Passenger;
import com.airflights.repository.PassengerRepository;
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
class PassengerControllerIntegrationTest extends BaseIntegrationTest {
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
                    "first_name": "John",
                    "last_name": "Doe",
                    "email": "john@example.com",
                    "passport_number": "A1234567"
                }
                """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.first_name", is("John")));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicatePassenger() throws Exception {
        String json = """
            {
                "first_name": "John",
                "last_name": "Doe",
                "email": "john@example.com",
                "passport_number": "A1234567"
            }
            """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.first_name", is("John")));

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void shouldUpdatePassenger() throws Exception {
        Passenger p = new Passenger();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setEmail("john@example.com");
        p.setPassportNumber("A1234567");
        Passenger saved = passengerRepository.save(p);

        String updateJson = """
            {
                  "first_name": "Johnny",
                  "last_name": "Doe",
                  "email": "john@example.com",
                  "passport_number": "B7654321"
            }
            """;

        mockMvc.perform(put("/api/passengers/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is("Johnny")))
                .andExpect(jsonPath("$.passport_number", is("B7654321")));
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
