package de.grimsi.gameradar.backend.repoitory;

import de.grimsi.gameradar.backend.entity.Game;
import de.grimsi.gameradar.backend.enums.GameGenre;
import de.grimsi.gameradar.backend.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.RepositoryTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameRepositoryTest extends RepositoryTest {

    @Autowired
    GameRepository gameRepository;

    @Test
    void testFindByName() {
        Game game1 = new Game();
        game1.setName("Test game 1");
        game1.setGenres(Set.of(GameGenre.SHOOTER, GameGenre.SIMULATION));
        game1.setTags(Set.of("Tag 1", "Tag 2"));
        game1.setMinTeams(1);
        game1.setMaxTeams(Integer.MAX_VALUE);
        game1.setMinTeamSize(1);
        game1.setMaxTeamSize(Integer.MAX_VALUE);

        Game game2 = new Game();
        game2.setName("Test game 2");
        game2.setGenres(Set.of(GameGenre.SHOOTER, GameGenre.SIMULATION));
        game2.setTags(Set.of("Tag 1", "Tag 2"));
        game2.setMinTeams(1);
        game2.setMaxTeams(Integer.MAX_VALUE);
        game2.setMinTeamSize(1);
        game2.setMaxTeamSize(Integer.MAX_VALUE);

        gameRepository.saveAll(List.of(game1, game2));

        Game gameFromRepository = gameRepository.findByName("Test game 1").orElse(new Game());

        assertEquals(game1, gameFromRepository);
    }
}
