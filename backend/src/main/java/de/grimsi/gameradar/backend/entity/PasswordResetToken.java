package de.grimsi.gameradar.backend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.Instant;

@Entity
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private Instant validUntil;
}
