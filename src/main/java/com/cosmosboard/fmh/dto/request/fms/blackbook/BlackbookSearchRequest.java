package com.cosmosboard.fmh.dto.request.fms.blackbook;

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
public class BlackbookSearchRequest {
    @ArraySchema(schema = @Schema(implementation = BlackbookVehicleSearchItem.class), minItems = 1)
    private List<BlackbookVehicleSearchItem> vehicles;
}
