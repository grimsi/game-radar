package de.grimsi.gameradar.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.views.PasswordResetTokenView;
import lombok.Data;

import java.time.Instant;

@Data
public class PasswordResetTokenDto {
    @JsonView({PasswordResetTokenView.Complete.class})
    private Long id;

    @JsonView({PasswordResetTokenView.Summary.class, PasswordResetTokenView.Complete.class})
    private String token;

    @JsonView({PasswordResetTokenView.Summary.class, PasswordResetTokenView.Complete.class})
    private UserDto user;

    @JsonView({PasswordResetTokenView.Summary.class, PasswordResetTokenView.Complete.class})
    private Instant validUntil;
}
