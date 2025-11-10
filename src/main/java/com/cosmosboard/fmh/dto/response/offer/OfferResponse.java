package com.cosmosboard.fmh.dto.response.offer;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.category.CategoryResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import com.cosmosboard.fmh.dto.response.car.CarResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.Offer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Setter
@Builder
public class OfferResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(name = "car", description = "Car of Offer", nullable = true, type = "carResponse")
    private CarResponse car;

    @Schema(name = "company", description = "company of Offer", nullable = true, type = "CompanyResponse")
    private CompanyResponse company;

    @Schema(name = "category", description = "category of Offer", nullable = true, type = "CategoryResponse")
    private CategoryResponse category;

    @Schema(name = "price", description = "price of Offer", nullable = true, type = "Double")
    private BigDecimal price;

    @Schema(example = "2022-09-29T22:37:31", description = "transactionAt of Offer", name = "transactionAt", type = "Long")
    private Long transactionAt;

    @Schema(example = "2022-09-29T22:37:31", description = "cancelAt of Offer", name = "cancelAt", type = "Long")
    private Long cancelAt;

    @Schema(example = "WAITING", description = "Status of Offer", name = "status", type = "String")
    private String status;

    @Schema(name = "updatedBy", description = "updatedBy of Offer", nullable = true, type = "UpdatedUserResponse")
    private UserResponse updatedBy;

    @Schema(name = "employee", description = "employee of the Offer", nullable = true, type = "UpdatedUserResponse")
    private UserResponse employee;

    @ArraySchema(schema = @Schema(example = "started", description = "updates from employee", required = false, name = "updates", type = "String"))
    private List<String> updates;

    public static OfferResponse convert(final Offer offer) {
        return OfferResponse.builder()
                .id(offer.getId())
                .price(offer.getPrice())
                .status(offer.getStatus().name())
                .updates(offer.getUpdates())
                .transactionAt(offer.getTransactionAt() != null ? offer.getTransactionAt().toInstant(ZoneOffset.UTC).toEpochMilli(): null)
                .cancelAt(offer.getCancelAt() != null ? offer.getCancelAt().toInstant(ZoneOffset.UTC).toEpochMilli(): null)
                .updatedBy(offer.getUpdatedBy() != null ? UserResponse.convertUpdated(offer.getUpdatedBy()) : null)
                .build();
    }

    public static OfferResponse convert(final Offer offer, final boolean showCar, final boolean showCompany,
                                        final boolean showEmployee) {
        final OfferResponse offerResponse = convert(offer);
        if (showCar) {
            offerResponse.setCar(CarResponse.convert(offer.getCar(), true, true,
                    false, false, false, false, false));
        }
        if (showCompany)
            offerResponse.setCompany(CompanyResponse.convert(offer.getCompany()));
        if (showEmployee)
            offerResponse.setEmployee(UserResponse.convertUpdated(offer.getEmployee() != null ? offer.getEmployee().getUser(): null));
        return offerResponse;
    }

    public static OfferResponse convert(final Offer offer, boolean showAll) {
        if (showAll)
            return convert(offer, true, true, true);
        return convert(offer);
    }
}
