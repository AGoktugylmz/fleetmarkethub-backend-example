package com.cosmosboard.fmh.dto.response.car;

import com.cosmosboard.fmh.dto.request.car.CreateCarRequestExcel;
import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchCarWithExcelResponse extends BaseResponse {
    private List<CreateCarRequestExcel> successfulCars;

    private List<FailedCarResponse> failedCars;
}

