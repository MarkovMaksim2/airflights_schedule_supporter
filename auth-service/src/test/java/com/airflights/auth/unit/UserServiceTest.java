package com.airflights.auth.unit;

import com.airflights.auth.dto.UserCreateRequest;
import com.airflights.auth.dto.UserResponse;
import com.airflights.auth.entity.Role;
import com.airflights.auth.entity.User;
import com.airflights.auth.repository.UserRepository;
import com.airflights.auth.security.UserPrincipal;
import com.airflights.auth.service.UserService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new UserCreateRequest();
        createRequest.setUsername("user1");
        createRequest.setEmail("user1@example.com");
        createRequest.setPassword("password123");
    }

    @Test
    void create_whenUsernameExists_throws() {
        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.create(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_whenEmailExists_throws() {
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.create(createRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_whenRolesMissing_defaultsPassenger() {
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        UserResponse response = userService.create(createRequest);

        assertEquals(10L, response.id());
        assertEquals("user1", response.username());
        assertEquals("user1@example.com", response.email());
        assertEquals(Set.of(Role.PASSENGER), response.roles());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Set.of(Role.PASSENGER), captor.getValue().getRoles());
        assertEquals("hashed", captor.getValue().getPasswordHash());
    }

    @Test
    void create_whenRolesProvided_keepsRoles() {
        createRequest.setRoles(Set.of(Role.SUPERVISOR));
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(createRequest);

        assertEquals(Set.of(Role.SUPERVISOR), response.roles());
    }

    @Test
    void getByUsername_whenMissing_throws() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("missing"));
    }

    @Test
    void getByUsername_returnsResponse() {
        User user = new User();
        user.setId(5L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setRoles(Set.of(Role.PASSENGER));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        UserResponse response = userService.getByUsername("user1");

        assertEquals(5L, response.id());
        assertEquals("user1", response.username());
        assertEquals("user1@example.com", response.email());
    }

    @Test
    void loadUserByUsername_returnsUserPrincipal() {
        User user = new User();
        user.setId(7L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPasswordHash("hashed");
        user.setRoles(Set.of(Role.SUPERVISOR));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("user1");

        assertInstanceOf(UserPrincipal.class, details);
        assertEquals("user1", details.getUsername());
    }
}
