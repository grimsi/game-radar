package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.api.SetupApi;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.enums.Roles;
import de.grimsi.gameradar.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class SetupApiController implements SetupApi {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<UserDto> registerAdmin(UserDto body) {
        if (userService.getUserCount("SUPERADMIN") > 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserDto newUser = userService.register(body, Collections.singleton(Roles.ROLE_SUPERADMIN.name()));

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
