package com.cosmosboard.fmh.dto.request.car;

import com.cosmosboard.fmh.dto.response.car.FailedCarResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ParseExcelResult {
    private List<CreateCarRequestExcel> successfulCars;

    private List<FailedCarResponse> failedCars;
}
