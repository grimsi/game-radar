package de.grimsi.gameradar.backend.dto;

import de.grimsi.gameradar.pluginapi.serverstatus.ServerState;
import lombok.Data;

import java.time.Instant;

@Data
public class ServerStatusDto {
    private ServerState status;
    private Integer currentPlayers;
    private Integer maxPlayers;
    private String currentMap;
    private String currentGamemode;
    private Instant lastRefresh;
}
