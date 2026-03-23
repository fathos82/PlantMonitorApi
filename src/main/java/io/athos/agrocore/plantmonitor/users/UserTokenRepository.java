package io.athos.agrocore.plantmonitor.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByTokenAndExpiresAtAfter(String tokenHash, LocalDateTime now);

    int deleteAllByExpiresAtBefore(LocalDateTime dateTime);
}
