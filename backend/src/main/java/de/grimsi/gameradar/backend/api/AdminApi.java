package de.grimsi.gameradar.backend.api;

import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.annotations.IsAdmin;
import de.grimsi.gameradar.backend.dto.PasswordResetTokenDto;
import de.grimsi.gameradar.backend.dto.PluginMetaInfoDto;
import de.grimsi.gameradar.backend.dto.RoleDto;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.validations.UserValidations;
import de.grimsi.gameradar.backend.views.PasswordResetTokenView;
import de.grimsi.gameradar.backend.views.UserView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"admin"}, description = "Endpoint for admins to manage the application and its users")
@IsAdmin
@RequestMapping("/v1/admin")
public interface AdminApi {

    @ApiOperation(value = "Get details of all registered users", authorizations = {@Authorization(value = "auth")})
    @JsonView(UserView.Management.class)
    @GetMapping(value = "/users", produces = {"application/json"})
    ResponseEntity<List<UserDto>> getUsers();

    @ApiOperation(value = "Edit the roles of a user with a given id", authorizations = {@Authorization(value = "auth")})
    @JsonView(UserView.Management.class)
    @PatchMapping(value = "/users/{userId}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<UserDto> editUserRoles(@Validated(UserValidations.UpdateRoles.class) @RequestBody UserDto body, @PathVariable Long userId);

    @ApiOperation(value = "Reset the password of a user with a given id", notes = "Only available when the config property 'allow-admin-password-reset' is set to true because of security concerns", authorizations = {@Authorization(value = "auth")})
    @JsonView(PasswordResetTokenView.Summary.class)
    @PostMapping(value = "/users/requestPasswordReset/{userId}")
    ResponseEntity<PasswordResetTokenDto> resetUserPassword(@PathVariable Long userId);

    @ApiOperation(value = "Get a list of all roles", authorizations = {@Authorization(value = "auth")})
    @GetMapping(value = "/roles", produces = {"application/json"})
    ResponseEntity<List<RoleDto>> getRoles();

    @ApiOperation(value = "Get a list of all installed plugins", authorizations = {@Authorization(value = "auth")})
    @GetMapping(value = "/plugins", produces = {"application/json"})
    ResponseEntity<List<PluginMetaInfoDto>> getInstalledPlugins();

    @ApiOperation(value = "Get a list of all supported providers", authorizations = {@Authorization(value = "auth")})
    @GetMapping(value = "/plugins/supportedProviders", produces = {"application/json"})
    ResponseEntity<List<String>> getSupportedProviders();

}