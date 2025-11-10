package com.cosmosboard.fmh.dto.request.car;

import com.cosmosboard.fmh.dto.IResource;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateCarRequestExcel implements IResource<CreateCarRequestExcel> {
    @NotBlank(message = "{not_blank}")
    @Schema(example = "d26b284d-c7e7-4308-90e7-59e1395b7af4", description = "Model of the car", name = "Model", title = "modelName")
    private String modelName;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "d26b284d-c7e7-4308-90e7-59e1395b7af4", description = "trim of the car", name = "Trim", title = "trimName")
    private String trimName;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "d26b284d-c7e7-4308-90e7-59e1395b7af4", description = "trim of the car", name = "Trim", title = "trimName")
    private String brandName;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "d26b284d", description = "vin of the car", name = "vin", title = "vin", required = true)
    private String vin;

    @NotNull(message = "{not_blank}")
    @Schema(example = "120000", description = "Mileage of the car in kilometers", name = "mileage", type = "Integer")
    private Integer mileage;

    @NotNull(message = "{not_blank}")
    @Schema(example = "modelYear", description = "modelYear of the car", name = "modelYear", title = "modelYear")
    private Integer modelYear;

    @Schema(example = "defaultMarketValue", description = "Default Market Value of the car", name = "defaultMarketValue")
    private Float defaultMarketValue;

    @Schema(example = "retailMarketValue", description = "Retail Market Value of the car", name = "retailMarketValue")
    private Float retailMarketValue;

    @Schema(example = "4.5", description = "Condition rating of the car on a scale of 1 to 5", name = "condition", type = "Float")
    private Float condition;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "Location ID of the car", name = "locationId", title = "locationId")
    private String locationName;

    @Schema(example = "https://example.com/cr-link", description = "CR Link of the car", name = "crLink")
    private String crLink;

    @Schema(example = "Red", description = "Exterior color of the car", name = "exteriorColor")
    private String exteriorColor;

    @Schema(example = "Black", description = "Interior color of the car", name = "interiorColor")

    private String interiorColor;

    @Schema(example = "SUV", description = "Vehicle type of the car", name = "vehicleType")
    private String vehicleType;

    @Schema(example = "Sunroof, Leather Seats", description = "Equipment of the car", name = "equipment")
    private String equipment;

    @Schema(example = "Minor scratches on the bumper", description = "Detail about the car condition", name = "detail")
    private String detail;

    @Schema(example = "15000", description = "MMR (Manheim Market Report) value of the car", name = "mmr")
    private BigDecimal mmr;

    @Schema(example = "true", description = "Whether the MMR value should be visible", name = "mmrShow")
    private Boolean mmrShow;

    @NotNull(message = "{not_blank}")
    @Schema(example = "NA", description = "Region type of the car", name = "regionType")
    private AppConstants.RegionType regionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "black_book_condition")
    private AppConstants.BlackBookCondition blackBookCondition;
}
