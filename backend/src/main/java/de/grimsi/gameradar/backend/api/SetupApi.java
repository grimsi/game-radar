package de.grimsi.gameradar.backend.api;

import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.dto.UserDto;
import de.grimsi.gameradar.backend.validations.UserValidations;
import de.grimsi.gameradar.backend.views.UserView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = {"setup"}, description = "Endpoint to setup the application after the first start")
@RequestMapping("/v1/setup")
public interface SetupApi {

    @ApiOperation(value = "Create an initial superadmin user", notes = "Only available until the initial Superadmin user has been created")
    @JsonView(UserView.Complete.class)
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<UserDto> registerAdmin(@Validated(UserValidations.Registration.class) @RequestBody UserDto body);

}
