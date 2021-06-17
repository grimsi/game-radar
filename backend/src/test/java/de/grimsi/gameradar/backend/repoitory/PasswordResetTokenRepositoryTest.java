package de.grimsi.gameradar.backend.repoitory;

import de.grimsi.gameradar.backend.entity.PasswordResetToken;
import de.grimsi.gameradar.backend.entity.User;
import de.grimsi.gameradar.backend.repository.PasswordResetTokenRepository;
import de.grimsi.gameradar.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.RepositoryTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordResetTokenRepositoryTest extends RepositoryTest {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findByTokenTest() {
        User user = new User();
        user.setId(1L);
        user = userRepository.save(user);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken("testToken");
        passwordResetToken.setUser(user);
        passwordResetToken.setValidUntil(Instant.now());

        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);
        passwordResetToken = passwordResetTokenRepository.findById(passwordResetToken.getId()).orElse(new PasswordResetToken());

        PasswordResetToken passwordResetTokenFromRepository = passwordResetTokenRepository.findByToken("testToken").orElse(new PasswordResetToken());

        assertEquals(passwordResetToken.getId(), passwordResetTokenFromRepository.getId());
    }

    @Test
    void findByUserTest() {
        User user = new User();
        user.setId(2L);
        user = userRepository.save(user);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken("testToken");
        passwordResetToken.setUser(user);
        passwordResetToken.setValidUntil(Instant.now());

        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);
        passwordResetToken = passwordResetTokenRepository.findById(passwordResetToken.getId()).orElse(new PasswordResetToken());

        PasswordResetToken passwordResetTokenFromRepository = passwordResetTokenRepository.findByUser(user).orElse(new PasswordResetToken());

        assertEquals(passwordResetToken.getId(), passwordResetTokenFromRepository.getId());
    }
}
