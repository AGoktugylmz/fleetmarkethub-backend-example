package com.cosmosboard.fmh.dto.response.car.update;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchCarStatusUpdateResponse extends BaseResponse {
    private List<UpdatedCarResponse> successfulCars;

    private List<FailedCarStatusUpdateResponse> failedCars;
}
