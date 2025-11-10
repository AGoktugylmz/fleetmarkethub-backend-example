package com.cosmosboard.fmh.dto.response.car.cclass;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.entity.CarClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarClassResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of brand", name = "name", type = "String")
    private String name;

    public static CarClassResponse convert(final CarClass carClass) {
        return CarClassResponse.builder()
                .id(carClass.getId())
                .name(carClass.getName())
                .build();
    }
}
