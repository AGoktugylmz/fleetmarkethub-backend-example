package com.cosmosboard.fmh.dto.request.employee;

import com.cosmosboard.fmh.dto.annotation.FieldMatch;
import com.cosmosboard.fmh.dto.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
public class EmployeeRegisterRequest {
    @NotBlank(message = "{not_blank}")
    @Email(message = "{invalid_email}")
    @Size(max = 100, message = "{max_length}")
    @Schema(example = "lorem@ipsum.com", description = "Email of the user", required = true, name = "email", type = "String")
    private String email;

    @NotBlank(message = "{not_blank}")
    @Password(message = "{invalid_password}")
    @Schema(example = "P@sswd123.", description = "Password of the user", required = true, name = "password", type = "String")
    private String password;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "P@sswd123.", description = "Password for confirmation", required = true, name = "passwordConfirm", type = "String")
    private String passwordConfirm;

    @NotBlank(message = "{not_blank}")
    @Size(max = 50, message = "{max_length}")
    @Schema(example = "John", description = "Name of the user", required = true, name = "name", type = "String")
    private String name;

    @NotBlank(message = "{not_blank}")
    @Size(max = 50, message = "{max_length}")
    @Schema(example = "DOE", description = "Lastname of the user", required = true, name = "lastName", type = "String")
    private String lastName;

}