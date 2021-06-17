package de.grimsi.gameradar.backend.repoitory;

import de.grimsi.gameradar.backend.entity.Role;
import de.grimsi.gameradar.backend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.RepositoryTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleRepositoryTest extends RepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    void findByNameTest() {
        Role role1 = new Role();
        role1.setName("Role 1");

        Role role2 = new Role();
        role2.setName("Role 2");

        roleRepository.saveAll(List.of(role1, role2));

        Role roleFromRepository = roleRepository.findByName("Role 1").orElse(new Role());

        assertEquals(role1, roleFromRepository);
    }
}
