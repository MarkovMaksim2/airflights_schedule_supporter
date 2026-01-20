package com.airflights.airline.unit;

import com.airflights.airline.application.dto.AirlineDto;
import com.airflights.airline.application.port.in.AirlineUseCase;
import com.airflights.airline.presentation.controller.AirlineController;
import com.airflights.airline.presentation.exception.RestExceptionHandler;
import com.airflights.airline.presentation.mapper.AirlinePresentationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AirlineController.class)
@Import({RestExceptionHandler.class, AirlinePresentationMapper.class})
class AirlineControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AirlineUseCase airlineUseCase;

    @Test
    void getAll_whenWithinLimit_delegatesToService() {
        when(airlineUseCase.getAll(anyInt(), anyInt()))
                .thenReturn(Flux.just(new AirlineDto(1L, "Air", "air@mail.com")));

        webTestClient.get()
                .uri("/api/airlines?page=0&size=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Air");

        verify(airlineUseCase).getAll(anyInt(), anyInt());
    }
}
