package com.airflights.airline.unit;

import com.airflights.airline.controller.AirlineController;
import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.exception.RestExceptionHandler;
import com.airflights.airline.service.AirlineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AirlineController.class)
@Import(RestExceptionHandler.class)
class AirlineControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AirlineService airlineService;

    @Test
    void getAll_whenWithinLimit_delegatesToService() {
        when(airlineService.getAll(any()))
                .thenReturn(Flux.just(new AirlineDto(1L, "Air", "air@mail.com")));

        webTestClient.get()
                .uri("/api/airlines?page=0&size=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Air");

        verify(airlineService).getAll(any());
    }
}
