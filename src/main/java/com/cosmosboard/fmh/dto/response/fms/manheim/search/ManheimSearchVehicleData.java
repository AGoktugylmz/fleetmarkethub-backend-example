package com.cosmosboard.fmh.dto.response.fms.manheim.search;

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
@Schema(description = "FMS Manheim Search Vehicle Data Response DTO")
public class ManheimSearchVehicleData {
    @Schema(name = "Vehicle Info", implementation = ManheimSearchVehicleInfo.class)
    private ManheimSearchVehicleInfo vehicle;

    @ArraySchema(schema = @Schema(name = "Vehicle item", implementation = ManheimSearchVehicleItem.class))
    private List<ManheimSearchVehicleItem> items;
}
