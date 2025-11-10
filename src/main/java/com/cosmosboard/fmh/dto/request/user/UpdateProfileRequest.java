package com.cosmosboard.fmh.dto.request.user;

import com.cosmosboard.fmh.dto.IResource;
import com.cosmosboard.fmh.dto.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateProfileRequest implements IResource<UpdateProfileRequest> {
    @Email(message = "{invalid_email}")
    @Size(min = 1, max = 100, message = "{min_max_length}")
    @Schema(example = "lorem@ipsum.com", description = "Email of the user", name = "email",
        type = "String")
    private String email;

    @Size(max = 13, message = "{max_length}")
    @Schema(example = "+905321234567", description = "GSM of the user", name = "gsm",
        type = "String")
    private String gsm;

    @Size(max = 50, message = "{max_length}")
    @Schema(example = "Dr.", description = "Title of the user", name = "title", type = "String")
    private String title;

    @Size(min = 1, max = 50, message = "{min_max_length}")
    @Schema(example = "John", description = "Name of the user", name = "name", type = "String")
    private String name;

    @Size(min = 1, max = 50, message = "{max_length}")
    @Schema(example = "DOE", description = "Lastname of the user", name = "lastName", type = "String")
    private String lastName;

    @Schema(example = "P@sswd123.", description = "Old Password", name = "oldPassword", type = "String")
    private String oldPassword;

    @Password(message = "{invalid_password}")
    @Schema(example = "P@sswd1234.", description = "New Password", name = "password", type = "String")
    private String password;

    @Schema(example = "P@sswd1234.", description = "New Password for confirmation",
            name = "passwordConfirm", type = "String")
    private String passwordConfirm;
}
