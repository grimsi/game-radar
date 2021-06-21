package de.grimsi.gameradar.backend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;

@Entity
@Data
public class GameServer {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String host;
    private Integer port;
    private String game;
    private Duration refreshDuration;
}
