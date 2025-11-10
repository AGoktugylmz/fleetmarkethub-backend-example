package com.cosmosboard.fmh.dto.response.car.model;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.car.brand.CarBrandResponse;
import com.cosmosboard.fmh.dto.response.car.trim.CarModelTrimResponse;
import com.cosmosboard.fmh.entity.CarModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class CarModelResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of brand", name = "name", type = "String")
    private String name;

    @Schema(description = "brand of model", name = "name", type = "CarBrandResponse")
    private CarBrandResponse brand;

    @Schema(name = "modelTrims", description = "Trim Models associated with model", nullable = true, type = "List<CarModelTrimResponse>")
    private List<CarModelTrimResponse> modelTrims;

    public static CarModelResponse convert(final CarModel carModel) {
        return CarModelResponse.builder()
                .id(carModel.getId())
                .name(carModel.getName())
                .build();
    }

    public static CarModelResponse convert(final CarModel carModel, final boolean showBrand,
                                           final boolean showModelTrims) {
        final CarModelResponse response = convert(carModel);
        if (showBrand)
            response.setBrand(CarBrandResponse.convert(carModel.getBrand()));
        if (showModelTrims)
            response.setModelTrims(carModel.getModelTrims().stream().map(CarModelTrimResponse::convert).toList());
        return response;
    }
}
