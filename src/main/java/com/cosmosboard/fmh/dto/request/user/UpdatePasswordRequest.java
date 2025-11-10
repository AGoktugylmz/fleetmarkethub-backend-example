package com.cosmosboard.fmh.dto.request.user;

import com.cosmosboard.fmh.dto.annotation.FieldMatch;
import com.cosmosboard.fmh.dto.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
public class UpdatePasswordRequest {
    @NotBlank(message = "{not_blank}")
    @Schema(example = "P@sswd123.", description = "Old Password", required = true, name = "oldPassword", type = "String")
    private String oldPassword;

    @NotBlank(message = "{not_blank}")
    @Password(message = "{invalid_password}")
    @Schema(example = "P@sswd1234.", description = "New Password", required = true, name = "password", type = "String")
    private String password;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "P@sswd1234.", description = "New Password for confirmation", required = true,
        name = "passwordConfirm", type = "String")
    private String passwordConfirm;
}
