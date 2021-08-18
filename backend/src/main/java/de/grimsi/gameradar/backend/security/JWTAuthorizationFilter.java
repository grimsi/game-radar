package de.grimsi.gameradar.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
    private final ApplicationProperties config;

    JWTAuthorizationFilter(AuthenticationManager authManager,
                           ApplicationProperties config) {
        super(authManager);
        this.config = config;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            // parse the token.
            try {
                DecodedJWT jwt = JWT
                        .require(Algorithm.HMAC512(config.getSecret().toString().getBytes()))
                        .build()
                        .verify(token.replace("Bearer ", ""));

                String user = jwt.getSubject();
                List<SimpleGrantedAuthority> authorities =
                        jwt.getClaim(config.getJwtRoleKey()).asList(SimpleGrantedAuthority.class);

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, authorities);
                }
            } catch (SignatureVerificationException | IllegalArgumentException | TokenExpiredException | JWTDecodeException e) {
                // Usually it's never a good idea to log sensitive data (in this case a token),
                // but since the token is invalid it should not be a problem
                log.warn("Error verifying JWT:\nToken: {}\nError: {}", token, e.getMessage());
            }
            return null;
        }
        return null;
    }
}
