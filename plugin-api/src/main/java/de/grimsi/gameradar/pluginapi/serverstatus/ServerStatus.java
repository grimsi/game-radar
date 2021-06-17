package de.grimsi.gameradar.pluginapi.serverstatus;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@ToString
public class ServerStatus {
    @NonNull
    private ServerState status;
    private Integer currentPlayers;
    private Integer maxPlayers;
    private String currentMap;
    private String currentGamemode;
}
