package de.grimsi.gameradar.backend.dev.reset;

import de.grimsi.gameradar.backend.repository.GameServerRepository;
import de.grimsi.gameradar.backend.repository.PasswordResetTokenRepository;
import de.grimsi.gameradar.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
public class ResetController implements ResetApi {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameServerRepository gameServerRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public ResponseEntity<Void> reset() {

        // remove all dynamically generated data
        userRepository.deleteAll();
        gameServerRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();

        return ResponseEntity.ok().build();
    }
}
