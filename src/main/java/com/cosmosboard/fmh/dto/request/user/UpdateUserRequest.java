package com.cosmosboard.fmh.dto.request.user;

import com.cosmosboard.fmh.dto.annotation.FieldMatch;
import com.cosmosboard.fmh.dto.annotation.MinListSize;
import com.cosmosboard.fmh.dto.annotation.Password;
import com.cosmosboard.fmh.dto.annotation.ValueOfEnum;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
public class UpdateUserRequest {
    @MinListSize(min = 1, message = "{min_list_size}")
    @Schema(description = "Roles of the user", required = true, name = "roles",
        type = "List<String>", allowableValues = {"ADMIN", "USER", "CONSULTANT"})
    private List<@ValueOfEnum(enumClass = AppConstants.RoleEnum.class) String> roles;

    @Email(message = "{invalid_email}")
    @Size(max = 100, message = "{max_length}")
    @Schema(example = "lorem@ipsum.com", description = "Email of the user", required = true, name = "email",
        type = "String")
    private String email;

    @Size(max = 13, message = "{max_length}")
    @Schema(example = "+905321234567", description = "GSM of the user", required = true, name = "gsm",
        type = "String")
    private String gsm;

    @Password(message = "{invalid_password}")
    @Schema(example = "P@sswd123.", description = "Password of the user", name = "password", type = "String")
    private String password;

    @Schema(example = "P@sswd123.", description = "Password for confirmation", name = "passwordConfirm",
        type = "String")
    private String passwordConfirm;

    @Size(max = 50, message = "{max_length}")
    @Schema(example = "Dr.", description = "Title of the user", name = "title", type = "String")
    private String title;

    @Size(max = 50, message = "{max_length}")
    @Schema(example = "John", description = "Name of the user", required = true, name = "name", type = "String")
    private String name;

    @Size(max = 50, message = "{max_length}")
    @Schema(example = "DOE", description = "Lastname of the user", required = true, name = "lastName", type = "String")
    private String lastName;

    @Schema(example = "true", description = "Is e-mail activated?", name = "isEmailActivated", type = "Boolean")
    private Boolean isEmailActivated;

    @Schema(example = "true", description = "Is GSM number activated?", name = "isGsmActivated", type = "Boolean")
    private Boolean isGsmActivated;

    @Schema(example = "false", description = "Is blocked?", name = "isBlocked", type = "Boolean")
    private Boolean isBlocked;
}
