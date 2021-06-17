package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.api.GameApi;
import de.grimsi.gameradar.backend.dto.GameDto;
import de.grimsi.gameradar.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameApiController implements GameApi {

    @Autowired
    GameService gameService;

    @Override
    public ResponseEntity<List<GameDto>> getGames() {
        return new ResponseEntity<>(gameService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GameDto> createGame(GameDto body) {
        return new ResponseEntity<>(gameService.create(body), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GameDto> editGame(GameDto body, Long gameId) {
        return new ResponseEntity<>(gameService.edit(gameId, body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteGame(Long gameId) {
        gameService.delete(gameId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
