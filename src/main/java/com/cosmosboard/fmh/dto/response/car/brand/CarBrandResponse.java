package com.cosmosboard.fmh.dto.response.car.brand;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.car.model.CarModelResponse;
import com.cosmosboard.fmh.entity.CarBrand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class CarBrandResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of brand", name = "name", type = "String")
    private String name;

    @Schema(name = "models", description = "Models Associated with brand", nullable = true, type = "List<CarModelResponse>")
    private List<CarModelResponse> models;

    public static CarBrandResponse convert(final CarBrand carBrand) {
        return CarBrandResponse.builder()
                .id(carBrand.getId())
                .name(carBrand.getName())
                .build();
    }

    public static CarBrandResponse convert(final CarBrand carBrand, final boolean showModels) {
        final CarBrandResponse response = convert(carBrand);
        if (showModels)
            response.setModels(carBrand.getModels().stream().map(CarModelResponse::convert).toList());
        return response;
    }
}
