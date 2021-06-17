package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import de.grimsi.gameradar.backend.dto.GameServerDto;
import de.grimsi.gameradar.backend.dto.ServerStatusDto;
import de.grimsi.gameradar.backend.entity.GameServer;
import de.grimsi.gameradar.backend.repository.GameServerRepository;
import de.grimsi.gameradar.pluginapi.PluginNotFoundException;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatus;
import de.grimsi.gameradar.pluginapi.serverstatus.ServerStatusPlugin;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameServerService extends DatabaseService<GameServer, GameServerDto, GameServerRepository> {

    @Autowired
    private Logger log;

    @Autowired
    private GameServerRepository gameServerRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private ApplicationProperties config;

    private final Map<Long, ServerStatusDto> serverStatusCache = new HashMap<>();

    public List<GameServerDto> getAll(Optional<String> host) {
        if (host.isPresent()) {
            log.debug("get all game servers with host {}", host.get());
            List<GameServer> gameServers = gameServerRepository.findAllByHost(host.get());
            return convertToDto(gameServers);
        }

        log.debug("get all game servers");
        List<GameServer> gameServers = gameServerRepository.findAll();
        return convertToDto(gameServers);
    }

    public List<GameServerDto> getAllStatuses(Optional<String> host) {
        return getServerStatus(getAll(host));
    }

    @Override
    public GameServerDto create(GameServerDto gameServerDto) {
        GameServer gameServer = convertToEntity(gameServerDto);

        /* Check if a plugin is installed that can handle the specified game */
        ServerStatusPlugin matchingPlugin = getMatchingPlugin(gameServer.getGame());

        if (gameServer.getPort() == null) {
            gameServer.setPort(matchingPlugin.getDefaultServerPort());
        }

        log.debug("create gameserver: {}", gameServer);
        gameServer = gameServerRepository.save(gameServer);

        return convertToDto(gameServer);
    }

    @Override
    public GameServerDto edit(Long gameServerId, GameServerDto gameServerDto) {
        GameServer existingGameServer = getEntityById(gameServerId);

        if (gameServerDto.getName() != null) {
            existingGameServer.setName(gameServerDto.getName());
        }

        if (gameServerDto.getHost() != null) {
            existingGameServer.setHost(gameServerDto.getHost());
        }

        if (gameServerDto.getPort() != null) {
            existingGameServer.setPort(gameServerDto.getPort());
        }

        if (gameServerDto.getGame() != null) {
            getMatchingPlugin(gameServerDto.getGame());
            existingGameServer.setGame(gameServerDto.getGame());
        }

        log.debug("edit gameserver: {}", existingGameServer);
        existingGameServer = gameServerRepository.save(existingGameServer);

        return convertToDto(existingGameServer);
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

    private List<GameServerDto> getServerStatus(List<GameServerDto> gameServerDtos) {
        return gameServerDtos.parallelStream().map(this::getServerStatus).toList();
    }

    private GameServerDto getServerStatus(GameServerDto gameServerDto) {

        /* first check if we a value already in the cache */
        if (serverStatusCache.containsKey(gameServerDto.getId())) {
            log.debug("cache hit for gameserver {}", gameServerDto.getId());

            ServerStatusDto cachedStatus = serverStatusCache.get(gameServerDto.getId());
            Duration minRefreshDuration = Duration.parse(config.getServerStatusMinRefreshDuration());
            Instant lastRefreshed = cachedStatus.getLastRefresh();

            /* check if the status is still up-to-date, return if it is */
            if (Instant.now().isBefore(lastRefreshed.plus(minRefreshDuration))) {
                log.debug("cache value for gameserver {} up-to-date, skipping refresh and returning cached value", gameServerDto.getId());

                gameServerDto.setStatus(cachedStatus);
                return gameServerDto;
            }

            log.debug("cache value for gameserver {} outdated, forcing refresh", gameServerDto.getId());
        }

        log.debug("get server status for server {}", gameServerDto);
        /* find a plugin that is able to provide the server data */
        ServerStatusPlugin plugin = getMatchingPlugin(gameServerDto.getGame());
        ServerStatus serverStatus = plugin.getServerStatus(gameServerDto.getHost(), gameServerDto.getPort());

        /* set the last refresh timestamp */
        ServerStatusDto serverStatusDto = convertToDto(serverStatus);
        serverStatusDto.setLastRefresh(Instant.now());

        /* put the now up-to-date status in the cache */
        serverStatusCache.put(gameServerDto.getId(), serverStatusDto);

        log.debug("server status: {}", serverStatus);
        gameServerDto.setStatus(serverStatusDto);

        return gameServerDto;
    }

    private void updateServerStatusCache(ServerStatusDto serverStatusDto) {

    }

    private ServerStatusDto convertToDto(ServerStatus serverStatus) {
        return mapper.map(serverStatus, ServerStatusDto.class);
    }
}
