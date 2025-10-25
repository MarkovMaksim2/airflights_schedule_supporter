package com.airflights.flight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    private Long airlineId;


    @ManyToOne(optional = false)
    @JoinColumn(name = "departure_airport_id")
    private Long departureAirportId;


    @ManyToOne(optional = false)
    @JoinColumn(name = "arrival_airport_id")
    private Long arrivalAirportId;


    @Column(nullable = false)
    private LocalDateTime departureTime;


    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private String status;
}