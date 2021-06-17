package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import de.grimsi.gameradar.backend.dto.PasswordResetTokenDto;
import de.grimsi.gameradar.backend.dto.PluginMetaInfoDto;
import de.grimsi.gameradar.backend.dto.RoleDto;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.service.PasswordResetService;
import de.grimsi.gameradar.backend.service.RoleService;
import de.grimsi.gameradar.backend.api.AdminApi;
import de.grimsi.gameradar.backend.service.PluginManagementService;
import de.grimsi.gameradar.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class AdminApiController implements AdminApi {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    PluginManagementService pluginManagementService;

    @Autowired
    ApplicationProperties config;

    @Override
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> editUserRoles(UserDto body, Long userId) {
        return new ResponseEntity<>(userService.editUserRoles(userId, body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PasswordResetTokenDto> resetUserPassword(Long userId) {
        if (!config.isAllowAdminPasswordReset()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "config property 'allow-admin-password-reset' is set to false.");
        }
        return new ResponseEntity<>(passwordResetService.requestPasswordReset(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RoleDto>> getRoles() {
        return new ResponseEntity<>(roleService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PluginMetaInfoDto>> getInstalledPlugins() {
        return new ResponseEntity<>(pluginManagementService.getAllPluginMetaInfo(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getSupportedProviders() {
        return new ResponseEntity<>(pluginManagementService.getAllSupportedProviders(), HttpStatus.OK);
    }
}
