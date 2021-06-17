package de.grimsi.gameradar.backend.repository;

import de.grimsi.gameradar.backend.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByName(String name);
}
