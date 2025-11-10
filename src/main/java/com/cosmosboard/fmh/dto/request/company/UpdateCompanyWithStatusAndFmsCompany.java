package com.cosmosboard.fmh.dto.request.company;

import com.cosmosboard.fmh.dto.annotation.CheckUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UpdateCompanyWithStatusAndFmsCompany {
    @Pattern(regexp = "WAITING|APPROVED|REJECTED", message = "Invalid status")
    private String status;

    @CheckUUID(message = "Invalid UUID")
    @Schema(name = "fmsCompanyId", description = "FMS Company ID", type = "String")
    private String fmsCompanyId;
}
