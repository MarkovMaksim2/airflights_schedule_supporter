package com.airflights.booking.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.airflights.booking.controller.BookingController;
import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.exception.RestExceptionHandler;
import com.airflights.booking.service.BookingService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingController.class)
@Import(RestExceptionHandler.class)
class BookingControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void getAll_returnsPage() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, 1L, 10L, LocalDateTime.now());
        Page<BookingDto> page = new PageImpl<>(List.of(bookingDto), PageRequest.of(0, 10), 1);
        when(bookingService.getAll(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/bookings?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getAll_largePage_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/bookings?page=0&size=100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsBooking() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, 1L, 10L, LocalDateTime.now());
        when(bookingService.getById(1L, null, null)).thenReturn(bookingDto);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_returnsCreated() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, 1L, 10L, LocalDateTime.now());
        when(bookingService.create(any(), any(), any())).thenReturn(bookingDto);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "passenger_id": 1,
                                  "flight_id": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());
    }
}
