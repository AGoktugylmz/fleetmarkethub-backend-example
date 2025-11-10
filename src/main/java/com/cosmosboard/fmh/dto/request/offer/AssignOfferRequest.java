package com.cosmosboard.fmh.dto.request.offer;

import com.cosmosboard.fmh.dto.annotation.UUID;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class AssignOfferRequest {
    @Schema(example = "assign", description = "operation", required = true, name = "operation", type = "String",
            allowableValues = {"assign", "unAssign", "update"})
    @NotNull
    private String operation;

    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Employee id", name = "employeeId", type = "String")
    @UUID
    private String employeeId;

    @ArraySchema(schema = @Schema(example = "lorem ipsum", description = "An update string", name = "updates", type = "List"))
    private List<@Size(max = 255) String> updates;

    @Schema(example = "ACCEPTED", description = "Status", name = "status", type = "String")
    private String status;
}
