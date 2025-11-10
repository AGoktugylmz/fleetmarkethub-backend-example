package com.cosmosboard.fmh.dto.response.car;

import com.cosmosboard.fmh.dto.request.car.CreateCarRequestExcel;
import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FailedCarResponse extends BaseResponse {
    private CreateCarRequestExcel request;

    private String errorMessage;
}
