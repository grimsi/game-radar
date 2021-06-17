package de.grimsi.gameradar.backend.entity;

import de.grimsi.gameradar.backend.enums.GameGenre;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @ElementCollection
    private Set<GameGenre> genres;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    private Integer minTeams;
    private Integer maxTeams;
    private Integer minTeamSize;
    private Integer maxTeamSize;
}
