package com.cosmosboard.fmh.dto.request.car;

import com.cosmosboard.fmh.dto.IResource;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class PreviewCarRequest implements IResource<PreviewCarRequest> {

    @NotBlank(message = "{not_blank}")
    @Schema(example = "d26b284d", description = "vin of the car", name = "vin", title = "vin", required = true)
    private String vin;

    @NotNull(message = "{not_blank}")
    @Schema(example = "120000", description = "Mileage of the car in kilometers", name = "mileage", type = "Integer")
    private Integer mileage;

    @Schema(example = "4.5", description = "Condition rating of the car on a scale of 1 to 5", name = "condition", type = "Float")
    private Float condition;

    @Schema(example = "10000", description = "Current market value (MMR) of the car", name = "mmr", type = "Float")
    private BigDecimal mmr;

    @NotNull(message = "{not_blank}")
    @Schema(example = "NA", description = "Region type of the car", name = "regionType")
    private AppConstants.RegionType regionType;
}
