package com.airflights.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "airports")
public class Airport {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String code;


    @Column(nullable = false)
    private String city;


    @OneToMany(mappedBy = "departureAirport")
    private List<Flight> departures;


    @OneToMany(mappedBy = "arrivalAirport")
    private List<Flight> arrivals;
}