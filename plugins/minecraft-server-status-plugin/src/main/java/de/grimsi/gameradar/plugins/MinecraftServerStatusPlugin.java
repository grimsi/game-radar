package de.grimsi.gameradar.plugins;

import de.grimsi.gameradar.pluginapi.PluginMetaInfo;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerState;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatus;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatusPlugin;

import java.util.Collections;

public class MinecraftServerStatusPlugin implements ServerStatusPlugin {

    private final int DEFAULT_PORT = 25565;

    @Override
    public PluginMetaInfo getMetaInfo() {
        return PluginMetaInfo.builder()
                .author("grimsi")
                .name("minecraft-server-status-plugin")
                .type(ServerStatusPlugin.class)
                .version(getClass().getPackage().getImplementationVersion())
                .description("This plugin allows you to view the status of a Minecraft server.")
                .supportedProviders(Collections.singletonList("Minecraft"))
                .license(null)
                .repositoryUrl(null)
                .build();
    }

    @Override
    public ServerStatus getServerStatus(String address, int port) {
        MineStat ms = new MineStat(address, port, 1);

        if (!ms.isServerUp()) {
            return ServerStatus.builder()
                    .status(ServerState.STOPPED)
                    .build();
        }

        return ServerStatus.builder()
                .status(ServerState.RUNNING)
                .currentPlayers(ms.getCurrentPlayers())
                .maxPlayers(ms.getMaximumPlayers())
                .build();
    }

    @Override
    public int getDefaultServerPort() {
        return DEFAULT_PORT;
    }
}
