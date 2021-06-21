package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.configuration.ApplicationProperties;
import de.grimsi.gameradar.backend.dto.GameServerDto;
import de.grimsi.gameradar.backend.dto.ServerStatusDto;
import de.grimsi.gameradar.backend.entity.GameServer;
import de.grimsi.gameradar.backend.repository.GameServerRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServerService extends DatabaseService<GameServer, GameServerDto, GameServerRepository> {

    @Autowired
    private Logger log;

    @Autowired
    private GameServerRepository gameServerRepository;

    @Autowired
    private GameServerStatusService gameServerStatusService;

    @Autowired
    private ApplicationProperties config;

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
        gameServerStatusService.checkForMatchingPlugin(gameServer.getGame());

        if (gameServer.getPort() == null) {
            gameServer.setPort(gameServerStatusService.getDefaultServerPort(gameServer.getGame()));
        }

        if (gameServer.getRefreshDuration() == null) {
            gameServer.setRefreshDuration(config.getServerStatusDefaultRefreshDuration());
        }

        log.debug("create gameserver: {}", gameServer);
        gameServer = gameServerRepository.save(gameServer);

        gameServerDto = convertToDto(gameServer);

        gameServerStatusService.scheduleServerStatusRefresh(gameServerDto);

        return gameServerDto;
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
            gameServerStatusService.checkForMatchingPlugin(gameServerDto.getGame());
            existingGameServer.setGame(gameServerDto.getGame());
        }

        if (gameServerDto.getRefreshDuration() != null) {
            existingGameServer.setRefreshDuration(gameServerDto.getRefreshDuration());
        }

        log.debug("edit gameserver: {}", existingGameServer);
        existingGameServer = gameServerRepository.save(existingGameServer);

        gameServerDto = convertToDto(existingGameServer);

        // Reschedule the server status refresh task
        gameServerStatusService.removeServerStatusRefresh(gameServerDto.getId());
        gameServerStatusService.scheduleServerStatusRefresh(gameServerDto);

        return gameServerDto;
    }

    @Override
    public void delete(Long gameServerId) {
        log.debug("delete gameServer: '{}'", gameServerId);
        gameServerRepository.deleteById(gameServerId);
        gameServerStatusService.removeServerStatusRefresh(gameServerId);
    }

    private List<GameServerDto> getServerStatus(List<GameServerDto> gameServerDtos) {
        return gameServerDtos.stream().map(this::getServerStatus).toList();
    }

    private GameServerDto getServerStatus(GameServerDto gameServerDto) {
        ServerStatusDto serverStatusDto = gameServerStatusService.getServerStatus(gameServerDto);
        gameServerDto.setStatus(serverStatusDto);
        return gameServerDto;
    }
}
