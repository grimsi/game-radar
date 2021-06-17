package de.grimsi.gameradar.backend.api;

import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.validations.UserValidations;
import de.grimsi.gameradar.backend.views.UserView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"login"}, description = "Endpoint for users and admins to authenticate themselves")
@RequestMapping("/v1/login")
public interface LoginApi {

    @ApiOperation("Authenticate the user")
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<Void> auth(@Validated(UserValidations.Credentials.class) @RequestBody UserDto body);

    @ApiOperation("Register a new user")
    @JsonView(UserView.Complete.class)
    @PostMapping(value = "/register", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<UserDto> register(@Validated(UserValidations.Registration.class) @RequestBody UserDto body);

    @ApiOperation("Request the reset of a forgotten password")
    @PostMapping(value = "/requestPasswordReset/{userEmail}")
    ResponseEntity<Void> requestPasswordReset(@PathVariable String userEmail, HttpServletRequest request);

    @ApiOperation("Reset the users password with a token")
    @PostMapping(value = "/resetPassword/{resetToken}")
    ResponseEntity<Void> resetPassword(@PathVariable String resetToken, @Validated(UserValidations.ResetPassword.class) @RequestBody UserDto body);
}
