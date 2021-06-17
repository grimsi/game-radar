package de.grimsi.gameradar.backend.repoitory;

import de.grimsi.gameradar.backend.entity.GameServer;
import de.grimsi.gameradar.backend.repository.GameServerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.RepositoryTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameServerRepositoryTest extends RepositoryTest {

    @Autowired
    GameServerRepository gameServerRepository;

    @Test
    void testFindAllByHost() {
        GameServer gameServer1 = new GameServer();
        gameServer1.setName("Test gameserver 1");
        gameServer1.setHost("test.host.com");
        gameServer1.setPort(Integer.MAX_VALUE);
        gameServer1.setGame("Test game 1");

        GameServer gameServer2 = new GameServer();
        gameServer2.setName("Test gameserver 2");
        gameServer2.setHost("test.host.com");
        gameServer2.setPort(Integer.MAX_VALUE);
        gameServer2.setGame("Test game 2");

        GameServer gameServer3 = new GameServer();
        gameServer3.setName("Test gameserver 3");
        gameServer3.setHost("test.differenthost.com");
        gameServer3.setPort(Integer.MAX_VALUE);
        gameServer3.setGame("Test game");

        gameServerRepository.saveAll(List.of(gameServer1, gameServer2, gameServer3));

        List<GameServer> expectedResult = List.of(gameServer1, gameServer2);

        List<GameServer> actualResult = gameServerRepository.findAllByHost("test.host.com");

        assertEquals(expectedResult, actualResult);
    }
}
