package com.airflights.restrictedzone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "restricted_zones")
@Data
@AllArgsConstructor
@NoArgsConstructor
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