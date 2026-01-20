package com.airport.unit;

import com.airflights.airport.AirportServiceApplication;
import com.airflights.airport.application.dto.AirportManagerDto;
import com.airflights.airport.application.port.in.AirportManagerUseCase;
import com.airflights.airport.presentation.controller.AirportManagerController;
import com.airflights.airport.presentation.exception.RestExceptionHandler;
import com.airflights.airport.presentation.mapper.AirportManagerPresentationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AirportManagerController.class)
@ContextConfiguration(classes = AirportServiceApplication.class)
@Import({RestExceptionHandler.class, AirportManagerPresentationMapper.class})
class AirportManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirportManagerUseCase airportManagerUseCase;

    @Test
    void getByEmail_delegatesToService() throws Exception {
        when(airportManagerUseCase.getByEmail("manager@airport.com"))
                .thenReturn(Mono.just(new AirportManagerDto(1L, 2L, "manager@airport.com")));

        MvcResult result = mockMvc.perform(get("/api/airport-managers/by-email")
                        .param("email", "manager@airport.com"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_email").value("manager@airport.com"));
    }

    @Test
    void create_delegatesToService() throws Exception {
        when(airportManagerUseCase.create(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Mono.just(new AirportManagerDto(1L, 2L, "manager@airport.com")));

        String json = """
                {
                  "airport_id": 2,
                  "user_email": "manager@airport.com"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/airport-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_delegatesToService() throws Exception {
        when(airportManagerUseCase.delete(1L)).thenReturn(Mono.empty());

        MvcResult result = mockMvc.perform(delete("/api/airport-managers/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isNoContent());
    }
}
