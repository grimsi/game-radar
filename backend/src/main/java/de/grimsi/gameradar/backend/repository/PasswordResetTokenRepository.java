package de.grimsi.gameradar.backend.repository;

import de.grimsi.gameradar.backend.entity.PasswordResetToken;
import de.grimsi.gameradar.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);
}
