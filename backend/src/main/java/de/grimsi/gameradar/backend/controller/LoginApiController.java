package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.api.LoginApi;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.enums.Roles;
import de.grimsi.gameradar.backend.service.ApplicationManagementService;
import de.grimsi.gameradar.backend.service.PasswordResetService;
import de.grimsi.gameradar.backend.service.UserService;
import de.grimsi.gameradar.backend.service.UtilityService;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RestController
public class LoginApiController implements LoginApi {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private ApplicationManagementService applicationManagementService;

    @Autowired
    private UtilityService utilityService;

    @Override
    public ResponseEntity<Void> auth(UserDto body) {
        return userService.authenticate(body) ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<UserDto> register(UserDto body) {
        if (!applicationManagementService.isApplicationSetupComplete()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/v1/setup");
            return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
        }

        UserDto newUser = userService.register(body, Collections.singleton(Roles.ROLE_USER.name()));

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> requestPasswordReset(String userEmail, HttpServletRequest request) {
        if (!new EmailValidator().isValid(userEmail, null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email has invalid format.");
        }

        passwordResetService.requestPasswordReset(userEmail, utilityService.getHostFromURI(request.getRequestURL().toString()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resetPassword(String resetToken, UserDto userDto) {
        passwordResetService.resetPassword(resetToken, userDto.getNewPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
