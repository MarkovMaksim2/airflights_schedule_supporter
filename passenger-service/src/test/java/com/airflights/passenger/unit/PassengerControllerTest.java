package com.airflights.passenger.unit;

import com.airflights.passenger.PassengerServiceApplication;
import com.airflights.passenger.application.dto.PassengerDto;
import com.airflights.passenger.application.port.in.PassengerUseCase;
import com.airflights.passenger.presentation.controller.PassengerController;
import com.airflights.passenger.presentation.exception.RestExceptionHandler;
import com.airflights.passenger.presentation.mapper.PassengerPresentationMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PassengerController.class)
@ContextConfiguration(classes = PassengerServiceApplication.class)
@Import({RestExceptionHandler.class, PassengerPresentationMapper.class})
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerUseCase passengerService;

    private PassengerDto passengerDto;

    @BeforeEach
    void setUp() {
        passengerDto = new PassengerDto();
        passengerDto.setId(1L);
        passengerDto.setFirstName("John");
        passengerDto.setLastName("Doe");
        passengerDto.setEmail("john@example.com");
        passengerDto.setPassportNumber("A1234567");
    }

    @Test
    void getAll_whenWithinLimit_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PassengerDto> page = new PageImpl<>(List.of(passengerDto), pageable, 1);
        when(passengerService.getAll(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/passengers?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"));

        verify(passengerService).getAll(pageable);
    }

    @Test
    void getAll_whenPageSizeTooLarge_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/passengers?page=0&size=51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Page size cannot exceed 50. Maximum allowed is 50, but received: 51"));

        verifyNoInteractions(passengerService);
    }

    @Test
    void getById_delegatesToService() throws Exception {
        when(passengerService.getById(1L)).thenReturn(passengerDto);

        mockMvc.perform(get("/api/passengers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(passengerService).getById(1L);
    }

    @Test
    void getByEmail_delegatesToService() throws Exception {
        when(passengerService.getByEmail("john@example.com")).thenReturn(passengerDto);

        mockMvc.perform(get("/api/passengers/by-email")
                        .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passport_number").value("A1234567"));

        verify(passengerService).getByEmail("john@example.com");
    }

    @Test
    void create_withSupervisorRole_creates() throws Exception {
        when(passengerService.create(any())).thenReturn(passengerDto);

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
                        .header("X-Auth-Roles", "ROLE_SUPERVISOR")
                        .header("X-Auth-Email", "john@example.com")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_withPassengerRole_missingEmailHeader_forbidden() throws Exception {
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
                        .header("X-Auth-Roles", "ROLE_PASSENGER")
                        .content(json))
                .andExpect(status().isForbidden());

        verifyNoInteractions(passengerService);
    }

    @Test
    void create_withoutPassengerRole_forbidden() throws Exception {
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
                        .header("X-Auth-Roles", "ROLE_USER")
                        .header("X-Auth-Email", "john@example.com")
                        .content(json))
                .andExpect(status().isForbidden());

        verifyNoInteractions(passengerService);
    }

    @Test
    void create_withPassengerRole_emailMatches_creates() throws Exception {
        when(passengerService.create(any())).thenReturn(passengerDto);

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
                        .header("X-Auth-Roles", "ROLE_PASSENGER")
                        .header("X-Auth-Email", "john@example.com")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(passengerService).create(any());
    }

    @Test
    void update_delegatesToService() throws Exception {
        when(passengerService.update(any(), any())).thenReturn(passengerDto);

        String json = """
                {
                  "first_name": "John",
                  "last_name": "Doe",
                  "email": "john@example.com",
                  "passport_number": "A1234567"
                }
                """;

        mockMvc.perform(put("/api/passengers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(passengerService).update(any(), any());
    }

    @Test
    void delete_delegatesToService() throws Exception {
        mockMvc.perform(delete("/api/passengers/1"))
                .andExpect(status().isNoContent());

        verify(passengerService).delete(1L);
    }
}
