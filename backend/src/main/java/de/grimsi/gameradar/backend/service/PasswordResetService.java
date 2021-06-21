package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import de.grimsi.gameradar.backend.dto.PasswordResetTokenDto;
import de.grimsi.gameradar.backend.entity.PasswordResetToken;
import de.grimsi.gameradar.backend.entity.User;
import de.grimsi.gameradar.backend.repository.PasswordResetTokenRepository;
import de.grimsi.gameradar.backend.repository.UserRepository;
import org.apache.commons.text.StringSubstitutor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Map.entry;

@Service
public class PasswordResetService {

    @Autowired
    private Logger log;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private LocalizationService localizationService;

    @Autowired
    private ApplicationProperties config;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;

    public void requestPasswordReset(String email, String linkAddress) {

        log.debug("user with email '{}' requested password reset", email);

        // we don't want the unauthenticated person to know if an account with a given mail exists,
        // so we always return HTTP Code 200 regardless of the result
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        PasswordResetToken token = generateNewToken(user);
        sendPasswordResetEmail(user, token, linkAddress);
    }

    public PasswordResetTokenDto requestPasswordReset(Long userId) {
        log.info("admin requested password reset for user with id '{}'", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user.invalid-id"));

        PasswordResetToken token = generateNewToken(user);

        return convertToDto(token);
    }

    public void resetPassword(String tokenString, String newPassword) {
        log.debug("user reset password with token '{}'", tokenString);

        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenString).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "token.invalid"));

        if (!isTokenValid(token)) {
            // since the token is expired we can safely delete it
            passwordResetTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token.expired");
        }

        User user = token.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        passwordResetTokenRepository.delete(token);
    }

    private PasswordResetToken generateNewToken(User user) {
        // check if there is already a reset token for the user
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByUser(user);

        if (tokenOptional.isPresent()) {
            PasswordResetToken token = tokenOptional.get();

            if (isTokenValid(token)) {
                // return the already existing token
                return token;
            } else {
                // since the token is expired we can safely delete it
                passwordResetTokenRepository.delete(token);
            }
        }

        Instant validUntil = Instant.now().plus(config.getPasswordResetTokenExpirationTime());

        PasswordResetToken token = new PasswordResetToken();
        token.setValidUntil(validUntil);
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());

        return passwordResetTokenRepository.save(token);
    }

    private boolean isTokenValid(PasswordResetToken token) {
        return token.getValidUntil().isAfter(Instant.now());
    }

    // TODO: as soon as the frontend has a page for the user to reset his password we should redirect there
    // TODO: (currently the link in the mail points to the backend)
    private String buildPasswordResetUrl(PasswordResetToken token, String host) {
        return "<Placeholder. " + host + "/v1/login/resetPassword/" + token.getToken() + ">";
    }

    private void sendPasswordResetEmail(User user, PasswordResetToken token, String linkAddress) {
        String subject = localizationService.getLocalizedMessage("password-reset-mail.subject", user.getLocale());
        String textTemplate = localizationService.getLocalizedMessage("password-reset-mail.message", user.getLocale());

        Map<String, String> templateVariables = Map.ofEntries(
                entry("userName", user.getUsername()),
                entry("resetUrl", buildPasswordResetUrl(token, linkAddress))
        );

        String text = StringSubstitutor.replace(textTemplate, templateVariables, "{", "}");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);

        mailSender.setDefaultEncoding("UTF-8");

        log.debug("send password reset mail to user with id '{}'", user.getId());

        mailSender.send(message);
    }

    private PasswordResetTokenDto convertToDto(PasswordResetToken passwordResetToken) {
        return mapper.map(passwordResetToken, PasswordResetTokenDto.class);
    }
}
