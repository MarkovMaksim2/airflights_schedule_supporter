package com.airflights.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "passengers")
@Data
@AllArgsConstructor
@NoArgsConstructor
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


    @Column(nullable = false, unique = true)
    private String passportNumber;
}