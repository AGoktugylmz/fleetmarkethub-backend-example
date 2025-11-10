package com.cosmosboard.fmh.dto.response.fms.blackbook.search;

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
@Schema(description = "FMS Blackbook Search Vehicle Data Response DTO")
public class BlackbookSearchVehicleData {
    @Schema(name = "Vehicle Info", implementation = BlackbookSearchVehicleInfo.class)
    private BlackbookSearchVehicleInfo vehicle;

    @ArraySchema(schema = @Schema(name = "Vehicle item", implementation = BlackbookSearchVehicleItem.class))
    private List<BlackbookSearchVehicleItem> items;
}
