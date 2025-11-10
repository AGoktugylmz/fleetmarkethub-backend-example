package com.cosmosboard.fmh.dto.request.car;

import com.cosmosboard.fmh.dto.annotation.ValueOfEnum;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ConfirmCarRequest {
    @ValueOfEnum(enumClass = AppConstants.CarStatusEnum.class)
    @Schema(example = "APPROVED", description = "Status of the user", required = true, name = "status", type = "String",
        allowableValues = {"WAITING", "APPROVED", "REJECTED", "CANCELLED"})
    private String status;

    @Size(max = 500, message = "{max_length}")
    @Schema(example = "Lorem ipsum", description = "Status message of the Car", name = "statusMessage",
        title = "String")
    private String statusMessage;
}
