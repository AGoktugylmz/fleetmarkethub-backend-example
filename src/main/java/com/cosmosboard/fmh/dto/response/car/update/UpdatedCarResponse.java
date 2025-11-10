package com.cosmosboard.fmh.dto.response.car.update;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatedCarResponse extends BaseResponse {
    private String vin;

    private AppConstants.CarStatusEnum oldStatus;

    private AppConstants.CarStatusEnum newStatus;
}
