package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.entity.Role;
import de.grimsi.gameradar.backend.entity.User;
import de.grimsi.gameradar.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService extends DatabaseService<User, UserDto, UserRepository> implements UserDetailsService {

    @Autowired
    private Logger log;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationProperties config;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        log.debug("get user with name '{}'", userName);
        return userRepository.findByUsername(userName).orElseThrow(
                () -> new UsernameNotFoundException("user.invalid-name")
        );
    }

    public boolean isValidId(Long id) {
        log.debug("check if user id '{}' is valid", id);
        return userRepository.findById(id).isPresent();
    }

    public boolean areValidIds(List<UserDto> users) {
        return users.stream().allMatch(u -> isValidId(u.getId()));
    }

    public UserDto editUserRoles(Long id, UserDto userDto) {
        User existingUser = getEntityById(id);
        Set<Role> updatedRoles = roleService.convertToRole(userDto.getRoles());
        existingUser.setRoles(updatedRoles);

        // do not log sensitive information
        if (log.isDebugEnabled()) {
            User logUser = new User();
            logUser.setUsername(existingUser.getUsername());
            logUser.setId(existingUser.getId());
            logUser.setRoles(existingUser.getRoles());
            log.debug("edit user roles: {}", existingUser);
        }

        return save(convertToDto(existingUser));
    }

    public UserDto editUserDetails(Long id, UserDto userDto) {
        User existingUser = getEntityById(id);

        if (userDto.getUsername() != null) {
            existingUser.setUsername(userDto.getUsername());
        }

        if (userDto.getLocale() != null) {
            existingUser.setLocale(userDto.getLocale());
        }

        /* password and email can only be changed if the current password is sent in addition */

        boolean isUserAuthenticated = userDto.getPassword() != null && passwordEncoder.matches(userDto.getPassword(), existingUser.getPassword());

        if (userDto.getNewPassword() != null) {
            if (!isUserAuthenticated) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user.password.does-not-match");
            }
            existingUser.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
        }

        if (userDto.getEmail() != null) {
            if (!isUserAuthenticated) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user.password.does-not-match");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (log.isDebugEnabled()) {
            User logUser = new User();
            logUser.setUsername(existingUser.getUsername());
            logUser.setLocale(existingUser.getLocale());
            logUser.setId(existingUser.getId());
            log.debug("edit user details: {}", existingUser);
        }

        return save(convertToDto(existingUser));
    }

    public void deleteById(Long id) {
        log.debug("delete user with id '{}'", id);
        userRepository.deleteById(id);
    }

    private boolean authenticate(String username, String password) {
        log.debug("authenticate user '{}'", username);
        UserDetails user = loadUserByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public boolean authenticate(UserDto userCredentialsDto) {
        return authenticate(userCredentialsDto.getUsername(), userCredentialsDto.getPassword());
    }

    public UserDto register(UserDto newUser, Set<String> roles) {
        User user = new User();

        user.setUsername(newUser.getUsername());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setEmail(newUser.getEmail());
        user.setLocale(newUser.getLocale());
        user.setRoles(roleService.convertToRole(roles));

        // if the user did not define a preferred locale, use the default one
        if (user.getLocale() == null) {
            user.setLocale(config.getDefaultLocale());
        }

        return save(convertToDto(user));
    }

    public int getUserCount(String roleName) {
        log.debug("get user count for role '{}'", roleName);
        return (int) userRepository.findAll().stream().filter(
                user -> user.getRoles().stream().anyMatch(role -> role.getName().equals(roleName))
        ).count();
    }
}
