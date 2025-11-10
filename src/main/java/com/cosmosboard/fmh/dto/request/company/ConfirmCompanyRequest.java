package com.cosmosboard.fmh.dto.request.company;

import com.cosmosboard.fmh.dto.annotation.ValueOfEnum;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class ConfirmCompanyRequest {
    @ValueOfEnum(enumClass = AppConstants.OfferStatusEnum.class)
    @Schema(example = "APPROVED", description = "Status of the user", required = true, name = "status", type = "String",
            allowableValues = {"WAITING", "ACCEPTED", "REJECTED", "CANCELLED", "FINISHED"})
    private String status;

    @Size(max = 500, message = "{max_length}")
    @Schema(example = "Lorem ipsum", description = "Status message of the offer", name = "statusMessage",
            title = "String")
    private String statusMessage;
}
