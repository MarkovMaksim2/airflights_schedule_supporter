package com.airport.unit;

import com.airflights.airport.controller.AirportController;
import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.exception.RestExceptionHandler;
import com.airflights.airport.AirportServiceApplication;
import com.airflights.airport.service.AirportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AirportController.class)
@ContextConfiguration(classes = AirportServiceApplication.class)
@Import(RestExceptionHandler.class)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirportService airportService;

    @Test
    void getAll_whenPageSizeOverLimit_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/airports?page=0&size=51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Page size cannot exceed 50. Maximum allowed is 50, but received: 51"));

        verifyNoInteractions(airportService);
    }

    @Test
    void getAll_whenWithinLimit_delegatesToService() throws Exception {
        when(airportService.getAll(any()))
                .thenReturn(Flux.just(new AirportDto(1L, "Airport", "AAA", "City")));

        MvcResult result = mockMvc.perform(get("/api/airports?page=0&size=1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("AAA"));

        verify(airportService).getAll(any());
    }

    @Test
    void getById_delegatesToService() throws Exception {
        when(airportService.getById(1L))
                .thenReturn(Mono.just(new AirportDto(1L, "Airport", "AAA", "City")));

        MvcResult result = mockMvc.perform(get("/api/airports/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Airport"));
    }

    @Test
    void getByCode_delegatesToService() throws Exception {
        when(airportService.findByCode("AAA"))
                .thenReturn(Mono.just(new AirportDto(1L, "Airport", "AAA", "City")));

        MvcResult result = mockMvc.perform(get("/api/airports/code/AAA"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("AAA"));
    }

    @Test
    void create_delegatesToService() throws Exception {
        when(airportService.create(any()))
                .thenReturn(Mono.just(new AirportDto(1L, "Airport", "AAA", "City")));

        String json = """
                {
                  "name": "Airport",
                  "code": "AAA",
                  "city": "City"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_delegatesToService() throws Exception {
        when(airportService.update(any(), any()))
                .thenReturn(Mono.just(new AirportDto(1L, "Updated", "AAA", "City")));

        String json = """
                {
                  "name": "Updated",
                  "code": "AAA",
                  "city": "City"
                }
                """;

        MvcResult result = mockMvc.perform(put("/api/airports/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_delegatesToService() throws Exception {
        when(airportService.delete(1L)).thenReturn(Mono.empty());

        MvcResult result = mockMvc.perform(delete("/api/airports/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk());
    }

    @Test
    void count_delegatesToService() throws Exception {
        when(airportService.count()).thenReturn(Mono.just(7L));

        MvcResult result = mockMvc.perform(get("/api/airports/count"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(7));
    }
}
