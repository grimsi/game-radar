package de.grimsi.gameradar.backend.api;

import de.grimsi.gameradar.backend.annotations.IsGameserverManager;
import de.grimsi.gameradar.backend.dto.GameServerDto;
import de.grimsi.gameradar.backend.validations.GameServerValidations;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Api(tags = {"gameservers"}, description = "Endpoint for users to retrieve available game servers")
@RequestMapping("/v1/gameservers")
public interface GameServerApi {

    @ApiOperation(value = "Get all game servers", authorizations = {@Authorization(value = "auth")})
    @GetMapping(produces = {"application/json"})
    ResponseEntity<List<GameServerDto>> getGameServers(
            @ApiParam(name = "host", value = "The server host.")
            @RequestParam(required = false) Optional<String> host);

    @ApiOperation(value = "Get all game servers with status", authorizations = {@Authorization(value = "auth")})
    @GetMapping(value = "/status", produces = {"application/json"})
    ResponseEntity<List<GameServerDto>> getGameServerStatuses(
            @ApiParam(name = "host", value = "The server host.")
            @RequestParam(required = false) Optional<String> host);

    @ApiOperation(value = "Create a new game server", authorizations = {@Authorization(value = "auth")})
    @IsGameserverManager
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<GameServerDto> createGameServer(@Validated(GameServerValidations.Create.class) @RequestBody GameServerDto body);

    @ApiOperation(value = "Edit a game server", authorizations = {@Authorization(value = "auth")})
    @IsGameserverManager
    @PatchMapping(value = "/{gameServerId}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<GameServerDto> editGameServer(@Validated(GameServerValidations.Update.class) @RequestBody GameServerDto body, @PathVariable Long gameServerId);

    @ApiOperation(value = "Delete a game server", authorizations = {@Authorization(value = "auth")})
    @IsGameserverManager
    @DeleteMapping(value = "/{gameServerId}")
    ResponseEntity<GameServerDto> deleteGameServer(@PathVariable Long gameServerId);
}
