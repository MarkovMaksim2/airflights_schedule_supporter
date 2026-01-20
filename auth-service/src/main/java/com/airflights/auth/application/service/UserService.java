package com.airflights.auth.application.service;

import com.airflights.auth.application.dto.UserCreateDto;
import com.airflights.auth.application.dto.UserDto;
import com.airflights.auth.application.mapper.UserMapper;
import com.airflights.auth.application.port.in.UserUseCase;
import com.airflights.auth.application.port.out.UserRepository;
import com.airflights.auth.application.security.UserPrincipal;
import com.airflights.auth.domain.model.Role;
import com.airflights.auth.domain.model.User;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService, UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserDto create(UserCreateDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Role.PASSENGER);
        }

        User user = userMapper.toDomain(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserPrincipal(user);
    }
}
