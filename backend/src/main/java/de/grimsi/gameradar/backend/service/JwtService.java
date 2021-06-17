package de.grimsi.gameradar.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.enums.Privileges;
import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Autowired
    private Logger log;

    @Autowired
    private ApplicationProperties config;

    public UserDto getUserFromToken(String token) {
        UserDto userDto = new UserDto();
        userDto.setId(getUserIdFromToken(token));
        return userDto;
    }

    public Long getUserIdFromToken(String token) {
        log.debug("get user id from token: {}", token);
        return Long.parseLong(decodeJWT(token).getSubject());
    }

    public boolean doesTokenContainPrivilege(String token, Privileges privilege) {
        log.debug("check if token '{}' contains privilege '{}'", token, privilege);
        return decodeJWT(token).getClaim(config.getJwtRoleKey())
                .asList(SimpleGrantedAuthority.class).stream()
                .anyMatch(p -> p.getAuthority().equals(privilege.name()));
    }

    private DecodedJWT decodeJWT(String token) {
        return JWT.decode(token.replace("Bearer ", ""));
    }
}
