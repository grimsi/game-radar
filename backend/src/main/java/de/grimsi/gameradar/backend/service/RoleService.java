package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.dto.RoleDto;
import de.grimsi.gameradar.backend.entity.Role;
import de.grimsi.gameradar.backend.repository.RoleRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService extends DatabaseService<Role, RoleDto, RoleRepository> {

    @Autowired
    private Logger log;

    @Autowired
    private RoleRepository roleRepository;

    private Role convertToRole(String roleName) {
        final String rolePrefix = "ROLE_";

        log.debug("convert string '{}' to role", roleName);

        if (!roleName.startsWith(rolePrefix)) {
            roleName = rolePrefix + roleName;
        }

        return roleRepository.findByName(roleName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "role.invalid-name")
        );
    }

    public Set<Role> convertToRole(Set<String> roles) {
        if (log.isDebugEnabled()) {
            log.debug("convert strings [{}] to roles", String.join(",", roles));
        }

        return roles.stream().map(this::convertToRole).collect(Collectors.toSet());
    }

    public RoleDto findByName(String roleName) {
        log.debug("get role with name '{}'", roleName);
        return convertToDto(convertToRole(roleName));
    }
}