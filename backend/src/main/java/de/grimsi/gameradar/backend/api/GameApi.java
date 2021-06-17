package de.grimsi.gameradar.backend.api;

import de.grimsi.gameradar.backend.annotations.IsAdmin;
import de.grimsi.gameradar.backend.dto.GameDto;
import de.grimsi.gameradar.backend.validations.GameValidations;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"games"}, description = "Endpoint for admins to manage the games and for users to retrieve available games")
@RequestMapping("/v1/games")
public interface GameApi {

    @ApiOperation(value = "Get a list of all available games", authorizations = {@Authorization(value = "auth")})
    @GetMapping(produces = {"application/json"})
    ResponseEntity<List<GameDto>> getGames();

    @ApiOperation(value = "Add a new game", authorizations = {@Authorization(value = "auth")})
    @IsAdmin
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<GameDto> createGame(@Validated(GameValidations.Create.class) @RequestBody GameDto body);

    @ApiOperation(value = "Edit a game", authorizations = {@Authorization(value = "auth")})
    @IsAdmin
    @PatchMapping(value = "/{gameId}")
    ResponseEntity<GameDto> editGame(@Validated(GameValidations.Update.class) @RequestBody GameDto body, @PathVariable Long gameId);

    @ApiOperation(value = "Delete a game", authorizations = {@Authorization(value = "auth")})
    @IsAdmin
    @DeleteMapping(value = "/{gameId}")
    ResponseEntity<Void> deleteGame(@PathVariable Long gameId);
}

