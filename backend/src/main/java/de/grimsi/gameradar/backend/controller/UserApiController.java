package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.api.UserApi;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.service.JwtService;
import de.grimsi.gameradar.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserApiController implements UserApi {

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Override
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> getSelfInfo(String token) {
        Long userId = jwtService.getUserIdFromToken(token);
        return new ResponseEntity<>(userService.getById(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> editSelfInfo(UserDto userDto, String token) {
        Long userId = jwtService.getUserIdFromToken(token);
        return new ResponseEntity<>(userService.editUserDetails(userId, userDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteSelf(UserDto userDto, String token) {
        Long userId = jwtService.getUserIdFromToken(token);
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
