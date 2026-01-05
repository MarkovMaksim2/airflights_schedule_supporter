package com.restrictedzone.unit;

import com.airflights.restrictedzone.RestrictedZoneServiceApplication;
import com.airflights.restrictedzone.controller.RestrictedZoneController;
import com.airflights.restrictedzone.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.exception.RestExceptionHandler;
import com.airflights.restrictedzone.service.RestrictedZoneService;
import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestrictedZoneController.class)
@ContextConfiguration(classes = RestrictedZoneServiceApplication.class)
@Import(RestExceptionHandler.class)
class RestrictedZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestrictedZoneService restrictedZoneService;

    private RestrictedZoneDto dto;

    @BeforeEach
    void setUp() {
        dto = new RestrictedZoneDto();
        dto.setId(1L);
        dto.setRegion("region-1");
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void getAll_whenWithinLimit_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestrictedZoneDto> page = new PageImpl<>(List.of(dto), pageable, 1);
        when(restrictedZoneService.getAll(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/restricted-zones?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].region").value("region-1"));

        verify(restrictedZoneService).getAll(pageable);
    }

    @Test
    void getAll_whenPageSizeTooLarge_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/restricted-zones?page=0&size=51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Page size cannot exceed 50. Maximum allowed is 50, but received: 51"));

        verifyNoInteractions(restrictedZoneService);
    }

    @Test
    void getById_delegatesToService() throws Exception {
        when(restrictedZoneService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/restricted-zones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("region-1"));

        verify(restrictedZoneService).getById(1L);
    }

    @Test
    void create_delegatesToService() throws Exception {
        when(restrictedZoneService.create(any())).thenReturn(dto);

        String json = """
                {
                  "region": "region-1",
                  "start_time": "2025-01-01T10:00:00",
                  "end_time": "2025-01-01T12:00:00"
                }
                """;

        mockMvc.perform(post("/api/restricted-zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_delegatesToService() throws Exception {
        mockMvc.perform(delete("/api/restricted-zones/1"))
                .andExpect(status().isNoContent());

        verify(restrictedZoneService).delete(1L);
    }
}
