package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.dto.GameDto;
import de.grimsi.gameradar.backend.repository.GameRepository;
import de.grimsi.gameradar.backend.entity.Game;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GameService extends DatabaseService<Game, GameDto, GameRepository> {

    @Autowired
    private Logger log;

    @Autowired
    private GameRepository gameRepository;

    @Override
    public GameDto edit(Long gameId, GameDto gameDto) {
        Game existingGame = getEntityById(gameId);

        if (gameDto.getName() != null) {
            existingGame.setName(gameDto.getName());
        }

        if (gameDto.getGenres() != null) {
            existingGame.setGenres(gameDto.getGenres());
        }

        if (gameDto.getTags() != null) {
            existingGame.setTags(gameDto.getTags());
        }

        if (gameDto.getMinTeams() != null) {
            existingGame.setMinTeams(gameDto.getMinTeams());
        }

        if (gameDto.getMaxTeams() != null) {
            existingGame.setMaxTeams(gameDto.getMaxTeams());
        }

        if (gameDto.getMinTeamSize() != null) {
            existingGame.setMinTeamSize(gameDto.getMinTeamSize());
        }

        if (gameDto.getMaxTeamSize() != null) {
            existingGame.setMaxTeamSize(gameDto.getMaxTeamSize());
        }

        /* validate the new properties because @SpELAssert doesn't work on entity level */
        if (existingGame.getMinTeams() > existingGame.getMaxTeams()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "game.teams.max-teams-bigger-than-min-teams");
        }

        if (existingGame.getMinTeamSize() > existingGame.getMaxTeamSize()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "game.teams.max-team-size-bigger-than-min-team-size");
        }

        log.debug("edit game: {}", existingGame);
        existingGame = gameRepository.save(existingGame);

        return convertToDto(existingGame);
    }

    public boolean isValidId(Long id) {
        log.debug("check if game id '{}' is valid", id);
        return gameRepository.findById(id).isPresent();
    }

    public boolean areValidIds(List<GameDto> games) {
        return games.stream().allMatch(g -> isValidId(g.getId()));
    }
}
