package com.airflights.integration;

import com.airflights.entity.Airline;
import com.airflights.entity.Airport;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import com.airflights.repository.AirlineRepository;
import com.airflights.repository.AirportRepository;
import com.airflights.repository.BookingRepository;
import com.airflights.repository.FlightRepository;
import com.airflights.repository.PassengerRepository;
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
class BookingControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("scheduler_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

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
        bookingRepository.deleteAll();
        passengerRepository.deleteAll();
        flightRepository.deleteAll();
        airlineRepository.deleteAll();
        airportRepository.deleteAll();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        // Create required entities
        Passenger passenger = new Passenger();
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");
        passenger.setPassportNumber("P1234567");
        Passenger savedPassenger = passengerRepository.save(passenger);

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

        Flight flight = new Flight();
        flight.setAirline(savedAirline);
        flight.setDepartureAirport(savedDepartureAirport);
        flight.setArrivalAirport(savedArrivalAirport);
        flight.setDepartureTime(LocalDateTime.of(2025, 12, 25, 10, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 12, 25, 12, 0));
        flight.setStatus("SCHEDULED");
        Flight savedFlight = flightRepository.save(flight);

        String json = """
                {
                    "passengerId": %d,
                    "flightId": %d,
                    "bookingTime": "2025-12-20T10:00:00"
                }
                """.formatted(savedPassenger.getId(), savedFlight.getId());

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passengerId", is(savedPassenger.getId().intValue())))
                .andExpect(jsonPath("$.flightId", is(savedFlight.getId().intValue())));
    }

    @Test
    void shouldGetAllBookings() throws Exception {
        // Create required entities
        Passenger passenger = new Passenger();
        passenger.setFirstName("Jane");
        passenger.setLastName("Smith");
        passenger.setEmail("jane.smith@example.com");
        passenger.setPassportNumber("P7654321");
        Passenger savedPassenger = passengerRepository.save(passenger);

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
        Flight savedFlight = flightRepository.save(flight);

        Booking booking = new Booking();
        booking.setPassenger(savedPassenger);
        booking.setFlight(savedFlight);
        booking.setBookingTime(LocalDateTime.of(2025, 12, 20, 10, 0));
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].passengerId", is(savedPassenger.getId().intValue())));
    }

    @Test
    void shouldGetBookingById() throws Exception {
        // Create required entities
        Passenger passenger = new Passenger();
        passenger.setFirstName("Bob");
        passenger.setLastName("Johnson");
        passenger.setEmail("bob.johnson@example.com");
        passenger.setPassportNumber("P1111111");
        Passenger savedPassenger = passengerRepository.save(passenger);

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

        Booking booking = new Booking();
        booking.setPassenger(savedPassenger);
        booking.setFlight(savedFlight);
        booking.setBookingTime(LocalDateTime.of(2025, 12, 20, 11, 0));
        Booking savedBooking = bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/" + savedBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId", is(savedPassenger.getId().intValue())))
                .andExpect(jsonPath("$.flightId", is(savedFlight.getId().intValue())));
    }

    @Test
    void shouldDeleteBooking() throws Exception {
        // Create required entities
        Passenger passenger = new Passenger();
        passenger.setFirstName("Alice");
        passenger.setLastName("Williams");
        passenger.setEmail("alice.williams@example.com");
        passenger.setPassportNumber("P2222222");
        Passenger savedPassenger = passengerRepository.save(passenger);

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

        Booking booking = new Booking();
        booking.setPassenger(savedPassenger);
        booking.setFlight(savedFlight);
        booking.setBookingTime(LocalDateTime.of(2025, 12, 20, 12, 0));
        Booking savedBooking = bookingRepository.save(booking);

        mockMvc.perform(delete("/api/bookings/" + savedBooking.getId()))
                .andExpect(status().isNoContent());
    }
}
