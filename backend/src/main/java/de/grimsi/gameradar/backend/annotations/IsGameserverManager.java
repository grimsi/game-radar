package de.grimsi.gameradar.backend.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('MANAGE_GAMESERVERS')")
public @interface IsGameserverManager {
}
