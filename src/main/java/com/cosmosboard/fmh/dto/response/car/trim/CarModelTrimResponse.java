package com.cosmosboard.fmh.dto.response.car.trim;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.car.model.CarModelResponse;
import com.cosmosboard.fmh.entity.CarModelTrim;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarModelTrimResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of brand", name = "name", type = "String")
    private String name;

    @Schema(description = "brand of model", name = "name", type = "CarBrandResponse")
    private CarModelResponse model;

    public static CarModelTrimResponse convert(final CarModelTrim carModelTrim) {
        return CarModelTrimResponse.builder()
                .id(carModelTrim.getId())
                .name(carModelTrim.getName())
                .build();
    }

    public static CarModelTrimResponse convert(final CarModelTrim carModelTrim, final boolean showModel) {
        final CarModelTrimResponse response = convert(carModelTrim);
        if (showModel)
            response.setModel(CarModelResponse.convert(carModelTrim.getModel()));
        return response;
    }
}
