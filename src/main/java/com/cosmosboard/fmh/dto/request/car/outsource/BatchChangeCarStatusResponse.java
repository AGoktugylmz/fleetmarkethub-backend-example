package com.cosmosboard.fmh.dto.request.car.outsource;

import com.cosmosboard.fmh.dto.response.car.CarResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchChangeCarStatusResponse {
    private List<CarResponse> successes;

    private List<FailedCarStatusResponse> failures;
}