package com.airflights.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "app_user")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @ToString.Include
    private Long id;

    @Column(unique=true, nullable=false)
    @ToString.Include
    private String username;

    @Column(nullable=false)
    @JsonIgnore
    @Setter(AccessLevel.PACKAGE)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Setter
    private Set<String> roles;

    @Setter
    private Long employeeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_managed_dept_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "department_id")
    @Setter
    private Set<Long> managedDeptIds;

    @Builder.Default
    @Setter
    private boolean enabled = true;

    public void changePasswordHash(String hashed) {
        this.passwordHash = hashed;
    }
}