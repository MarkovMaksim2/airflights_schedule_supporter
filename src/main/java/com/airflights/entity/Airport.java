package com.airflights.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "airports")
@Data
@AllArgsConstructor
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

    @Column(nullable = false, unique = true)
    private String name;
}