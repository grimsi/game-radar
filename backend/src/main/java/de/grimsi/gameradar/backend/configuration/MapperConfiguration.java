package de.grimsi.gameradar.backend.configuration;

import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.entity.Role;
import de.grimsi.gameradar.backend.entity.User;
import de.grimsi.gameradar.backend.service.RoleService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class MapperConfiguration {

    @Autowired
    private RoleService roleService;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        Converter<Set<Role>, Set<String>> roleToRoleName = new AbstractConverter<>() {
            @Override
            protected Set<String> convert(Set<Role> source) {
                return source.stream().map(Role::getName).collect(Collectors.toUnmodifiableSet());
            }
        };

        Converter<Set<String>, Set<Role>> roleNameToRole = new AbstractConverter<>() {
            @Override
            protected Set<Role> convert(Set<String> source) {
                return roleService.convertToRole(source);
            }
        };

        mapper.typeMap(User.class, UserDto.class)
                .addMappings(m -> m.using(roleToRoleName).map(User::getRoles, UserDto::setRoles));

        mapper.typeMap(UserDto.class, User.class)
                .addMappings(m -> m.using(roleNameToRole).map(UserDto::getRoles, User::setRoles));

        return mapper;
    }
}
