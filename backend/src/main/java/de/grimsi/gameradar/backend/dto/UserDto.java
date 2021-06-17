package de.grimsi.gameradar.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import de.grimsi.gameradar.backend.validations.UserValidations;
import de.grimsi.gameradar.backend.views.UserView;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Data
public class UserDto {

    @NotNull(message = "user.id.required", groups = {UserValidations.Id.class})
    @JsonView({UserView.Basic.class, UserView.Complete.class, UserView.Management.class})
    private Long id;

    @NotBlank(message = "user.username.required", groups = {UserValidations.Credentials.class, UserValidations.Registration.class})
    @JsonView({UserView.Basic.class, UserView.Complete.class, UserView.Management.class})
    private String username;

    @NotBlank(message = "user.password.required", groups = {UserValidations.Credentials.class, UserValidations.Registration.class, UserValidations.DeleteUser.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "user.newPassword.required", groups = {UserValidations.ResetPassword.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    @NotBlank(message = "user.email.required", groups = {UserValidations.Registration.class})
    @Email(message = "user.email.invalid-format", groups = {UserValidations.Registration.class, UserValidations.UpdateDetails.class})
    @JsonView({UserView.Complete.class})
    private String email;

    @JsonView({UserView.Basic.class, UserView.Complete.class, UserView.Management.class})
    private Locale locale;

    @NotNull(message = "user.roles.required", groups = {UserValidations.UpdateRoles.class})
    @Size(min = 1, message = "user.roles.at-least-one-role-assigned", groups = {UserValidations.UpdateRoles.class})
    @JsonView({UserView.Complete.class, UserView.Management.class})
    private Set<String> roles = new HashSet<>();
}
