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
@Schema(description = "FMS Blackbook Search Vehicle Item Response DTO")
public class BlackbookSearchVehicleItem {
    @Schema(name = "publishDate", description = "Publish Date", example = "4/11/2025")
    private String publishDate;

    @Schema(name = "dataFreq", description = "Data Frequency", example = "D")
    private String dataFreq;

    @Schema(name = "state", description = "State", example = "CA")
    private String state;

    @Schema(name = "country", description = "Country", example = "US")
    private String country;

    @Schema(name = "vin", description = "Vehicle Identification Number", example = "JM1BPAALM")
    private String vin;

    @Schema(name = "uvc", description = "Universal Vehicle Code", example = "2021540340")
    private String uvc;

    @Schema(name = "groupnum", description = "Group Number", example = "4251")
    private String groupnum;

    @Schema(name = "modelYear", description = "Model Year", example = "2021")
    private String modelYear;

    @Schema(name = "make", description = "Vehicle Make", example = "Mazda")
    private String make;

    @Schema(name = "model", description = "Vehicle Model", example = "Mazda3")
    private String model;

    @Schema(name = "series", description = "Vehicle Series", example = "Premium")
    private String series;

    @Schema(name = "style", description = "Vehicle Style", example = "4D Sedan FWD at")
    private String style;

    @Schema(name = "mileageCat", description = "Mileage Category", example = "B")
    private String mileageCat;

    @Schema(name = "classCode", description = "Class Code", example = "A")
    private String classCode;

    @Schema(name = "className", description = "Class Name", example = "Small Car")
    private String className;

    @Schema(name = "descriptionScore", description = "Description Score")
    private String descriptionScore;

    @Schema(name = "firstValuesFlag", description = "First Values Flag", example = "false")
    private Boolean firstValuesFlag;

    @Schema(name = "riskScore", description = "Risk Score")
    private String riskScore;

    @Schema(name = "baseWholeXclean", description = "Base Wholesale Extra Clean Value", example = "20575")
    private Integer baseWholeXclean;

    @Schema(name = "mileageWholeXclean", description = "Mileage Adjustment for Wholesale Extra Clean", example = "0")
    private Integer mileageWholeXclean;

    @Schema(name = "addDeductWholeXclean", description = "Add/Deduct for Wholesale Extra Clean", example = "0")
    private Integer addDeductWholeXclean;

    @Schema(name = "regionalWholeXclean", description = "Regional Adjustment for Wholesale Extra Clean", example = "-100")
    private Integer regionalWholeXclean;

    @Schema(name = "adjustedWholeXclean", description = "Adjusted Wholesale Extra Clean Value", example = "20475")
    private Integer adjustedWholeXclean;

    @Schema(name = "finalWholeXclean", description = "Final Wholesale Extra Clean Value", example = "20475")
    private Integer finalWholeXclean;

    @Schema(name = "baseWholeClean", description = "Base Wholesale Clean Value", example = "18925")
    private Integer baseWholeClean;

    @Schema(name = "mileageWholeClean", description = "Mileage Adjustment for Wholesale Clean", example = "275")
    private Integer mileageWholeClean;

    @Schema(name = "addDeductWholeClean", description = "Add/Deduct for Wholesale Clean", example = "0")
    private Integer addDeductWholeClean;

    @Schema(name = "regionalWholeClean", description = "Regional Adjustment for Wholesale Clean", example = "-100")
    private Integer regionalWholeClean;

    @Schema(name = "adjustedWholeClean", description = "Adjusted Wholesale Clean Value", example = "19100")
    private Integer adjustedWholeClean;

    @Schema(name = "finalWholeClean", description = "Final Wholesale Clean Value", example = "19100")
    private Integer finalWholeClean;

    @Schema(name = "baseWholeAvg", description = "Base Wholesale Average Value", example = "16650")
    private Integer baseWholeAvg;

    @Schema(name = "mileageWholeAvg", description = "Mileage Adjustment for Wholesale Average", example = "525")
    private Integer mileageWholeAvg;

    @Schema(name = "addDeductWholeAvg", description = "Add/Deduct for Wholesale Average", example = "0")
    private Integer addDeductWholeAvg;

    @Schema(name = "regionalWholeAvg", description = "Regional Adjustment for Wholesale Average", example = "-100")
    private Integer regionalWholeAvg;

    @Schema(name = "adjustedWholeAvg", description = "Adjusted Wholesale Average Value", example = "17075")
    private Integer adjustedWholeAvg;

    @Schema(name = "finalWholeAvg", description = "Final Wholesale Average Value", example = "17075")
    private Integer finalWholeAvg;

    @Schema(name = "baseWholeRough", description = "Base Wholesale Rough Value", example = "14475")
    private Integer baseWholeRough;

    @Schema(name = "mileageWholeRough", description = "Mileage Adjustment for Wholesale Rough", example = "775")
    private Integer mileageWholeRough;

    @Schema(name = "addDeductWholeRough", description = "Add/Deduct for Wholesale Rough", example = "0")
    private Integer addDeductWholeRough;

    @Schema(name = "regionalWholeRough", description = "Regional Adjustment for Wholesale Rough", example = "-100")
    private Integer regionalWholeRough;

    @Schema(name = "adjustedWholeRough", description = "Adjusted Wholesale Rough Value", example = "15150")
    private Integer adjustedWholeRough;

    @Schema(name = "finalWholeRough", description = "Final Wholesale Rough Value", example = "15150")
    private Integer finalWholeRough;

    @Schema(name = "baseRetailXclean", description = "Base Retail Extra Clean Value", example = "24475")
    private Integer baseRetailXclean;

    @Schema(name = "mileageRetailXclean", description = "Mileage Adjustment for Retail Extra Clean", example = "0")
    private Integer mileageRetailXclean;

    @Schema(name = "addDeductRetailXclean", description = "Add/Deduct for Retail Extra Clean", example = "0")
    private Integer addDeductRetailXclean;

    @Schema(name = "regionalRetailXclean", description = "Regional Adjustment for Retail Extra Clean", example = "-100")
    private Integer regionalRetailXclean;

    @Schema(name = "adjustedRetailXclean", description = "Adjusted Retail Extra Clean Value", example = "24375")
    private Integer adjustedRetailXclean;

    @Schema(name = "finalRetailXclean", description = "Final Retail Extra Clean Value", example = "24375")
    private Integer finalRetailXclean;

    @Schema(name = "baseRetailClean", description = "Base Retail Clean Value", example = "22750")
    private Integer baseRetailClean;

    @Schema(name = "mileageRetailClean", description = "Mileage Adjustment for Retail Clean", example = "275")
    private Integer mileageRetailClean;

    @Schema(name = "addDeductRetailClean", description = "Add/Deduct for Retail Clean", example = "0")
    private Integer addDeductRetailClean;

    @Schema(name = "regionalRetailClean", description = "Regional Adjustment for Retail Clean", example = "-100")
    private Integer regionalRetailClean;

    @Schema(name = "adjustedRetailClean", description = "Adjusted Retail Clean Value", example = "22925")
    private Integer adjustedRetailClean;

    @Schema(name = "finalRetailClean", description = "Final Retail Clean Value", example = "22925")
    private Integer finalRetailClean;

    @Schema(name = "baseRetailAvg", description = "Base Retail Average Value", example = "19000")
    private Integer baseRetailAvg;

    @Schema(name = "mileageRetailAvg", description = "Mileage Adjustment for Retail Average", example = "525")
    private Integer mileageRetailAvg;

    @Schema(name = "addDeductRetailAvg", description = "Add/Deduct for Retail Average", example = "0")
    private Integer addDeductRetailAvg;

    @Schema(name = "regionalRetailAvg", description = "Regional Adjustment for Retail Average", example = "-100")
    private Integer regionalRetailAvg;

    @Schema(name = "adjustedRetailAvg", description = "Adjusted Retail Average Value", example = "19425")
    private Integer adjustedRetailAvg;

    @Schema(name = "finalRetailAvg", description = "Final Retail Average Value", example = "19425")
    private Integer finalRetailAvg;

    @Schema(name = "baseRetailRough", description = "Base Retail Rough Value", example = "16925")
    private Integer baseRetailRough;

    @Schema(name = "mileageRetailRough", description = "Mileage Adjustment for Retail Rough", example = "775")
    private Integer mileageRetailRough;

    @Schema(name = "addDeductRetailRough", description = "Add/Deduct for Retail Rough", example = "0")
    private Integer addDeductRetailRough;

    @Schema(name = "regionalRetailRough", description = "Regional Adjustment for Retail Rough", example = "-100")
    private Integer regionalRetailRough;

    @Schema(name = "adjustedRetailRough", description = "Adjusted Retail Rough Value", example = "17600")
    private Integer adjustedRetailRough;

    @Schema(name = "finalRetailRough", description = "Final Retail Rough Value", example = "17600")
    private Integer finalRetailRough;

    @Schema(name = "baseTradeinClean", description = "Base Trade-in Clean Value", example = "19335")
    private Integer baseTradeinClean;

    @Schema(name = "mileageTradeinClean", description = "Mileage Adjustment for Trade-in Clean", example = "275")
    private Integer mileageTradeinClean;

    @Schema(name = "addDeductTradeinClean", description = "Add/Deduct for Trade-in Clean", example = "0")
    private Integer addDeductTradeinClean;

    @Schema(name = "regionalTradeinClean", description = "Regional Adjustment for Trade-in Clean", example = "-100")
    private Integer regionalTradeinClean;

    @Schema(name = "adjustedTradeinClean", description = "Adjusted Trade-in Clean Value", example = "19510")
    private Integer adjustedTradeinClean;

    @Schema(name = "finalTradeinClean", description = "Final Trade-in Clean Value", example = "19510")
    private Integer finalTradeinClean;

    @Schema(name = "baseTradeinAvg", description = "Base Trade-in Average Value", example = "17215")
    private Integer baseTradeinAvg;

    @Schema(name = "mileageTradeinAvg", description = "Mileage Adjustment for Trade-in Average", example = "525")
    private Integer mileageTradeinAvg;

    @Schema(name = "addDeductTradeinAvg", description = "Add/Deduct for Trade-in Average", example = "0")
    private Integer addDeductTradeinAvg;

    @Schema(name = "regionalTradeinAvg", description = "Regional Adjustment for Trade-in Average", example = "-100")
    private Integer regionalTradeinAvg;

    @Schema(name = "adjustedTradeinAvg", description = "Adjusted Trade-in Average Value", example = "17640")
    private Integer adjustedTradeinAvg;

    @Schema(name = "finalTradeinAvg", description = "Final Trade-in Average Value", example = "17640")
    private Integer finalTradeinAvg;

    @Schema(name = "baseTradeinRough", description = "Base Trade-in Rough Value", example = "13675")
    private Integer baseTradeinRough;

    @Schema(name = "mileageTradeinRough", description = "Mileage Adjustment for Trade-in Rough", example = "775")
    private Integer mileageTradeinRough;

    @Schema(name = "addDeductTradeinRough", description = "Add/Deduct for Trade-in Rough", example = "0")
    private Integer addDeductTradeinRough;

    @Schema(name = "regionalTradeinRough", description = "Regional Adjustment for Trade-in Rough", example = "-100")
    private Integer regionalTradeinRough;

    @Schema(name = "adjustedTradeinRough", description = "Adjusted Trade-in Rough Value", example = "14350")
    private Integer adjustedTradeinRough;

    @Schema(name = "finalTradeinRough", description = "Final Trade-in Rough Value", example = "14350")
    private Integer finalTradeinRough;

    @Schema(name = "conditionAdjustedWholesale", description = "Condition Adjusted Wholesale", example = "16500.00")
    private Double conditionAdjustedWholesale;

    @Schema(name = "region2", description = "Region 2 Adjustment", example = "50")
    private Integer region2;

    @Schema(name = "region3", description = "Region 3 Adjustment", example = "-100")
    private Integer region3;

    @Schema(name = "region4", description = "Region 4 Adjustment", example = "-75")
    private Integer region4;

    @Schema(name = "region5", description = "Region 5 Adjustment", example = "0")
    private Integer region5;

    @Schema(name = "region6", description = "Region 6 Adjustment", example = "0")
    private Integer region6;

    @Schema(name = "msrp", description = "Manufacturer's Suggested Retail Price", example = "27000")
    private Integer msrp;

    @Schema(name = "retailEquipped", description = "Retail Equipped Price", example = "28285")
    private Integer retailEquipped;

    @Schema(name = "priceIncludes", description = "Features Included in Price", example = "AC AT")
    private String priceIncludes;

    @Schema(name = "wheelBase", description = "Wheel Base in Inches", example = "107.3")
    private Double wheelBase;

    @Schema(name = "tireSize", description = "Tire Size", example = "2154/45R18")
    private String tireSize;

    @Schema(name = "gvw", description = "Gross Vehicle Weight", example = "3071.0")
    private Double gvw;

    @Schema(name = "seatCap", description = "Seating Capacity", example = "5")
    private String seatCap;

    @Schema(name = "seats", description = "Seat Type", example = "Sport")
    private String seats;

    @Schema(name = "fuelType", description = "Fuel Type", example = "Gas")
    private String fuelType;

    @Schema(name = "fuelCap", description = "Fuel Capacity in Gallons", example = "13.2")
    private String fuelCap;

    @Schema(name = "fuelDelivery", description = "Fuel Delivery System", example = "Direct injection")
    private String fuelDelivery;

    @Schema(name = "hwyMpg", description = "Highway MPG", example = "35")
    private String hwyMpg;

    @Schema(name = "cityMpg", description = "City MPG", example = "26")
    private String cityMpg;

    @Schema(name = "engineDescription", description = "Engine Description", example = "2.5L I-4 DI DOHC")
    private String engineDescription;

    @Schema(name = "cylinders", description = "Number of Cylinders", example = "4")
    private String cylinders;

    @Schema(name = "engineDisplacement", description = "Engine Displacement", example = "2.5L")
    private String engineDisplacement;

    @Schema(name = "baseHp", description = "Base Horsepower", example = "186 @ 6000")
    private String baseHp;

    @Schema(name = "taxableHp", description = "Taxable Horsepower", example = "19.6")
    private Double taxableHp;

    @Schema(name = "torque", description = "Torque", example = "186 @ 4000")
    private String torque;

    @Schema(name = "transmission", description = "Transmission Type", example = "A")
    private String transmission;

    @Schema(name = "drivetrain", description = "Drivetrain", example = "FWD")
    private String drivetrain;

    @Schema(name = "numGears", description = "Number of Gears", example = "6")
    private String numGears;

    @Schema(name = "extDoors", description = "Number of External Doors", example = "4")
    private String extDoors;

    @Schema(name = "moonSunroof", description = "Moon/Sunroof Description", example = "Moonroof, power")
    private String moonSunroof;

    @Schema(name = "airbags", description = "Airbag Description", example = "Side Curtain; Supplemental Restraint System")
    private String airbags;

    @Schema(name = "antiCorrosionWarranty", description = "Anti-Corrosion Warranty", example = "5-year/Unlimited-mile")
    private String antiCorrosionWarranty;

    @Schema(name = "basicWarranty", description = "Basic Warranty", example = "3-year/36,000-mile, Limited")
    private String basicWarranty;

    @Schema(name = "powertrainWarranty", description = "Powertrain Warranty", example = "5-year/60,000-mile, Limited")
    private String powertrainWarranty;

    @Schema(name = "roadAssistWarranty", description = "Roadside Assistance Warranty", example = "3-year/36,000-mile")
    private String roadAssistWarranty;

    @ArraySchema(
        schema = @Schema(name = "List of Add/Deduct Items", implementation = ManheimSearchVehicleData.class)
    )
    private List<BlackbookSearchAddDeductResponse> addDeductList;

    @ArraySchema(
        schema = @Schema(name = "List of Mileage Adjustments", implementation = ManheimSearchVehicleData.class)
    )
    private List<BlackbookSearchMileageResponse> mileageList;

    @Schema(name = "modelNumberList", description = "List of Model Numbers", example = "[\"PR\"]")
    private List<String> modelNumberList;
}
