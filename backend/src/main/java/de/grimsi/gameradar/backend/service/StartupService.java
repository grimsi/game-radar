package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.entity.Privilege;
import de.grimsi.gameradar.backend.entity.Role;
import de.grimsi.gameradar.backend.enums.Privileges;
import de.grimsi.gameradar.backend.enums.Roles;
import de.grimsi.gameradar.backend.repository.PrivilegeRepository;
import de.grimsi.gameradar.backend.repository.RoleRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class StartupService implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private Logger log;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            log.debug("setup already completed");
            return;
        }

        log.debug("initialize roles and privileges");

        /* Initialize roles and privileges */
        Privilege superadminApiAccess = createPrivilegeIfNotFound(Privileges.SUPERADMIN_API_ACCESS);
        Privilege adminApiAccess = createPrivilegeIfNotFound(Privileges.ADMIN_API_ACCESS);
        Privilege userApiAccess = createPrivilegeIfNotFound(Privileges.USER_API_ACCESS);
        Privilege manageTournaments = createPrivilegeIfNotFound(Privileges.MANAGE_TOURNAMENTS);
        Privilege manageGameServers = createPrivilegeIfNotFound(Privileges.MANAGE_GAMESERVERS);
        Privilege moderateShoutboard = createPrivilegeIfNotFound(Privileges.MODERATE);

        Role superAdminRole = createRoleIfNotFound(Roles.ROLE_SUPERADMIN, Set.of(userApiAccess, superadminApiAccess, adminApiAccess, manageTournaments, manageGameServers, moderateShoutboard));
        Role adminRole = createRoleIfNotFound(Roles.ROLE_ADMIN, Set.of(userApiAccess, adminApiAccess, manageTournaments, manageGameServers, moderateShoutboard));
        Role tournamentManagerRole = createRoleIfNotFound(Roles.ROLE_TOURNAMENT_ORGANISER, Set.of(userApiAccess, manageTournaments));
        Role gameserverAdminRole = createRoleIfNotFound(Roles.ROLE_GAMESERVER_ADMIN, Set.of(userApiAccess, manageGameServers));
        Role moderatorRole = createRoleIfNotFound(Roles.ROLE_MODERATOR, Set.of(userApiAccess, moderateShoutboard));
        Role userRole = createRoleIfNotFound(Roles.ROLE_USER, Collections.singleton(userApiAccess));

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
}

