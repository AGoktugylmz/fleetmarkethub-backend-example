package com.cosmosboard.fmh.dto.response.car;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchCarResponse extends BaseResponse {
    private List<CarResponse> successfulCars;

    private List<FailedCarResponse> failedCars;
}

