package com.cosmosboard.fmh.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import static com.cosmosboard.fmh.util.AppConstants.GSM_ACTIVATION_TOKEN_LENGTH;

@Getter
@Setter
public class ActivateGsmRequest {
    @NotNull(message = "{not_blank}")
    @Size(min = GSM_ACTIVATION_TOKEN_LENGTH, max = GSM_ACTIVATION_TOKEN_LENGTH, message = "{min_max_length}")
    @Schema(example = "123456", description = "Token", required = true, name = "token", type = "String")
    private String token;
}
