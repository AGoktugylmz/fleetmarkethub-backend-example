package com.cosmosboard.fmh.dto.request.offer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ConfirmOfferRequest {
    @NotBlank(message = "{not_blank}")
    @Pattern(regexp = "ACCEPTED|REJECTED", message = "Invalid value. Should be either ACCEPTED or REJECTED.")
    @Schema(example = "ACCEPTED", description = "Status of the user", required = true, name = "status", type = "String",
            allowableValues = {"WAITING", "ACCEPTED", "REJECTED", "CANCELLED", "FINISHED"})
    private String status;
}
