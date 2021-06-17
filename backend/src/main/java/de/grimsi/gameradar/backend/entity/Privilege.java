package de.grimsi.gameradar.backend.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Privilege implements GrantedAuthority {

    private static final long serialVersionUID = 4046070055352200517L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String authority;
}
