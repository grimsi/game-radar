package de.grimsi.gameradar.backend.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import de.grimsi.gameradar.backend.entity.User;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ApplicationProperties config;

    private final AuthenticationManager authenticationManager;

    private final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                            ApplicationProperties config) {
        this.authenticationManager = authenticationManager;
        this.config = config;
    }

    @SneakyThrows(IOException.class)
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        User creds = new ObjectMapper()
                .readValue(req.getInputStream(), User.class);

        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (BadCredentialsException exception) {
            log.error("Login with bad credentials attempted for user '{}'", creds.getUsername());
            throw exception;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {

        List<String> authorityNames = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String[] authorityNamesArray = new String[authorityNames.size()];
        authorityNames.toArray(authorityNamesArray);

        Date expirationDate = Date.from(Instant.now().plus(Duration.parse(config.getJwtExpirationTime())));

        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getId().toString())
                .withArrayClaim(config.getJwtRoleKey(), authorityNamesArray)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(expirationDate)
                .sign(HMAC512(config.getSecret().toString().getBytes()));
        res.addHeader("Access-Control-Expose-Headers", "Authorization");
        res.addHeader("Authorization", "Bearer " + token);
    }
}
