package com.airflights.integration;

import com.airflights.entity.Airline;
import com.airflights.entity.Airport;
import com.airflights.entity.Flight;
import com.airflights.repository.AirlineRepository;
import com.airflights.repository.AirportRepository;
import com.airflights.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FlightControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("scheduler_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        flightRepository.deleteAll();
        airlineRepository.deleteAll();
        airportRepository.deleteAll();
    }

    @Test
    void shouldCreateFlight() throws Exception {
        // Create required entities
        Airline airline = new Airline();
        airline.setName("Test Airline");
        airline.setContactEmail("contact@testairline.com");
        Airline savedAirline = airlineRepository.save(airline);

        Airport departureAirport = new Airport();
        departureAirport.setCode("SFO");
        departureAirport.setCity("San Francisco");
        departureAirport.setName("San Francisco International Airport");
        Airport savedDepartureAirport = airportRepository.save(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setCode("LAX");
        arrivalAirport.setCity("Los Angeles");
        arrivalAirport.setName("Los Angeles International Airport");
        Airport savedArrivalAirport = airportRepository.save(arrivalAirport);

        String json = """
                {
                    "airlineId": %d,
                    "departureAirportId": %d,
                    "arrivalAirportId": %d,
                    "departureTime": "2025-12-25T10:00:00",
                    "arrivalTime": "2025-12-25T12:00:00",
                    "status": "SCHEDULED"
                }
                """.formatted(savedAirline.getId(), savedDepartureAirport.getId(), savedArrivalAirport.getId());

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.airlineId", is(savedAirline.getId().intValue())))
                .andExpect(jsonPath("$.departureAirportId", is(savedDepartureAirport.getId().intValue())))
                .andExpect(jsonPath("$.arrivalAirportId", is(savedArrivalAirport.getId().intValue())))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));
    }

    @Test
    void shouldGetAllFlights() throws Exception {
        // Create required entities
        Airline airline = new Airline();
        airline.setName("Another Airline");
        airline.setContactEmail("contact@anotherairline.com");
        Airline savedAirline = airlineRepository.save(airline);

        Airport departureAirport = new Airport();
        departureAirport.setCode("NYC");
        departureAirport.setCity("New York");
        departureAirport.setName("New York International Airport");
        Airport savedDepartureAirport = airportRepository.save(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setCode("MIA");
        arrivalAirport.setCity("Miami");
        arrivalAirport.setName("Miami International Airport");
        Airport savedArrivalAirport = airportRepository.save(arrivalAirport);

        Flight flight = new Flight();
        flight.setAirline(savedAirline);
        flight.setDepartureAirport(savedDepartureAirport);
        flight.setArrivalAirport(savedArrivalAirport);
        flight.setDepartureTime(LocalDateTime.of(2025, 12, 26, 14, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 12, 26, 17, 0));
        flight.setStatus("SCHEDULED");
        flightRepository.save(flight);

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status", is("SCHEDULED")));
    }

    @Test
    void shouldGetFlightById() throws Exception {
        // Create required entities
        Airline airline = new Airline();
        airline.setName("Third Airline");
        airline.setContactEmail("contact@thirdairline.com");
        Airline savedAirline = airlineRepository.save(airline);

        Airport departureAirport = new Airport();
        departureAirport.setCode("CHI");
        departureAirport.setCity("Chicago");
        departureAirport.setName("Chicago International Airport");
        Airport savedDepartureAirport = airportRepository.save(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setCode("SEA");
        arrivalAirport.setCity("Seattle");
        arrivalAirport.setName("Seattle International Airport");
        Airport savedArrivalAirport = airportRepository.save(arrivalAirport);

        Flight flight = new Flight();
        flight.setAirline(savedAirline);
        flight.setDepartureAirport(savedDepartureAirport);
        flight.setArrivalAirport(savedArrivalAirport);
        flight.setDepartureTime(LocalDateTime.of(2025, 12, 27, 9, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 12, 27, 12, 0));
        flight.setStatus("SCHEDULED");
        Flight savedFlight = flightRepository.save(flight);

        mockMvc.perform(get("/api/flights/" + savedFlight.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.airlineId", is(savedAirline.getId().intValue())))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));
    }

    @Test
    void shouldUpdateFlight() throws Exception {
        // Create required entities
        Airline airline = new Airline();
        airline.setName("Fourth Airline");
        airline.setContactEmail("contact@fourthairline.com");
        Airline savedAirline = airlineRepository.save(airline);

        Airport departureAirport = new Airport();
        departureAirport.setCode("BOS");
        departureAirport.setCity("Boston");
        departureAirport.setName("Boston International Airport");
        Airport savedDepartureAirport = airportRepository.save(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setCode("ATL");
        arrivalAirport.setCity("Atlanta");
        arrivalAirport.setName("Atlanta International Airport");
        Airport savedArrivalAirport = airportRepository.save(arrivalAirport);

        Flight flight = new Flight();
        flight.setAirline(savedAirline);
        flight.setDepartureAirport(savedDepartureAirport);
        flight.setArrivalAirport(savedArrivalAirport);
        flight.setDepartureTime(LocalDateTime.of(2025, 12, 28, 15, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 12, 28, 18, 0));
        flight.setStatus("SCHEDULED");
        Flight savedFlight = flightRepository.save(flight);

        String json = """
                {
                    "airlineId": %d,
                    "departureAirportId": %d,
                    "arrivalAirportId": %d,
                    "departureTime": "2025-12-28T16:00:00",
                    "arrivalTime": "2025-12-28T19:00:00",
                    "status": "DELAYED"
                }
                """.formatted(savedAirline.getId(), savedDepartureAirport.getId(), savedArrivalAirport.getId());

        mockMvc.perform(put("/api/flights/" + savedFlight.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELAYED")))
                .andExpect(jsonPath("$.departureTime", is("2025-12-28T16:00:00")));
    }

    @Test
    void shouldDeleteFlight() throws Exception {
        // Create required entities
        Airline airline = new Airline();
        airline.setName("Fifth Airline");
        airline.setContactEmail("contact@fifthairline.com");
        Airline savedAirline = airlineRepository.save(airline);

        Airport departureAirport = new Airport();
        departureAirport.setCode("DEN");
        departureAirport.setCity("Denver");
        departureAirport.setName("Denver International Airport");
        Airport savedDepartureAirport = airportRepository.save(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setCode("PHX");
        arrivalAirport.setCity("Phoenix");
        arrivalAirport.setName("Phoenix International Airport");
        Airport savedArrivalAirport = airportRepository.save(arrivalAirport);

        Flight flight = new Flight();
        flight.setAirline(savedAirline);
        flight.setDepartureAirport(savedDepartureAirport);
        flight.setArrivalAirport(savedArrivalAirport);
        flight.setDepartureTime(LocalDateTime.of(2025, 12, 29, 11, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 12, 29, 14, 0));
        flight.setStatus("SCHEDULED");
        Flight savedFlight = flightRepository.save(flight);

        mockMvc.perform(delete("/api/flights/" + savedFlight.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetInfiniteScrollFlights() throws Exception {
        // Create required entities
        Airline airline = new Airline();
        airline.setName("Sixth Airline");
        airline.setContactEmail("contact@sixthairline.com");
        Airline savedAirline = airlineRepository.save(airline);

        Airport departureAirport = new Airport();
        departureAirport.setCode("LAS");
        departureAirport.setCity("Las Vegas");
        departureAirport.setName("Las Vegas International Airport");
        Airport savedDepartureAirport = airportRepository.save(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setCode("MCO");
        arrivalAirport.setCity("Orlando");
        arrivalAirport.setName("Orlando International Airport");
        Airport savedArrivalAirport = airportRepository.save(arrivalAirport);

        Flight flight = new Flight();
        flight.setAirline(savedAirline);
        flight.setDepartureAirport(savedDepartureAirport);
        flight.setArrivalAirport(savedArrivalAirport);
        flight.setDepartureTime(LocalDateTime.of(2025, 12, 30, 13, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 12, 30, 16, 0));
        flight.setStatus("SCHEDULED");
        flightRepository.save(flight);

        mockMvc.perform(get("/api/flights/scroll")
                        .param("offset", "0")
                        .param("limit", "20"))
                .andExpect(status().isOk());
    }
}
