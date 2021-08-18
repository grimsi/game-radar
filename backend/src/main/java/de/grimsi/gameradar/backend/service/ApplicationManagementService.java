package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.enums.Roles;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationManagementService {

    @Autowired
    private UserService userService;

    @Getter
    @Value("${application.version}")
    private String applicationVersion;

    public boolean isApplicationSetupComplete() {
        return userService.getUserCount(Roles.ROLE_SUPERADMIN) > 0;
    }

}
