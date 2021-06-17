package de.grimsi.gameradar.backend.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.grimsi.gameradar.backend.validations.GameServerValidations;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GameServerDto {
    private Long id;

    @NotBlank(message = "gameserver.name.required", groups = {GameServerValidations.Create.class})
    private String name;

    @NotBlank(message = "gameserver.host.required", groups = {GameServerValidations.Create.class})
    private String host;

    private Integer port;

    @NotBlank(message = "gameserver.game.required", groups = {GameServerValidations.Create.class})
    private String game;

    @JsonUnwrapped
    private ServerStatusDto status;
}
