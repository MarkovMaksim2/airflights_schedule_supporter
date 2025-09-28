package com.airflights.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "airlines")
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String name;


    @OneToMany(mappedBy = "airline")
    private List<Flight> flights;
}