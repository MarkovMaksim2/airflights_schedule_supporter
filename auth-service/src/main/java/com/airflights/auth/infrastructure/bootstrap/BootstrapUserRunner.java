package com.airflights.auth.infrastructure.bootstrap;

import com.airflights.auth.application.port.out.UserRepository;
import com.airflights.auth.domain.model.Role;
import com.airflights.auth.domain.model.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapUserRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String email;
    private final String password;

    public BootstrapUserRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${auth.bootstrap.username:}") String username,
            @Value("${auth.bootstrap.email:}") String email,
            @Value("${auth.bootstrap.password:}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return;
        }
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(Set.of(Role.SUPERVISOR));
        userRepository.save(user);
    }
}
