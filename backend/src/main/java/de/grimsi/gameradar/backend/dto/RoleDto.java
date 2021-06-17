package de.grimsi.gameradar.backend.dto;

import de.grimsi.gameradar.backend.entity.Privilege;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
public class RoleDto {
    private Long id;

    @NotBlank(message = "role.name.required")
    private String name;

    @Valid
    private Set<Privilege> privileges = new HashSet<>();
}