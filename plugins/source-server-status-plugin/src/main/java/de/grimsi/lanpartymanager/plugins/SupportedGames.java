package de.grimsi.gameradar.plugins;

import lombok.Getter;

import java.util.Map;
import java.util.Map.Entry;

import static java.util.Map.entry;


public class SupportedGames {

    @Getter
    private static final Map<Short, String> supportedGames = Map.ofEntries(
            game(4000, "Garry's Mod"),
            game(320, "Half-Life 2: Deathmatch"),
            game(240, "Counter-Strike: Source"),
            game(300, "Day of Defeat: Source"),
            game(440, "Team Fortress 2"),
            game(500, "Left 4 Dead"),
            game(550, "Left 4 Dead 2"),
            game(630, "Alien Swarm"),
            game(730, "Counter-Strike: Global Offensive")
    );

    private static Entry<Short, String> game(int appId, String name) {
        return entry((short) appId, name);
    }
}
