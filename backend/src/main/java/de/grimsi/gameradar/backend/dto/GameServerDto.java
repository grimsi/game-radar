package de.grimsi.gameradar.backend.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.grimsi.gameradar.backend.validations.GameServerValidations;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.Duration;

@Data
public class GameServerDto {
    private Long id;

    @NotBlank(message = "gameserver.name.required", groups = {GameServerValidations.Create.class})
    private String name;

    @NotBlank(message = "gameserver.host.required", groups = {GameServerValidations.Create.class})
    private String host;

    @Min(value = 0, message = "gameserver.port.not-negative", groups = {GameServerValidations.Create.class, GameServerValidations.Update.class})
    private Integer port;

    @NotBlank(message = "gameserver.game.required", groups = {GameServerValidations.Create.class})
    private String game;

    private Duration refreshDuration;

    @JsonUnwrapped
    private ServerStatusDto status;
}
