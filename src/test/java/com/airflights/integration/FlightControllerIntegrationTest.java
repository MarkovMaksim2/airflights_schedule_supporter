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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class FlightControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

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
                    "airline_id": %d,
                    "departure_airport_id": %d,
                    "arrival_airport_id": %d,
                    "departure_time": "2025-12-25T10:00:00",
                    "arrival_time": "2025-12-25T12:00:00",
                    "status": "SCHEDULED"
                }
                """.formatted(savedAirline.getId(), savedDepartureAirport.getId(), savedArrivalAirport.getId());

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.airline_id", is(savedAirline.getId().intValue())))
                .andExpect(jsonPath("$.departure_airport_id", is(savedDepartureAirport.getId().intValue())))
                .andExpect(jsonPath("$.arrival_airport_id", is(savedArrivalAirport.getId().intValue())))
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
                .andExpect(jsonPath("$.airline_id", is(savedAirline.getId().intValue())))
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
                    "airline_id": %d,
                    "departure_airport_id": %d,
                    "arrival_airport_id": %d,
                    "departure_time": "2025-12-28T16:00:00",
                    "arrival_time": "2025-12-28T19:00:00",
                    "status": "DELAYED"
                }
                """.formatted(savedAirline.getId(), savedDepartureAirport.getId(), savedArrivalAirport.getId());

        mockMvc.perform(put("/api/flights/" + savedFlight.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELAYED")))
                .andExpect(jsonPath("$.departure_time", is("2025-12-28T16:00:00")));
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
