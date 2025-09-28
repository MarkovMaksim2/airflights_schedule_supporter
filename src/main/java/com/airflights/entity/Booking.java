package com.airflights.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    private Passenger passenger;


    @ManyToOne(optional = false)
    private Flight flight;


    @Column(nullable = false)
    private LocalDateTime bookingTime;
}