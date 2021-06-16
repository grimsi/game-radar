package de.grimsi.gameradar.pluginapi.serverstatus;

public record ServerStatus(
        ServerState status,
        Integer currentPlayers,
        Integer maxPlayers,
        String currentMap,
        String currentGamemode
) {}
