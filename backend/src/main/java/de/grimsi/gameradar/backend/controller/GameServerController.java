package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.api.GameServerApi;
import de.grimsi.gameradar.backend.dto.GameServerDto;
import de.grimsi.gameradar.backend.service.GameServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class GameServerController implements GameServerApi {

    @Autowired
    GameServerService gameServerService;

    @Override
    public ResponseEntity<List<GameServerDto>> getGameServers(Optional<String> host) {
        return new ResponseEntity<>(gameServerService.getAll(host), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GameServerDto>> getGameServerStatuses(Optional<String> host) {
        return new ResponseEntity<>(gameServerService.getAllStatuses(host), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GameServerDto> createGameServer(@Valid GameServerDto body) {
        return new ResponseEntity<>(gameServerService.create(body), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GameServerDto> editGameServer(GameServerDto body, Long gameServerId) {
        return new ResponseEntity<>(gameServerService.edit(gameServerId, body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GameServerDto> deleteGameServer(Long gameServerId) {
        gameServerService.delete(gameServerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
