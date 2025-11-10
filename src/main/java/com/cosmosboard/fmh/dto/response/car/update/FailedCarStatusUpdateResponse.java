package com.cosmosboard.fmh.dto.response.car.update;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FailedCarStatusUpdateResponse extends BaseResponse {
    private String vin;

    private String errorMessage;
}
