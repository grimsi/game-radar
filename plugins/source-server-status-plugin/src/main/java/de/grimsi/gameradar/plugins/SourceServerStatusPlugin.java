package de.grimsi.gameradar.plugins;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;
import de.grimsi.gameradar.pluginapi.PluginMetaInfo;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerState;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatus;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatusPlugin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SourceServerStatusPlugin implements ServerStatusPlugin {

    private final int DEFAULT_PORT = 27015;

    @Override
    public PluginMetaInfo getMetaInfo() {
        return PluginMetaInfo
                .builder()
                .author("grimsi")
                .name("source-server-status-plugin")
                .type(ServerStatusPlugin.class)
                .version(getClass().getPackage().getImplementationVersion())
                .description("This plugin allows you to view the status of a Source Engine based server.")
                .supportedProviders(getSupportedProviders())
                .license(null)
                .repositoryUrl(null)
                .build();
    }

    @Override
    public ServerStatus getServerStatus(String address, int port) {

        try {
            InetAddress serverIp = InetAddress.getByName(address);
            SourceServer server = new SourceServer(serverIp.getHostAddress(), port);
            server.initialize();

            HashMap<String, Object> serverInfo = server.getServerInfo();

            return ServerStatus
                    .builder()
                    .status(ServerState.RUNNING)
                    .currentPlayers(server.getPlayers().size())
                    .maxPlayers(getMaxPlayers((byte) serverInfo.get("maxPlayers")))
                    .currentMap((String) serverInfo.get("mapName"))
                    .currentGamemode((String) serverInfo.get("gameDescription"))
                    .build();

        } catch (SteamCondenserException | TimeoutException ignored) {
        } catch (UnsupportedOperationException | UnknownHostException e) {
            return ServerStatus
                    .builder()
                    .status(ServerState.UNKNOWN)
                    .build();
        }

        return ServerStatus
                .builder()
                .status(ServerState.STOPPED)
                .build();
    }

    @Override
    public int getDefaultServerPort() {
        return DEFAULT_PORT;
    }

    private Integer getMaxPlayers(byte maxPlayerByte) {
        // source engine based servers return the maxPlayers as two-complement of a byte,
        // so we have to convert it to a unsigned int
        return (int) maxPlayerByte & 0xff;
    }

    private List<String> getSupportedProviders() {
        List<String> supportedGames = new ArrayList<>(SupportedGames.getSupportedGames().values());
        Collections.sort(supportedGames);
        return supportedGames;
    }
}
