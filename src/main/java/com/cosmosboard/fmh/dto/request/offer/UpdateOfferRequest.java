package com.cosmosboard.fmh.dto.request.offer;

import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateOfferRequest {

    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Car id", name = "carId", type = "String")
    private String carId;

    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Company id", name = "companyId",
            type = "String")
    private String companyId;

    @Schema(example = "10.5", description = "Price", name = "price",
            type = "Double")
    private BigDecimal price;

    @Schema(example = "ACCEPTED", description = "Status", name = "status", type = "String")
    private AppConstants.OfferStatusEnum status = AppConstants.OfferStatusEnum.WAITING;
}
