package com.airflights.auth.unit;

import com.airflights.auth.entity.Role;
import com.airflights.auth.entity.User;
import com.airflights.auth.repository.UserRepository;
import com.airflights.auth.service.BootstrapUserRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootstrapUserRunnerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments args;

    @Test
    void run_whenAnyFieldBlank_skipsBootstrap() {
        BootstrapUserRunner runner = new BootstrapUserRunner(
                userRepository,
                passwordEncoder,
                "",
                "admin@example.com",
                "password"
        );

        runner.run(args);

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void run_whenUserExists_skipsBootstrap() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        BootstrapUserRunner runner = new BootstrapUserRunner(
                userRepository,
                passwordEncoder,
                "admin",
                "admin@example.com",
                "password"
        );

        runner.run(args);

        verify(userRepository).existsByUsername("admin");
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void run_whenValid_createsSupervisorUser() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");

        BootstrapUserRunner runner = new BootstrapUserRunner(
                userRepository,
                passwordEncoder,
                "admin",
                "admin@example.com",
                "password"
        );

        runner.run(args);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("admin", saved.getUsername());
        assertEquals("admin@example.com", saved.getEmail());
        assertEquals("hashed", saved.getPasswordHash());
        assertEquals(Set.of(Role.SUPERVISOR), saved.getRoles());
    }
}
