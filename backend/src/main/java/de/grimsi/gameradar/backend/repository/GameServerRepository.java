package de.grimsi.gameradar.backend.repository;

import de.grimsi.gameradar.backend.entity.GameServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameServerRepository extends JpaRepository<GameServer, Long> {
    List<GameServer> findAllByHost(String host);
}
