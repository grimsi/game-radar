package de.grimsi.gameradar.backend.dev.reset;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile("dev")
@Api(tags = {"reset"}, description = "Endpoint to perform a reset of the application")
@RequestMapping("/v1/reset")
public interface ResetApi {

    @ApiOperation(value = "Reset the application")
    @GetMapping(value = "")
    ResponseEntity<Void> reset();
}
