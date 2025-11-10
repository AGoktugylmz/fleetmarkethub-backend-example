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
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateCarRequest implements IResource<CreateCarRequest> {

    @Size(min = 1, max = 50, message = "{min_max_length}")
    @Schema(example = "Lorem", description = "Title of the car", name = "Title", title = "String")
    private String title;

    @Size(min = 1, message = "{min_value}")
    @Schema(example = "Lorem", description = "Content of the car", name = "content", title = "String")
    private String content;

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

    @Schema(example = "unit", description = "unit of the car", name = "unit", title = "unit")
    private String unit;

    @NotNull(message = "{not_blank}")
    @Schema(example = "modelYear", description = "modelYear of the car", name = "modelYear", title = "modelYear")
    private Integer modelYear;

    @Schema(example = "red", description = "exteriorColor of the car", name = "exteriorColor", title = "exteriorColor")
    private String exteriorColor;

    @Schema(example = "white", description = "interiorColor of the car", name = "interiorColor", title = "interiorColor")
    private String interiorColor;

    @Schema(example = "www.lorem.com", description = "Report link for the car details", name = "reportLink", type = "String")
    private String reportLink;

    @NotNull(message = "{not_blank}")
    @Schema(example = "120000", description = "Mileage of the car in kilometers", name = "mileage", type = "Integer")
    private Integer mileage;

    @Schema(example = "10000", description = "Current market value (MMR) of the car", name = "mmr", type = "Float")
    private BigDecimal mmr;

    @Schema(example = "Leather seats, Sunroof, Bluetooth", description = "List of equipment available in the car", name = "equipment", type = "String")
    private String equipment;

    @Schema(example = "4.5", description = "Condition rating of the car on a scale of 1 to 5", name = "condition", type = "Float")
    private Float condition;

    @Schema(example = "defaultMarketValue", description = "Default Market Value of the car", name = "defaultMarketValue")
    private Float defaultMarketValue;

    @Schema(example = "retailMarketValue", description = "Retail Default Market Value of the car", name = "retailMarketValue")
    private Float retailMarketValue;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "Location ID of the car", name = "locationId", title = "locationId")
    private String locationId;

    @Schema(example = "3eae03cf-0a78-4c0a-bcc1-9083e0219c0e", description = "Car Group ID of the car", name = "carGroupId", title = "carGroupId")
    private String carGroupId;

    @Schema(example = "7b9e3c33-69c4-42a1-bdf5-6439f1c91212", description = "Car Class ID of the car", name = "carClassId", title = "carClassId")
    private String carClassId;

    @Schema(example = "true", description = "Whether the MMR value should be visible", name = "mmrShow")
    private Boolean mmrShow;

    @NotNull(message = "{not_blank}")
    @Schema(example = "NA", description = "Region type of the car", name = "regionType")
    private AppConstants.RegionType regionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "black_book_condition")
    private AppConstants.BlackBookCondition blackBookCondition;
}
