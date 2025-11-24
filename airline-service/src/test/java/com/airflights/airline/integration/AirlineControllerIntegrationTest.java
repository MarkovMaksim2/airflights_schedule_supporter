package com.airflights.airline.integration;

import com.airflights.airline.entity.Airline;
import com.airflights.airline.repository.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AirlineControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AirlineRepository airlineRepository;

    @BeforeEach
    void setUp() {
        airlineRepository.deleteAll().block();
    }

    @Test
    void shouldCreateAirline() {
        String json = """
                {
                    "name": "Sky Airlines",
                    "contact_email": "contact@skyairlines.com"
                }
                """;

        webTestClient.post()
                .uri("/api/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sky Airlines")
                .jsonPath("$.contact_email").isEqualTo("contact@skyairlines.com");
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateAirline() {
        Airline airline = new Airline(null, "Sky Airlines", "contact@skyairlines.com");

        airlineRepository.save(airline).block();

        String json = """
                {
                    "name": "Sky Airlines",
                    "contact_email": "contact@skyairlines.com"
                }
                """;

        webTestClient.post()
                .uri("/api/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(v -> ((String)v).contains("already exists"));
    }

    @Test
    void shouldGetAllAirlines() {
        Airline airline = new Airline(null, "Ocean Airways", "info@oceanairways.com");
        airlineRepository.save(airline).block();

        webTestClient.get()
                .uri("/api/airlines")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].name").isEqualTo("Ocean Airways");
    }

    @Test
    void shouldGetAirlineById() {
        Airline airline = new Airline(null, "Mountain Airlines", "info@mountainairlines.com");
        Airline saved = airlineRepository.save(airline).block();

        webTestClient.get()
                .uri("/api/airlines/" + saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Mountain Airlines")
                .jsonPath("$.contact_email").isEqualTo("info@mountainairlines.com");
    }

    @Test
    void shouldUpdateAirline() {
        Airline airline = new Airline(null, "Desert Airways", "old@desertairways.com");
        Airline saved = airlineRepository.save(airline).block();

        String json = """
                {
                    "name": "Desert Airways Updated",
                    "contact_email": "new@desertairways.com"
                }
                """;

        webTestClient.put()
                .uri("/api/airlines/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Desert Airways Updated")
                .jsonPath("$.contact_email").isEqualTo("new@desertairways.com");
    }

    @Test
    void shouldDeleteAirline() {
        Airline airline = new Airline(null, "Forest Airlines", "info@forestairlines.com");
        Airline saved = airlineRepository.save(airline).block();

        webTestClient.delete()
                .uri("/api/airlines/" + saved.getId())
                .exchange()
                .expectStatus().isNoContent();
    }
}
