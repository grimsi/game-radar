package de.grimsi.gameradar.pluginapi.serverstatus;

import de.grimsi.gameradar.pluginapi.GameRadarPlugin;

public interface ServerStatusPlugin extends GameRadarPlugin {
    ServerStatus getServerStatus(String address, int port);

    int getDefaultServerPort();
}
