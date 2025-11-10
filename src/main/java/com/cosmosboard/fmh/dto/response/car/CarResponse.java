package com.cosmosboard.fmh.dto.response.car;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.address.AddressResponse;
import com.cosmosboard.fmh.dto.response.car.cclass.CarClassResponse;
import com.cosmosboard.fmh.dto.response.car.group.CarGroupResponse;
import com.cosmosboard.fmh.dto.response.car.model.CarModelResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import com.cosmosboard.fmh.dto.response.image.ImageResponse;
import com.cosmosboard.fmh.dto.response.location.LocationResponse;
import com.cosmosboard.fmh.dto.response.marketValue.MarketValueResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.Car;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class CarResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(name = "company", description = "Company of car", nullable = true, type = "CompanyResponse")
    private CompanyResponse company;

    @Schema(name = "address", description = "Address of car", nullable = true, type = "AddressResponse")
    private AddressResponse address;

    @Schema(example = "Lorem", description = "Title of car", name = "title", type = "String")
    private String title;

    @Schema(example = "Lorem ipsum", description = "Content of car", name = "content", type = "String")
    private String content;

    @Schema(example = "WAITING", description = "Status of car", name = "status", type = "String")
    private String status;

    @Schema(example = "Lorem ipsum", description = "Status message of car", name = "statusMessage", type = "String")
    private String statusMessage;

    @Schema(name = "offers", description = "Offers Associated with car", nullable = true, type = "List<OfferResponse>")
    private List<OfferResponse> offers;

    @Schema(name = "carModel", description = "Model of car", nullable = true, type = "CarModelResponse")
    private CarModelResponse carModel;

    @Schema(example = "123123123", description = "VIN of car", name = "vin", type = "String")
    private String vin;

    @Schema(example = "lorem", description = "unit of car", name = "unit", type = "String")
    private String unit;

    @Schema(example = "1999", description = "modelYear of car", name = "modelYear", type = "Integer")
    private Integer modelYear;

    @Schema(name = "carGroup", description = "Car Group of the car", nullable = true, type = "CarGroupResponse")
    private CarGroupResponse carGroup;

    @Schema(name = "carClass", description = "Car Class of the car", nullable = true, type = "CarClassResponse")
    private CarClassResponse carClass;

    @Schema(example = "red", description = "exteriorColor of the car", name = "exteriorColor", title = "exteriorColor")
    private String exteriorColor;

    @Schema(example = "white", description = "interiorColor of the car", name = "interiorColor", title = "interiorColor")
    private String interiorColor;

    @Schema(example = "www.lorem.com", description = "Report link for the car details", name = "reportLink", type = "String")
    private String reportLink;

    @Schema(example = "120000", description = "Mileage of the car in kilometers", name = "mileage", type = "Integer")
    private Integer mileage;

    @Schema(example = "10000", description = "Current market value (MMR) of the car", name = "mmr", type = "Float")
    private Float mmr;

    @Schema(example = "Leather seats, Sunroof, Bluetooth", description = "List of equipment available in the car", name = "equipment", type = "String")
    private String equipment;

    @Schema(example = "4.5", description = "Condition rating of the car on a scale of 1 to 5", name = "condition", type = "Float")
    private Float condition;

    @Schema(example = "1999,9", description = "Default market value of car", name = "defaultMarketValue", type = "Float")
    private Float defaultMarketValue;

    @Schema(example = "21000", description = "Retail market value of car", name = "retailMarketValue", type = "Float")
    private Float retailMarketValue;

    @Schema(name = "images", description = "Images associated with car", nullable = true, type = "List<ImageResponse>")
    private List<ImageResponse> images;

    @Schema(name = "favoritedUsers", description = "Favorited users associated with car", nullable = true, type = "List<UserResponse>")
    private List<UserResponse> favoritedUsers;

    @Schema(example = "1685621520000", description = "Date time field of car creation", name = "createdAt", type = "Long")
    private Long createdAt;

    @Schema(example = "1685621520000", description = "Date time field of car update", name = "updatedAt", type = "Long")
    private Long updatedAt;

    @Schema(name = "location", description = "Location of the car", nullable = true, type = "LocationResponse")
    private LocationResponse location;

    @Schema(example = "EUROPE", description = "Region type of the car", name = "regionType", type = "String")
    private String regionType;

    @Schema(name = "lastMarketValues", description = "Last market values of the car", nullable = true, type = "Map<String, MarketValueResponse>")
    private Map<String, MarketValueResponse> lastMarketValues;

    @Schema(example = "CLEAN", description = "Black Book condition of the car", name = "blackBookCondition", type = "String")
    private String blackBookCondition;

    @Schema(example = "true", description = "Whether the MMR value should be visible", name = "mmrShow")
    private Boolean mmrShow;

    public static CarResponse convert(final Car car) {
        return CarResponse.builder()
            .id(car.getId())
            .title(car.getTitle())
            .content(car.getContent())
            .status(car.getStatus().name())
            .statusMessage(car.getStatusMessage())
            .offers(null)
            .vin(car.getVin())
            .unit(car.getUnit())
            .modelYear(car.getModelYear())
            .carGroup(car.getCarGroup() != null ? CarGroupResponse.convert(car.getCarGroup()) : null)
            .carClass(car.getCarClass() != null ? CarClassResponse.convert(car.getCarClass()) : null)
            .exteriorColor(car.getExteriorColor())
            .interiorColor(car.getInteriorColor())
            .reportLink(car.getReportLink())
            .mileage(car.getMileage())
            .mmr(car.getMmr())
            .mmrShow(car.getMmrShow())
            .equipment(car.getEquipment())
            .condition(car.getCondition())
            .defaultMarketValue(car.getDefaultMarketValue())
            .retailMarketValue(car.getRetailMarketValue())
            .blackBookCondition(car.getBlackBookCondition() != null ? car.getBlackBookCondition().name() : null)
            .images(car.getImages() != null ? car.getImages().stream().map(ImageResponse::convert).toList() : new ArrayList<>())
            .regionType(car.getRegionType() != null ? car.getRegionType().name() : null)
            .createdAt(car.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .updatedAt(car.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .build();
    }

    public static CarResponse convert(final Car car, final boolean showCompany
        , final boolean showCarModel, final boolean showFavoritedUsers, final boolean showOffers
        , final boolean showCarGroup, final boolean showCarClass, final boolean showLocation) {
        final CarResponse response = convert(car);
        if (showCompany)
            response.setCompany(CompanyResponse.convert(car.getCompany(), false, true, false));
        if (showCarModel)
            response.setCarModel(CarModelResponse.convert(car.getModel(), true, true));
        if (showFavoritedUsers)
            response.setFavoritedUsers(car.getFavoritedUsers().stream().map(UserResponse::convert).toList());
        if (showOffers && car.getOffers() != null) {
            response.setOffers(car.getOffers().stream()
                .map(it -> OfferResponse.convert(it, false, true, false))
                .toList());
        } else if (showOffers) {
            response.setOffers(new ArrayList<>());
        }
        if (showCarGroup && car.getCarGroup() != null)
            response.setCarGroup(CarGroupResponse.convert(car.getCarGroup()));
        if (showCarClass && car.getCarClass() != null)
            response.setCarClass(CarClassResponse.convert(car.getCarClass()));
        if (showLocation && car.getLocation() != null)
            response.setLocation(LocationResponse.convert(car.getLocation()));

        return response;
    }

    public static CarResponse convert(final Car car, final boolean showAll) {
        if (showAll)
            return convert(car, true, true, true, true, true, true, true);
        return convert(car);
    }
}
