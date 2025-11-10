package com.cosmosboard.fmh.dto.response.fms.blackbook.search;

import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchVehicleData;
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
@Schema(description = "FMS Blackbook Search Response DTO")
public class BlackbookSearchResponse {
    @Schema(name = "code", description = "Status code", example = "200")
    private Integer code;

    @Schema(name = "success", description = "Success", example = "true")
    private Boolean success;

    @Schema(name = "message", description = "Message", example = "Lorem ipsum")
    private String message;

    @ArraySchema(schema = @Schema(name = "Vehicle data", implementation = ManheimSearchVehicleData.class))
    private List<BlackbookSearchVehicleData> data;
}
