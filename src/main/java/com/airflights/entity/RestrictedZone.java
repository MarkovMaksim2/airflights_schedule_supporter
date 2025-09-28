package com.airflights.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "restricted_zones")
public class RestrictedZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String region;


    @Column(nullable = false)
    private LocalDateTime startTime;


    @Column(nullable = false)
    private LocalDateTime endTime;
}