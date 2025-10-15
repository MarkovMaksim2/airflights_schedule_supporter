package com.airflights.entity;

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
    private Airline airline;


    @ManyToOne(optional = false)
    @JoinColumn(name = "departure_airport_id")
    private Airport departureAirport;


    @ManyToOne(optional = false)
    @JoinColumn(name = "arrival_airport_id")
    private Airport arrivalAirport;


    @Column(nullable = false)
    private LocalDateTime departureTime;


    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private String status;
}