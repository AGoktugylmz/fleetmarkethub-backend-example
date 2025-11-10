package com.cosmosboard.fmh.dto.request.user;

import com.cosmosboard.fmh.dto.annotation.FieldMatch;
import com.cosmosboard.fmh.dto.annotation.MinListSize;
import com.cosmosboard.fmh.dto.annotation.ValueOfEnum;
import com.cosmosboard.fmh.dto.request.auth.RegisterRequest;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
public class CreateUserRequest extends RegisterRequest {
    @NotEmpty(message = "{not_blank}")
    @MinListSize(min = 1, message = "{min_list_size}")
    @Schema(description = "Roles of the user", required = true, name = "roles",
        type = "List<String>", allowableValues = {"ADMIN", "USER", "CONSULTANT"})
    private List<@ValueOfEnum(enumClass = AppConstants.RoleEnum.class) String> roles;

    @Schema(example = "true", description = "Is e-mail activated?", name = "isEmailActivated", type = "Boolean")
    private Boolean isEmailActivated;

    @Schema(example = "true", description = "Is GSM number activated?", name = "isGsmActivated", type = "Boolean")
    private Boolean isGsmActivated;

    @Schema(example = "false", description = "Is blocked?", name = "isBlocked", type = "Boolean")
    private Boolean isBlocked;
}
