package com.cosmosboard.fmh.dto.request.fms.manheim;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManheimSearchRequest {
    @ArraySchema(schema = @Schema(implementation = ManheimVehicleSearchItem.class), minItems = 1)
    private List<ManheimVehicleSearchItem> vehicles;
}
