package com.airflights.auth.controller;

import com.airflights.auth.dto.*;
import com.airflights.auth.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERVISOR')")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequestDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> findAll(@ParameterObject Pageable pageable) {
        Page<UserDto> page = userService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDto> setRoles(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserRolesRequestDto dto
    ) {
        UserDto updated = userService.setRoles(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/managed-departments")
    public ResponseEntity<UserDto> setManagedDepartments(
            @PathVariable("id") Long id,
            @RequestBody Set<Long> managedDeptIds
    ) {
        UserDto updated = userService.setManagedDepartments(id, managedDeptIds);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody ChangePasswordRequestDto dto
    ) {
        userService.changePassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enabled")
    public ResponseEntity<UserDto> setEnabled(
            @PathVariable("id") Long id,
            @RequestBody SetEnabledRequestDto dto
    ) {
        UserDto updated = userService.setEnabled(id, dto.isEnabled());
        return ResponseEntity.ok(updated);
    }
}
