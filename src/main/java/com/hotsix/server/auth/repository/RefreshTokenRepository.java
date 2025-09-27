package com.hotsix.server.auth.repository;

import com.hotsix.server.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUserId(Long userId);
}
