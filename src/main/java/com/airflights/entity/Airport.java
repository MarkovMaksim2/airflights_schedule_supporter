package com.airflights.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "airports")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, unique = true)
    private String name;
}