package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.dto.GameServerDto;
import de.grimsi.gameradar.backend.dto.ServerStatusDto;
import de.grimsi.gameradar.pluginapi.PluginNotFoundException;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerState;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatus;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatusPlugin;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class GameServerStatusService {

    @Autowired
    private Logger log;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private TaskScheduler executor;

    @Autowired
    private PluginManager pluginManager;

    private final Map<Long, ServerStatusDto> serverStatusCache = new HashMap<>();
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

    public void scheduleServerStatusRefresh(GameServerDto gameServerDto) {
        ScheduledFuture<?> scheduledTask = executor.scheduleWithFixedDelay(() -> refreshServerStatus(gameServerDto), gameServerDto.getRefreshDuration());
        scheduledTasks.put(gameServerDto.getId(), scheduledTask);
    }

    public void removeServerStatusRefresh(Long gameServerId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(gameServerId);

        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            return;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "gameserver.invalid-id");
    }

    public ServerStatusDto refreshServerStatus(GameServerDto gameServerDto) {
        log.debug("refresh server status for server {}", gameServerDto);
        /* find a plugin that is able to provide the server data */
        ServerStatusPlugin plugin = getMatchingPlugin(gameServerDto.getGame());
        ServerStatus serverStatus = plugin.getServerStatus(gameServerDto.getHost(), gameServerDto.getPort());

        /* set the last refresh timestamp */
        ServerStatusDto serverStatusDto = convertToDto(serverStatus);
        serverStatusDto.setLastRefresh(Instant.now());

        /* put the now up-to-date status in the cache */
        serverStatusCache.put(gameServerDto.getId(), serverStatusDto);

        log.debug("server status: {}", serverStatus);
        return serverStatusDto;
    }

    public ServerStatusDto getServerStatus(GameServerDto gameServerDto) {
        log.debug("get server status for server {}", gameServerDto);

        ServerStatusDto serverStatusDto = serverStatusCache.get(gameServerDto.getId());

        if (serverStatusDto != null) {
            // we got a hit in the cache, return it
            log.debug("cache hit for server {}", gameServerDto);
            return serverStatusDto;
        }

        // This means that the first query did not yet succeed, so we return status "UNKNOWN"
        log.debug("no server status in cache for server {}", gameServerDto);

        ServerStatusDto unkownServerStatus = new ServerStatusDto();
        unkownServerStatus.setStatus(ServerState.UNKNOWN);
        unkownServerStatus.setLastRefresh(Instant.now());

        return unkownServerStatus;
    }

    public Integer getDefaultServerPort(String game) {
        return getMatchingPlugin(game).getDefaultServerPort();
    }

    public void checkForMatchingPlugin(String game) {
        getMatchingPlugin(game);
    }

    private ServerStatusPlugin getMatchingPlugin(String game) {
        try {
            log.debug("get server status plugin for game '{}'", game);
            return pluginManager.getPluginByProvider(game, ServerStatusPlugin.class);
        } catch (PluginNotFoundException e) {
            log.debug("no server status plugin for game '{}' installed", game);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gameserver.no-matching-plugin");
        }
    }

    private ServerStatusDto convertToDto(ServerStatus serverStatus) {
        return mapper.map(serverStatus, ServerStatusDto.class);
    }
}
