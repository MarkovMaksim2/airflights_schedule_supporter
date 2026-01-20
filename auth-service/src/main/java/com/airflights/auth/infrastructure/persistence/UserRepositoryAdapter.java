package com.airflights.auth.infrastructure.persistence;

import com.airflights.auth.application.port.out.UserRepository;
import com.airflights.auth.domain.model.User;
import com.airflights.auth.infrastructure.persistence.mapper.UserEntityMapper;
import com.airflights.auth.infrastructure.persistence.repository.JpaUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final JpaUserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return userEntityMapper.toDomain(
                userRepository.save(userEntityMapper.toEntity(user))
        );
    }
}
