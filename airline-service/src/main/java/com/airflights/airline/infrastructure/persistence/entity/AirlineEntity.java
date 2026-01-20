package com.airflights.airline.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("airlines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirlineEntity {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("contact_email")
    private String contactEmail;
}
