package com.cosmosboard.fmh.dto.request.company;

import com.cosmosboard.fmh.dto.annotation.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CompanyOperationRequest {
    @Schema(example = "assign", description = "operation", required = true, name = "operation", type = "String",
            allowableValues = {"assign", "unAssign", "update"})
    @NotNull
    private String operation;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Company id", name = "companyId", type = "String")
    @UUID
    private String companyId;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "User id", name = "userId", type = "String")
    @UUID
    private String userId;

    @Schema(example = "true", description = "Is owner", name = "isOwner", type = "boolean")
    private boolean owner;
}
