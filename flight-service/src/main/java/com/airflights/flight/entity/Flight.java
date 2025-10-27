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

    @Column(nullable = false)
    private Long airlineId;

    @Column(nullable = false)
    private Long departureAirportId;

    @Column(nullable = false)
    private Long arrivalAirportId;


    @Column(nullable = false)
    private LocalDateTime departureTime;


    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private String status;
}