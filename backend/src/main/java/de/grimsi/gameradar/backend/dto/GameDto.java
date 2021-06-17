package de.grimsi.gameradar.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.annotations.SpELAssert;
import de.grimsi.gameradar.backend.enums.GameGenre;
import de.grimsi.gameradar.backend.validations.GameValidations;
import de.grimsi.gameradar.backend.views.GameView;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@SpELAssert(value = "minTeams <= maxTeams", message = "game.teams.max-teams-bigger-than-min-teams")
@SpELAssert(value = "minTeamSize <= maxTeamSize", message = "game.teams.max-team-size-bigger-than-min-team-size")
public class GameDto {
    @JsonView({GameView.Summary.class, GameView.Complete.class})
    @NotNull(message = "game.id.required", groups = {GameValidations.Id.class})
    private Long id;

    @JsonView({GameView.Summary.class, GameView.Complete.class})
    @NotBlank(message = "game.name.required", groups = {GameValidations.Create.class})
    private String name;

    @NotNull(message = "game.genres.required", groups = {GameValidations.Create.class})
    @Size(min = 1, message = "game.genres.at-least-one", groups = {GameValidations.Create.class, GameValidations.Update.class})
    @JsonView({GameView.Complete.class})
    private Set<GameGenre> genres;

    @JsonView({GameView.Complete.class})
    private Set<String> tags = new HashSet<>();

    @NotNull(message = "game.maxPlayers.required", groups = {GameValidations.Create.class})
    @Min(value = 1, message = "game.minTeams.at-least-one", groups = {GameValidations.Create.class, GameValidations.Update.class})
    @JsonView({GameView.Complete.class})
    private Integer minTeams;

    @NotNull(message = "game.maxPlayers.required", groups = {GameValidations.Create.class})
    @Min(value = 1, message = "game.maxTeams.at-least-one", groups = {GameValidations.Create.class, GameValidations.Update.class})
    @JsonView({GameView.Complete.class})
    private Integer maxTeams;

    @NotNull(message = "game.maxTeamSize.required", groups = {GameValidations.Create.class})
    @Min(value = 1, message = "game.minTeamSize.at-least-one", groups = {GameValidations.Create.class, GameValidations.Update.class})
    @JsonView({GameView.Complete.class})
    private Integer minTeamSize;

    @NotNull(message = "game.maxTeamSize.required", groups = {GameValidations.Create.class})
    @Min(value = 1, message = "game.maxTeamSize.at-least-one", groups = {GameValidations.Create.class, GameValidations.Update.class})
    @JsonView({GameView.Complete.class})
    private Integer maxTeamSize;
}
