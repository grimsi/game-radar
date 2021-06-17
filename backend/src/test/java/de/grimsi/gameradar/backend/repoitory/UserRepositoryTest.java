package de.grimsi.gameradar.backend.repoitory;

import de.grimsi.gameradar.backend.entity.User;
import de.grimsi.gameradar.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.RepositoryTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRepositoryTest extends RepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsernameTest() {
        User user1 = new User();
        user1.setUsername("User 1");

        User user2 = new User();
        user2.setUsername("User 2");

        userRepository.saveAll(List.of(user1, user2));

        User userFromRepository = userRepository.findByUsername("User 1").orElse(new User());

        assertEquals(user1, userFromRepository);
    }

    @Test
    void findByEmailTest() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");

        User user2 = new User();
        user2.setEmail("user2@mail.com");

        userRepository.saveAll(List.of(user1, user2));

        User userFromRepository = userRepository.findByEmail("user1@mail.com").orElse(new User());

        assertEquals(user1, userFromRepository);
    }
}
