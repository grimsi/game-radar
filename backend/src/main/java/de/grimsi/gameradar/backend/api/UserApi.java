package de.grimsi.gameradar.backend.api;

import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.validations.UserValidations;
import de.grimsi.gameradar.backend.views.UserView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"users"}, description = "Endpoint for users to manage their information")
@RequestMapping("/v1/users")
public interface UserApi {

    @ApiOperation(value = "Get usernames of all registered users", authorizations = {@Authorization(value = "auth")})
    @JsonView(UserView.Basic.class)
    @GetMapping(produces = {"application/json"})
    ResponseEntity<List<UserDto>> getUsers();

    @ApiOperation(value = "Get details of the requesting user", authorizations = {@Authorization(value = "auth")})
    @JsonView(UserView.Complete.class)
    @GetMapping(value = "/self", produces = {"application/json"})
    ResponseEntity<UserDto> getSelfInfo(@RequestHeader(name = "Authorization") String token);

    @ApiOperation(value = "Edit details of the requesting user", authorizations = {@Authorization(value = "auth")})
    @JsonView(UserView.Complete.class)
    @PatchMapping(value = "/self", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<UserDto> editSelfInfo(@Validated(UserValidations.UpdateDetails.class) @RequestBody UserDto userDto, @RequestHeader(name = "Authorization") String token);

    @ApiOperation(value = "Delete the requesting user", authorizations = {@Authorization(value = "auth")})
    @DeleteMapping("/self")
    ResponseEntity<Void> deleteSelf(@Validated(UserValidations.DeleteUser.class) @RequestBody UserDto userDto, @RequestHeader(name = "Authorization") String token);
}
