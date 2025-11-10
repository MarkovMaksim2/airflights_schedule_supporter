package com.airflights.airline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("airlines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airline {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("contact_email")
    private String contactEmail;
}