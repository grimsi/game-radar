package de.grimsi.gameradar.backend.repoitory;

import de.grimsi.gameradar.backend.entity.Privilege;
import de.grimsi.gameradar.backend.repository.PrivilegeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.RepositoryTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrivilegeRepositoryTest extends RepositoryTest {

    @Autowired
    PrivilegeRepository privilegeRepository;

    @Test
    void findByAuthorityTest() {
        Privilege privilege1 = new Privilege();
        privilege1.setAuthority("testAuthority1");

        Privilege privilege2 = new Privilege();
        privilege2.setAuthority("testAuthority2");

        privilegeRepository.saveAll(List.of(privilege1, privilege2));

        Privilege privilege = privilegeRepository.findByAuthority("testAuthority1").orElse(new Privilege());

        assertEquals(privilege1, privilege);
    }
}
