package com.cosmosboard.fmh.dto.request.offer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateOfferRequest {

    @NotBlank(message = "{not_blank}")
    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Car id", name = "carId", type = "String")
    private String carId;

    @Digits(integer = 10 /*precision*/, fraction = 2 /*scale*/)
    @Schema(example = "10.5", description = "Price", name = "price", type = "Double")
    private BigDecimal price;

}
