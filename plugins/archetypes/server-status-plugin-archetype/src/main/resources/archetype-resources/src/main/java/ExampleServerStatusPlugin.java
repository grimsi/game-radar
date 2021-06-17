#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
        package ${package};

import de.grimsi.gameradar.pluginapi.PluginMetaInfo;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerState;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatus;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatusPlugin;

import java.net.URL;
import java.util.Collections;

public class ExampleServerStatusPlugin implements ServerStatusPlugin {

    private final int DEFAULT_PORT = 12345;

    @Override
    public PluginMetaInfo getMetaInfo() {
        return PluginMetaInfo
                .builder()
                .author("${pluginAuthor}")
                .name("${artifactId}")
                .type(ServerStatusPlugin.class)
                .version(getClass().getPackage().getImplementationVersion())
                .description("${pluginDescription}")
                .supportedProviders(Collections.emptyList())
                .license(null)
                .repositoryUrl(null)
                .build();
    }

    @Override
    public ServerStatus getServerStatus(String address, int port) {
        return ServerStatus
                .builder()
                .status(ServerState.STOPPED)
                .currentPlayers(12)
                .maxPlayers(24)
                .currentMap("exampleMap")
                .currentGamemode("exampleGamemode")
                .build();
    }

    @Override
    public int getDefaultServerPort() {
        return DEFAULT_PORT;
    }
}
