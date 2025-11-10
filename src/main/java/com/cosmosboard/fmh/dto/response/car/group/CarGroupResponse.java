package com.cosmosboard.fmh.dto.response.car.group;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.entity.CarGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarGroupResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of brand", name = "name", type = "String")
    private String name;

    public static CarGroupResponse convert(final CarGroup carGroup) {
        return CarGroupResponse.builder()
                .id(carGroup.getId())
                .name(carGroup.getName())
                .build();
    }
}
