package com.airflights.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String firstName;


    @Column(nullable = false)
    private String lastName;


    @Column(nullable = false, unique = true)
    private String email;


    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}