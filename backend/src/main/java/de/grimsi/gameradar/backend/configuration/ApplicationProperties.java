package de.grimsi.gameradar.backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Locale;
import java.util.UUID;

@Data
@Primary
@Configuration
@ConfigurationProperties(prefix = "game-radar")
public class ApplicationProperties {
    private UUID secret;
    private String jwtExpirationTime;
    private String jwtRoleKey;
    private String passwordResetTokenExpirationTime;
    private boolean allowAdminPasswordReset;
    private Locale defaultLocale;
    private String serverStatusMinRefreshDuration;
}
