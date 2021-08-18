package de.grimsi.gameradar.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = {"info"}, description = "Endpoint to retrieve information about the application state")
@RequestMapping("/v1/info")
public interface InfoApi {

    @ApiOperation(value = "Get setup status of the application")
    @GetMapping(value = "/isSetup", produces = {"application/json"})
    ResponseEntity<Boolean> isSetup();

    @ApiOperation(value = "Get the backend version")
    @GetMapping(value = "/version", produces = {"application/json"})
    ResponseEntity<String> version();
}
