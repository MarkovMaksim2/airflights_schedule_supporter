package com.airflights.airline.integration;

import com.airflights.airline.infrastructure.persistence.entity.AirlineEntity;
import com.airflights.airline.infrastructure.persistence.repository.R2dbcAirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AirlineControllerIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private R2dbcAirlineRepository airlineRepository;

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
                .header("X-Auth-Email", "contact@skyairlines.com")
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
        AirlineEntity airline = new AirlineEntity(null, "Sky Airlines", "contact@skyairlines.com");

        airlineRepository.save(airline).block();

        String json = """
                {
                    "name": "Sky Airlines",
                    "contact_email": "contact@skyairlines.com"
                }
                """;

        webTestClient.post()
                .uri("/api/airlines")
                .header("X-Auth-Email", "contact@skyairlines.com")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(v -> ((String)v).contains("already exists"));
    }

    @Test
    void shouldGetAllAirlines() {
        AirlineEntity airline = new AirlineEntity(null, "Ocean Airways", "info@oceanairways.com");
        airlineRepository.save(airline).block();

        webTestClient.get()
                .uri("/api/airlines")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Ocean Airways");
    }

    @Test
    void shouldGetAirlineById() {
        AirlineEntity airline = new AirlineEntity(null, "Mountain Airlines", "info@mountainairlines.com");
        AirlineEntity saved = airlineRepository.save(airline).block();

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
        AirlineEntity saved = airlineRepository.save(
                new AirlineEntity(null, "Old", "old@mail.com")
        ).block();

        String json = """
            {
                "name": "New Name",
                "contact_email": "new@mail.com"
            }
        """;

        webTestClient.put()
                .uri("/api/airlines/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("New Name");
    }

    @Test
    void shouldDeleteAirline() {
        AirlineEntity airline = new AirlineEntity(null, "Forest Airlines", "info@forestairlines.com");
        AirlineEntity saved = airlineRepository.save(airline).block();

        webTestClient.delete()
                .uri("/api/airlines/" + saved.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnEmptyList() {
        webTestClient.get()
                .uri("/api/airlines")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("[]");
    }

    @Test
    void shouldReturnNotFound() {
        webTestClient.get()
                .uri("/api/airlines/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnNotFoundForNonExisting() {
        webTestClient.delete()
                .uri("/api/airlines/999")
                .exchange()
                .expectStatus().isNotFound();
    }
}
