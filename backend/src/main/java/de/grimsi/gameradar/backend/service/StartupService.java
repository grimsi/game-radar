package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.dto.GameServerDto;
import de.grimsi.gameradar.backend.entity.Privilege;
import de.grimsi.gameradar.backend.entity.Role;
import de.grimsi.gameradar.backend.enums.Privileges;
import de.grimsi.gameradar.backend.enums.Roles;
import de.grimsi.gameradar.backend.repository.PrivilegeRepository;
import de.grimsi.gameradar.backend.repository.RoleRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StartupService {

    private boolean alreadySetup = false;

    @Autowired
    private Logger log;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private GameServerService gameServerService;

    @Autowired
    private GameServerStatusService gameServerStatusService;

    @EventListener(ApplicationReadyEvent.class)
    public void setup() {

        if (alreadySetup) {
            log.debug("setup already completed");
            return;
        }

        log.debug("initialize roles and privileges");

        /* Initialize roles and privileges */
        Privilege superadminApiAccess = createPrivilegeIfNotFound(Privileges.SUPERADMIN_API_ACCESS);
        Privilege adminApiAccess = createPrivilegeIfNotFound(Privileges.ADMIN_API_ACCESS);
        Privilege userApiAccess = createPrivilegeIfNotFound(Privileges.USER_API_ACCESS);
        Privilege manageGameServers = createPrivilegeIfNotFound(Privileges.MANAGE_GAMESERVERS);

        Role superAdminRole = createRoleIfNotFound(Roles.ROLE_SUPERADMIN, Set.of(userApiAccess, superadminApiAccess, adminApiAccess, manageGameServers));
        Role adminRole = createRoleIfNotFound(Roles.ROLE_ADMIN, Set.of(userApiAccess, adminApiAccess, manageGameServers));
        Role gameserverAdminRole = createRoleIfNotFound(Roles.ROLE_GAMESERVER_ADMIN, Set.of(userApiAccess, manageGameServers));
        Role userRole = createRoleIfNotFound(Roles.ROLE_USER, Collections.singleton(userApiAccess));

        /* Schedule all game servers for server status refreshes */
        scheduleGameServerStatusUpdates();

        alreadySetup = true;
    }

    private Privilege createPrivilegeIfNotFound(Privileges authority) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findByAuthority(authority.name());

        if (optionalPrivilege.isPresent()) {
            return optionalPrivilege.get();
        }

        Privilege privilege = new Privilege();
        privilege.setAuthority(authority.name());
        privilegeRepository.save(privilege);
        return privilege;
    }

    private Role createRoleIfNotFound(Roles roleName, Set<Privilege> privileges) {
        Optional<Role> optionalRole = roleRepository.findByName(roleName.name());

        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }

        Role role = new Role();
        role.setName(roleName.name());
        role.setPrivileges(privileges);
        return roleRepository.save(role);
    }

    private void scheduleGameServerStatusUpdates() {
        List<GameServerDto> allGameServers = gameServerService.getAll();
        allGameServers.forEach((server) -> gameServerStatusService.scheduleServerStatusRefresh(server));
    }
}

